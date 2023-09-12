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
import java.util.Iterator;

public class GETClient {
    LamportClock clock;
    Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;

    /**
     * Creates a GET client connected to a host with a specified port.
     * @param hostname the hostname of the server
     * @param port the port number of the server
     */
    public GETClient(String hostname, int port) throws IOException, InterruptedException {
        clock = new LamportClock();

        int connectTry = 0;
        while (connectTry<=4) {
            try {
                if (connectTry!=0) Thread.sleep(1000);
                socket = new Socket(hostname, port);
                break;
            } catch (Exception e) {
                if (++connectTry==4) throw e;
                else System.out.println("Cannot connect. Re-connecting...");
            }
        }
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

            displayData(new JSONObject(res.body));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void displayData(JSONObject obj) {
        Iterator<String> it = obj.keys();
        while (it.hasNext()) {
            JSONArray arr = obj.getJSONArray(it.next());
            for (int i=0; i< arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                for (String key : item.keySet()) {
                    System.out.format("%20s │ %s%n", key, item.get(key));
                }
            }
            System.out.println();
        }
    }

    /**
     * It will take the URL to the server as an argument with the format of
     * hostname:port.
     * @param args the server URL in the form of hostname:port
     */
    public static void main(String[] args) {
        try {
            String[] path = args[0].split(":", 2);
            String hostname = path[0];
            int port = Integer.parseInt(path[1]);
            GETClient client = new GETClient(hostname, port);
            client.getData();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("ERROR: Bad arguments.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
