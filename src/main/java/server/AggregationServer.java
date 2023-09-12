package server;

import clock.LamportClock;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.Request;
import rest.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.UUID;

public class AggregationServer extends Thread {
    String dbPath;
    LamportClock clock;
    final private ServerSocket server;
    private JSONObject data;
    LinkedList<RequestNode> queue;
    public static class RequestNode {
        String clientId;
        public ObjectOutputStream out;
        public Request request;
        public RequestNode(String clientId, ObjectOutputStream out, Request req) {
            this.clientId = clientId;
            this.out = out;
            this.request = req;
        }
    };

    /**
     * Creates an Aggregation Server bound to a specified port.<br>
     * The current maximum timeout for its server socket is 10 seconds
     * and each client socket is 5 seconds.
     * @param port the port number
     */
    public AggregationServer(int port) throws IOException {
        clock = new LamportClock();
        queue = new LinkedList<>();
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
        System.out.println("Server is connected to port " + port);
        try {
            AggregationServer server = new AggregationServer(port);
            server.start();
        } catch (Exception e) {}
    }

    @Override
    public void run() {
        Thread requestThread = new Thread(this::handleRequest);
        requestThread.start();
        try {
            server.setSoTimeout(10 * 1000);
            while (true) {
                Socket client = server.accept();
                Thread clientThread = new Thread(() -> handleClient(client));
                clientThread.start();
            }
        } catch (SocketTimeoutException e) {
            try {
                System.out.println("Server is closing");
                server.close();
                requestThread.stop();
            } catch (Exception err) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleRequest() {
        while (true) {
            try {
                if (!queue.isEmpty()) {
                    RequestNode reqNode = queue.poll();
                    ObjectOutputStream outStream = reqNode.out;
                    Request req = reqNode.request;
                    Response res;
                    if (req.method.equals("GET")) {
                        String data = getWeatherData();
                        res = new Response(200, clock.get(), data);
                    } else if (req.method.equals("PUT")) {
                        if (req.body.isEmpty()) res = new Response(204, clock.get(), null);
                        else {
                            try {
                                JSONObject newObj = new JSONObject(req.body);
//                                clientId = newObj.getString("id");
                                putWeatherData(reqNode.clientId, newObj, req.clockTime);
                                res = new Response(200, clock.get(), null);
                            } catch (JSONException e) {
                                res = new Response(500, clock.get(), null);
                            }
                        }
                    } else res = new Response(400, clock.get(), null);

                    outStream.writeObject(res);
                    System.out.println("server clock " + clock.get());
                } else Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Returns the weather data stored in the server.
     * @return the data to be returned
    */
    private String getWeatherData() {
        clock.increment();
        return data.toString();
    }

    /**
     * Updates the weather data stored in the server.
     * @param obj the new data to be added
     * @param clockTime the LamportClock timestamp from the client
     */
    private void putWeatherData(String clientId, JSONObject obj, int clockTime) {
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

    /**
     * Listens to the client socket's input stream for requests
     * and write the responses to the output stream.
     * @param socket the client socket
     */
    private void handleClient(Socket socket) {
        try {
            boolean isContent = false;
            String clientId = UUID.randomUUID().toString();
            long start = System.currentTimeMillis();
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

            while (System.currentTimeMillis() < start + 3 * 1000) {
                try {
                    Request req = (Request) inStream.readObject();
                    start = System.currentTimeMillis();
                    if (req.method.equals("PUT")) isContent = true;
                    synchronized (this) {
                        RequestNode reqNode = new RequestNode(clientId, outStream, req);
                        queue.add(reqNode);
                    }
                } catch (Exception e) {}
            }
            socket.close();
            if (isContent) {
                removeWeatherData(clientId);
                System.out.println("Server removed content from " + clientId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


