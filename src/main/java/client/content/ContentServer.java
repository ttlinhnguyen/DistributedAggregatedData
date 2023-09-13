package client.content;

import client.AbstractClient;
import org.json.JSONObject;
import rest.Request;
import rest.Response;

import java.io.*;
import java.util.Scanner;
import java.util.UUID;

public class ContentServer extends AbstractClient implements Runnable {
    String id;
    JSONObject input;
    /**
     * Creates a content server connected to a host with a specified port.
     * @param hostname the hostname of the server
     * @param port the port number of the server
     */
    public ContentServer(String hostname, int port) throws IOException, InterruptedException {
        super(hostname, port);
        id = "Content-" + UUID.randomUUID();
    }

    @Override
    public void run() {
        putData();
    }

    /**
     * Sends a PUT request to the server with the new data to be added.
     */
    public void putData() {
        try {

            sendRequest(new Request("PUT", clock.get(), input.toString()));
            Response res = getResponse();
            System.out.println("PUT " + res.status);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Transforms the input file to a JSONObject.
     * @param filename the path to the input file
     */
    public void readInput(String filename) throws FileNotFoundException, NullPointerException {
        File f = new File(filename);
        input = new JSONObject();
        Scanner scanner = new Scanner(f);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            String[] attr = s.split(": ", 2);
            input.put(attr[0], attr[1]);
        }
        scanner.close();
        input.put("id", id);
    }

    /**
     * It will take the URL to the server and the input path file as arguments.
     * The format of the server URL is hostname:port.
     * @param args the server URL in the form of hostname:port & the path to the input file
     */
    public static void main(String[] args) {
        try {
            String[] path = args[0].split(":", 2);
            String hostname = path[0];
            int port = Integer.parseInt(path[1]);

            String inputFilePath = args[1]; // example: src/main/java/content/data1.txt

            Thread client = new Thread(new ContentServer(hostname, port));
            client.start();

        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("ERROR: Bad arguments.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
