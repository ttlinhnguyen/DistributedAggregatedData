package client;

import clock.LamportClock;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.Request;
import rest.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GETClient {
    LamportClock clock;
    final Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;

    /**
     * Creates a GET client connected to a host with a specified port.
     * @param hostname the hostname of the server
     * @param port the port number of the server
     */
    public GETClient(String hostname, int port) throws IOException {
        clock = new LamportClock();
        socket = new Socket(hostname, port);
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Sends a GET request to the server to get the weather data and print out the response.
     */
    public void getData() {
        try {
            outStream.writeObject(new Request("GET", clock.get(), null));
            Response res = (Response) inStream.readObject();
            clock.update(res.clockTime);
            System.out.println("GET " + res.status);
//            System.out.println(res.body);
            displayData(new JSONArray(res.body));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void displayData(JSONArray arr) {
        for (int i=0; i<arr.length(); i++) {
            JSONObject item = arr.getJSONObject(i);
            for (String key : item.keySet()) {
                System.out.format("%20s │ %s%n", key, item.get(key));
            }
            System.out.println();
        }
    }

    /**
     * It will take the URL to the server as an argument with the format of
     * hostname:port.
     * If not provided, it'll be set to localhost:4567
     * @param args the server URL in the form of hostname:port
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String hostname = "localhost";
        int port = 4567;
        if (args.length>0) {
            String[] path = args[0].split(":", 2);
            hostname = path[0];
            port = Integer.parseInt(path[1]);
        }
        GETClient client = new GETClient(hostname, port);
        client.getData();
    }
}
