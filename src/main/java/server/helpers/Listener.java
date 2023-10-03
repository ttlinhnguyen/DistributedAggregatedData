package server.helpers;

import server.AggregationServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.PriorityBlockingQueue;

public class Listener implements Runnable {
    private boolean running;
    private AggregationServer server;

    /**
     * Listens to the incoming clients and creates a `ClientHandler` thread
     * corresponding to that client.
     */

    public Listener (AggregationServer server) {
        running = true;
        this.server = server;
    }
    @Override
    public void run() {
        try {
            while (running) {
                Socket client = server.getServerSocket().accept();
                Thread clientThread = new Thread(new ClientHandler(client, server));
                clientThread.start();
            }
        } catch (IOException e) {}
    }

    public void stop() { running = false; }

}
