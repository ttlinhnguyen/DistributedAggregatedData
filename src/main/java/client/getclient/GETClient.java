package client.getclient;

import client.AbstractClient;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.Request;
import rest.Response;

import java.io.IOException;
import java.util.Iterator;

public class GETClient extends AbstractClient implements Runnable {

    /**
     * Creates a GET client connected to a host with a specified port.
     * @param hostname the hostname of the server
     * @param port the port number of the server
     */
    public GETClient(String hostname, int port) throws IOException, InterruptedException {
        super(hostname, port);
    }

    @Override
    public void run() {
        getData();
    }

    /**
     * Sends a GET request to the server to get the weather data and print out the response.
     */
    public void getData() {
        try {
            sendRequest(new Request("GET", clock.get(), null));

            Response res = getResponse();
            System.out.println("GET " + res.status);
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
            Thread client = new Thread(new GETClient(hostname, port));
            client.start();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("ERROR: Bad arguments.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
