package server;

import rest.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler implements Runnable{
    private Socket socket;
    private LinkedBlockingQueue<RequestNode> queue;

    public ClientHandler(Socket socket, Listener listener) {
        this.socket = socket;
        this.queue = listener.getQueue();
    }

    @Override
    public void run() {
        ObjectInputStream inputStream;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            try {
                Request req = (Request) inputStream.readObject();
                RequestNode reqNode = new RequestNode(socket, req);
                queue.add(reqNode);
            } catch (Exception e) {}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
