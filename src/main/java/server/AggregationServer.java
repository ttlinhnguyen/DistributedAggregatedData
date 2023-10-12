package server;

import clock.LamportClock;
import server.helpers.*;

import java.io.*;
import java.net.ServerSocket;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

public class AggregationServer implements Runnable {
    private int port;
    private boolean running = true;
    private LamportClock clock;
    private ServerSocket server;
    private PriorityBlockingQueue<RequestNode> requestQueue;
    private Semaphore fileLock;
    private Storage storage;
    public String replicaPath;

    private Listener listener;

    /**
     * Creates an Aggregation Server bound to a specified port.<br>
     * The current maximum timeout for its server socket is 10 seconds
     * and each client socket is 5 seconds.
     * @param port the port number
     */
    public AggregationServer(int port) throws IOException {
        this.port = port;
        clock = new LamportClock();
        fileLock = new Semaphore(1, true);
        requestQueue = new PriorityBlockingQueue<>(11, Comparator.comparingLong(RequestNode::getPriority));
        replicaPath = "src/main/java/server/replica.json";
    }

    /**
     * Bounds the socket to the specified port. Runs a {@code Listener} and handles oncoming requests.
     */
    public void run() {
        try {
            server = new ServerSocket(port);
            storage = new Storage(this, "src/main/java/server/weather.json");
            startListener();
            while (running) {
                if (!requestQueue.isEmpty()) startHandlingRequest(requestQueue.poll());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the server socket and other helper threads.
     */
    public void stop() {
        try {
            server.close();
            listener.stop();
            running = false;
        } catch (Exception e) {}
    }

    /**
     * Starts a {@Listener} to listens to client sockets.
     */
    private void startListener() {
        listener = new Listener(this);
        Thread t = new Thread(listener);
        t.start();
    }

    /**
     * Handles the request in the queue.
     */
    private void startHandlingRequest(RequestNode reqNode) {
        RequestHandler handler = new RequestHandler(reqNode, this);
        handler.run();
    }

    public PriorityBlockingQueue<RequestNode> getRequestQueue() { return requestQueue; }
    public ServerSocket getServerSocket() { return server; }
    public Storage getStorage() { return storage; }
    public boolean isRunning() { return running; }
    public LamportClock getClock() { return clock; }
    public Semaphore getFileLock() { return fileLock; }
//    public Replica getReplica() { return replica; }
    public void stopListener() { listener.stop(); }
    public void removeAllData() throws InterruptedException { storage.removeAllData(); }

    /**
     * The command line arguments take one port number. If not provided, then the port
     * will be set to 4567.
     * @param args port number
     */
    public static void main(String[] args) {
        int port = 4567;
        if (args.length>0) port = Integer.parseInt(args[0]);
        try {
            Thread server = new Thread(new AggregationServer(port));
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}