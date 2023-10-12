package client.content;

import client.AbstractClient;
import org.json.JSONObject;
import rest.Request;
import rest.Response;

import java.io.*;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.UUID;

public class ContentServer extends AbstractClient implements Runnable {
    String dir = "src/main/java/client/content";
    String id;
    String body;

    /**
     * Constructor for the content server.
     * To connect to the server, run {@code connect()}. To send request to the server,
     * call {@code readInput(filename)} to load up the data first, then call
     * {@code requestAndResponse()} to send the request and receive the response from the server.
     * @param hostname the hostname of the server
     * @param port the port number of the server
     */
    public ContentServer(String hostname, int port) {
        super(hostname, port);
        id = "Content-" + UUID.randomUUID();
        body = "";
    }

    @Override
    public void run() {
        try {
            connect();
            requestAndResponse();
        } catch (Exception e) {}
    }

    public Request createRequest() {
        return createRequestHelper("PUT", body);
    }

    /**
     * Sends a PUT request to the server with the new data to be added.
     */
    protected void showResponse() throws IOException, ClassNotFoundException {
        Response res = getResponse();
        System.out.println("PUT " + res.status);
    }

    /**
     * Transforms the input file to a JSONObject.
     * @param filename the path to the input file
     */
    public void readInput(String filename) throws FileNotFoundException {
        if (filename==null || filename.isEmpty()) {
            body = "";
            return;
        }

        File f = new File(dir+"/"+ filename);
        JSONObject input = new JSONObject();
        Scanner scanner = new Scanner(f);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            String[] attr = s.split(": ", 2);
            input.put(attr[0], attr[1]);
        }
        scanner.close();
        input.put("id", id);
        input.put("timestamp", new Timestamp(System.currentTimeMillis()));
        body = !input.isEmpty() ? input.toString() : "";
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

            String inputFilePath = args.length>1 ? args[1] : "" ; // example: data1.txt

            ContentServer contentServer = new ContentServer(hostname, port);
            contentServer.readInput(inputFilePath);
            contentServer.run();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("ERROR: Bad arguments.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
