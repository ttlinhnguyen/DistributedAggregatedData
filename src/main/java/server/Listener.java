package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.PriorityBlockingQueue;

class Listener implements Runnable {
    private boolean running;
    private ServerSocket server;
    private PriorityBlockingQueue<RequestNode> requestQueue;

    public Listener (AggregationServer server) {
        running = true;
        this.server = server.getServerSocket();
        this.requestQueue = server.getRequestQueue();
    }
    @Override
    public void run() {
        try {
            server.setSoTimeout(5 * 1000);
            while (running) {
                Socket client = server.accept();
                Thread clientThread = new Thread(new ClientHandler(client, this));
                clientThread.start();
            }
        } catch (SocketTimeoutException e) {
            System.out.println("closing server socket");
            stop();
        } catch (IOException e) {}
    }

    public void stop() { running = false; }

    public PriorityBlockingQueue<RequestNode> getRequestQueue() { return requestQueue; }
}
