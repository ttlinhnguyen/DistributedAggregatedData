package client;

import clock.LamportClock;
import rest.HttpParser;
import rest.Request;
import rest.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public abstract class AbstractClient {
    protected String hostname;
    protected int port;
    protected LamportClock clock;
    protected Socket socket;
    protected ObjectOutputStream outStream;
    protected ObjectInputStream inStream;

    /**
     * Abstract class for clients. Used for GETClient and ContentServer.
     * @param hostname the hostname of the server that the client connects to
     * @param port the port of the server
     */
    public AbstractClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        clock = new LamportClock();
    }

    /**
     * Connects to the server with the specified hostname and port upon initialization.
     * Retry 3 times if it cannot connect to the server.
     */
    public void connect() throws IOException, InterruptedException {
        int connectTry = 0;
        while (connectTry<=4) {
            try {
                if (connectTry!=0) Thread.sleep(1000);
                socket = new Socket(hostname, port);
                break;
            } catch (Exception e) {
                if (++connectTry==4) {
                    System.err.println("ERR: Server does not exist.");
                    throw e;
                }
                else System.out.println("Cannot connect. Re-connecting...");
            }
        }
    }

    /**
     * Creates a request and sends it to the server. Waits for the request then shows it.
     * Retry 3 times if the server is down.
     * @throws IOException I/O exception when the socket is not connected.
     */
    public void requestAndResponse() throws IOException {
        Request req = createRequest();
        int connectTry = 0;
        while (connectTry<=4) {
            try {
                if (connectTry!=0) Thread.sleep(1000);
                sendRequest(req);
                showResponse();
                break;
            } catch (IOException e) {
                if (++connectTry==4) {
                    System.err.println("ERR: Server does not exist.");
                    throw e;
                }
                else System.out.println("Cannot connect. Re-connecting...");
            } catch (Exception e) {}
        }
    }

    /**
     * Helper function for the {@code createRequest()} method.
     * @param method i.e. GET, PUT.
     * @param body The body of the HTTP message.
     * @return
     */
    protected Request createRequestHelper(String method, String body) {
        Request req = new Request(method);
        try {
            req.addHeader("Host", InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {}
        req.addHeader("User-Agent", getClass().getSimpleName());
        req.addHeader("Accept", "application/json");
        req.addHeader("Server-Timing", Integer.toString(clock.get()));
        req.addHeader("Content-Length", Integer.toString(body.length()));
        req.setBody(body);
        return req;
    }

    /**
     * Create a customised request.
     * @return a Request object to be passed to the server
     */
    public abstract Request createRequest();


    /**
     * Displays the response from the server.
     */
    protected abstract void showResponse() throws IOException, ClassNotFoundException;

    /**
     * Sends the request to the server.
     * @param req Request of the client.
     * @throws IOException I/O Exception when the socket or the output stream is not connected.
     */
    public void sendRequest(Request req) throws IOException {
        clock.increment();
        String reqHttpString = HttpParser.createRequest(req);
        outStream = new ObjectOutputStream(socket.getOutputStream());
        outStream.writeObject(reqHttpString);
    }

    /**
     * Waits for a response from the server which times out in 10 seconds.
     * @return A Response from the server
     * @throws IOException I/O Exception if the socket is not connected.
     */
    public Response getResponse() throws IOException, ClassNotFoundException {
        socket.setSoTimeout(10*1000);
        try {
            inStream = new ObjectInputStream(socket.getInputStream());
            String resHttpString = (String) inStream.readObject();
            Response res = HttpParser.parseResponse(resHttpString);
            clock.update(Integer.parseInt(res.headers.get("Server-Timing")));
            socket.setSoTimeout(0);
            return res;
        } catch (SocketTimeoutException e) {
            return new Response(500);
        } catch (SocketException e) {
            System.err.println("ERR: Server is crashed.");
            return new Response(500);
        }
    }

    /**
     * Close the client's socket.
     */
    public void stop() {
        try {
            socket.close();
        } catch (Exception e) {}
    }
}
