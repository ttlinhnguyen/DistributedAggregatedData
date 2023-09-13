package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class Listener implements Runnable {
    private boolean running;
    private ServerSocket server;
    private LinkedBlockingQueue<RequestNode> queue;

    public Listener (AggregationServer server) {
        running = true;
        this.server = server.getServerSocket();
        this.queue = server.getQueue();
    }
    @Override
    public void run() {
        try {
            while (running) {
                Socket client = server.accept();
                Thread clientThread = new Thread(new ClientHandler(client, this));
                clientThread.start();
            }
        } catch (IOException e) {

        }
    }

    public void stop() { running = false; }

    public LinkedBlockingQueue<RequestNode> getQueue() { return queue; }
}
