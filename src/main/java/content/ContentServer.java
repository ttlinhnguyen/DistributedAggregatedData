package content;

import clock.LambdaClock;
import org.json.JSONArray;
import org.json.JSONObject;
import rest.Request;
import rest.Response;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ContentServer {
    LambdaClock clock;
    final Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    public ContentServer(String hostname, int port) throws IOException {
        clock = new LambdaClock();
        socket = new Socket(hostname, port);
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new ObjectInputStream(socket.getInputStream());
    }
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
    public static void main(String[] args) throws IOException {
        ContentServer client = new ContentServer("localhost", 4567);
        JSONObject in = client.readInput("data1.txt");
        JSONArray data = new JSONArray().put(in);
        client.putData(data);
    }
}
