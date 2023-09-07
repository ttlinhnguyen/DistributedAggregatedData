package content;

import clock.LamportClock;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.Request;
import rest.Response;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ContentServer {
    LamportClock clock;
    final Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    /**
     * Creates a content server connected to a host with a specified port.
     * @param hostname the hostname of the server
     * @param port the port number of the server
     */
    public ContentServer(String hostname, int port) throws IOException {
        clock = new LamportClock();
        socket = new Socket(hostname, port);
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Sends a PUT request to the server with the new data to be added.
     * @param data the data to be sent to the server
     */
    public void putData(JSONArray data) {
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
        return json;
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

        ContentServer client = new ContentServer(hostname, port);
        JSONObject in = client.readInput("src/main/java/content/data1.txt");
        JSONArray data = new JSONArray().put(in);
        client.putData(data);
    }
}
