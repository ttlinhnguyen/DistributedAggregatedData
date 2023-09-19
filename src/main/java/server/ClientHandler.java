package server;

import rest.HttpParser;
import rest.Request;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.PriorityBlockingQueue;

class ClientHandler implements Runnable{
    private Socket socket;
    private PriorityBlockingQueue<RequestNode> requestQueue;
    private HttpParser httpParser;

    public ClientHandler(Socket socket, Listener listener) {
        this.socket = socket;
        this.requestQueue = listener.getRequestQueue();
        httpParser = new HttpParser();
    }

    @Override
    public void run() {
        ObjectInputStream inputStream;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            String reqHttpString = (String) inputStream.readObject();
            Request req = httpParser.parseRequest(reqHttpString);
            RequestNode reqNode = new RequestNode(socket, req);
            requestQueue.add(reqNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
