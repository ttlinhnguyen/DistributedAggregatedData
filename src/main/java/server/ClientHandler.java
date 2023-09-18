package server;

import rest.Request;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.PriorityBlockingQueue;

class ClientHandler implements Runnable{
    private Socket socket;
    private PriorityBlockingQueue<RequestNode> requestQueue;

    public ClientHandler(Socket socket, Listener listener) {
        this.socket = socket;
        this.requestQueue = listener.getRequestQueue();
    }

    @Override
    public void run() {
        ObjectInputStream inputStream;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            Request req = (Request) inputStream.readObject();
            RequestNode reqNode = new RequestNode(socket, req);
            requestQueue.add(reqNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
