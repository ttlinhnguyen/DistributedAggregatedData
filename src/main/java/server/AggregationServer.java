package server;

import clock.LamportClock;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.PriorityBlockingQueue;

public class AggregationServer implements Runnable {
    private boolean running = true;
    LamportClock clock;
    final ServerSocket server;
    private PriorityBlockingQueue<RequestNode> requestQueue;
    private Storage storage;

    private Listener listener;

    /**
     * Creates an Aggregation Server bound to a specified port.<br>
     * The current maximum timeout for its server socket is 10 seconds
     * and each client socket is 5 seconds.
     * @param port the port number
     */
    public AggregationServer(int port) throws IOException {
        clock = new LamportClock();
        requestQueue = new PriorityBlockingQueue<>(11, new RequestComparator());
        server = new ServerSocket(port);
        storage = new Storage(this, "src/main/java/server/weather.json");

    }

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

//    @Override
    public void run() {
        System.out.println("starting server");
        startListener();
        while (running) {
            if (!requestQueue.isEmpty()) startHandlingRequest(requestQueue.poll());
        }
    }
    public void stop() {
        listener.stop();
        running = false;
    }

    private void startListener() {
        listener = new Listener(this);
        Thread t = new Thread(listener);
        t.start();
    }

    private void startHandlingRequest(RequestNode reqNode) {
        RequestHandler handler = new RequestHandler(reqNode, this);
        handler.run();
    }

    public PriorityBlockingQueue<RequestNode> getRequestQueue() { return requestQueue; }
    public ServerSocket getServerSocket() { return server; }
    public Storage getStorage() { return storage; }
    public boolean isRunning() { return running; }
}