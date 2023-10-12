package server.helpers;

import rest.HttpParser;
import rest.Request;
import server.AggregationServer;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.PriorityBlockingQueue;

public class ClientHandler implements Runnable{
    String reqHttpString;
    private String cliendId;
    private Socket socket;
    private PriorityBlockingQueue<RequestNode> requestQueue;
    private HttpParser httpParser;
    private AggregationServer server;

    /**
     * Constantly listens to the input stream from the client socket
     * until the socket is timeout. Remove the content after the timeout.
     */
    public ClientHandler(Socket socket, AggregationServer server) {
        this.socket = socket;
        this.requestQueue = server.getRequestQueue();
        httpParser = new HttpParser();
        this.server = server;
        cliendId = "";
    }

    /**
     * While the socket is not timeout, add the request to the server's request queue.
     */
    @Override
    public void run() {
        ObjectInputStream inputStream;
        try {
            socket.setSoTimeout(30 * 1000);
            while (server.isRunning()) {
                inputStream = new ObjectInputStream(socket.getInputStream());
                reqHttpString = (String) inputStream.readObject();
                Request req = httpParser.parseRequest(reqHttpString);
                RequestNode reqNode = new RequestNode(socket, req);
                requestQueue.add(reqNode);
                cliendId = req.headers.get("Client-Id");
            }
        } catch (SocketTimeoutException e) {
            try {
                server.getStorage().removeWeatherData(cliendId);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (EOFException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
