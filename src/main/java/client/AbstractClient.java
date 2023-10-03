package client;

import clock.LamportClock;
import rest.HttpParser;
import rest.Request;
import rest.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class AbstractClient {
    protected String hostname;
    protected int port;
    protected LamportClock clock;
    protected Socket socket;
    protected ObjectOutputStream outStream;
    protected ObjectInputStream inStream;

    public AbstractClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        clock = new LamportClock();
    }

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

    protected void sendRequest(Request req) throws IOException {
        clock.increment();
        String reqHttpString = HttpParser.createRequest(req);
        outStream = new ObjectOutputStream(socket.getOutputStream());
        outStream.writeObject(reqHttpString);
    }

    protected Response getResponse() throws IOException, ClassNotFoundException {
        inStream = new ObjectInputStream(socket.getInputStream());
        String resHttpString = (String) inStream.readObject();
        Response res = HttpParser.parseResponse(resHttpString);
        clock.update(Integer.parseInt(res.headers.get("Server-Timing")));
        return res;
    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() { return socket; }
}
