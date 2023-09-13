package server;

import clock.LamportClock;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Scanner;

public class AggregationServer implements Runnable {
    private boolean running = true;
    String dbPath;
    LamportClock clock;
    final ServerSocket server;
    private JSONObject data;
    private LinkedBlockingQueue<RequestNode> queue;
    private Listener listener;

    /**
     * Creates an Aggregation Server bound to a specified port.<br>
     * The current maximum timeout for its server socket is 10 seconds
     * and each client socket is 5 seconds.
     * @param port the port number
     */
    public AggregationServer(int port) throws IOException {
        clock = new LamportClock();
        queue = new LinkedBlockingQueue<>();
        server = new ServerSocket(port);
        dbPath = "src/main/java/server/weather.json";

        updateLocalData();
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
            if (!queue.isEmpty()) startHandlingRequest(queue.poll());
        }
    }
    public void stop() {
        listener.stop();
    }

    private void startListener() {
        listener = new Listener(this);
        Thread t = new Thread(listener);
        t.start();
    }

    private void startHandlingRequest(RequestNode reqNode) {
        Thread t = new Thread(new RequestHandler(reqNode, this));
        t.start();
    }

    public String getWeatherData() {
        clock.increment();
        return data.toString();
    }

    /**
     * Updates the weather data stored in the server.
     * @param obj the new data to be added
     * @param clockTime the LamportClock timestamp from the client
     */
    public void putWeatherData(JSONObject obj, int clockTime) {
        String clientId = obj.getString("id");
        clock.update(clockTime);
        if (!data.has(clientId)) data.put(clientId, new JSONArray());
        data.getJSONArray(clientId).put(obj);
        updateDbFile();
    }

    private void removeWeatherData(String id) {
        if (!id.isEmpty() && data.has(id)) {
            data.remove(id);
            updateDbFile();
        }
    }

    private void updateDbFile() {
        try {
            FileWriter writer = new FileWriter(dbPath);
            writer.write(data.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLocalData() {
        try {
            File db = new File(dbPath);
            db.createNewFile();
            Scanner scanner = new Scanner(db);
            String dbText = "";
            if (scanner.hasNextLine()) {
                dbText += scanner.nextLine();
            }
            scanner.close();
            if (dbText.isEmpty()) data = new JSONObject();
            else data = new JSONObject(dbText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedBlockingQueue<RequestNode> getQueue() { return queue; }
    public ServerSocket getServerSocket() { return server; }
    public boolean isRunning() { return running; }
}