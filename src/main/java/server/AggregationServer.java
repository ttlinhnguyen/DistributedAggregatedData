package server;

import clock.LamportClock;
import org.json.JSONArray;
import rest.Request;
import rest.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AggregationServer extends Thread {
    LamportClock clock;
    final private ServerSocket server;
    private JSONArray data;

    /**
     * Creates an Aggregation Server bound to a specified port.<br>
     * The current maximum timeout for its server socket is 10 seconds
     * and each client socket is 5 seconds.
     * @param port the port number
     */
    public AggregationServer(int port) throws IOException {
        clock = new LamportClock();
        server = new ServerSocket(port);
        data = new JSONArray();
    }

    public static void main(String[] args) throws IOException {
        AggregationServer server = new AggregationServer(4567);
        server.start();
    }

    @Override
    public void run() {
        try {
            server.setSoTimeout(10 * 1000);
            while (true) {
                Socket client = server.accept();
                Thread clientThread = new Thread(() -> handleClient(client));
                clientThread.start();
            }
        } catch (SocketTimeoutException e) {
            try {
                server.close();
            } catch (Exception err) {}
        } catch (Exception e) {
            e.printStackTrace();
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
     * @param newData the new data to be added
     * @param clockTime the LamportClock timestamp from the client
     */
    private void putWeatherData(String newData, int clockTime) {
        clock.update(clockTime);
        JSONArray newArray = new JSONArray(newData);
        for (int i=0; i<newArray.length(); i++) {
            data.put(newArray.get(i));
        }
    }

    /**
     * Listens to the client socket's input stream for requests
     * and write the responses to the output stream.
     * @param socket the client socket
     */
    private void handleClient(Socket socket) {
        try {
            socket.setSoTimeout(3 * 1000);
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Request req = (Request) inStream.readObject();
                if (req.method.equals("GET")) {
                    outStream.writeObject(new Response(200, clock.get(), getWeatherData()));
                } else if (req.method.equals("PUT")) {
                    putWeatherData(req.body, req.clockTime);
                    outStream.writeObject(new Response(200, clock.get(), ""));
                } else {
                    outStream.writeObject(new Response(500, clock.get(), ""));
                }
                System.out.println("server clock " + clock.get());
            }
        } catch (SocketTimeoutException e) {
            try {
                socket.close();
            } catch (IOException errSocket) {}
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
