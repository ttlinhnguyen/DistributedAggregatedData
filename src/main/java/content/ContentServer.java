package content;

import clock.LamportClock;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.Request;
import rest.Response;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

public class ContentServer {
    String id;
    LamportClock clock;
    Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    /**
     * Creates a content server connected to a host with a specified port.
     * @param hostname the hostname of the server
     * @param port the port number of the server
     */
    public ContentServer(String hostname, int port) throws IOException, InterruptedException {
        id = "Content-" + UUID.randomUUID();
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
     * Sends a PUT request to the server with the new data to be added.
     * @param data the data to be sent to the server
     */
    public void putData(JSONObject data) {
        clock.increment();
        try {
            outStream.writeObject(new Request("PUT", clock.get(), data.toString()));
            Response res = (Response) inStream.readObject();
            System.out.println("PUT " + res.status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Transforms the input file to a JSONObject.
     * @param filename the path to the input file
     */
    public JSONObject readInput(String filename) throws FileNotFoundException, NullPointerException {
        File f = new File(filename);
        JSONObject json = new JSONObject();
        Scanner scanner = new Scanner(f);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            String[] attr = s.split(": ", 2);
            json.put(attr[0], attr[1]);
        }
        scanner.close();
        json.put("id", id);
        return json;
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

            ContentServer client = new ContentServer(hostname, port);
            JSONObject data = client.readInput(inputFilePath);
            client.putData(data);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("ERROR: Bad arguments.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
