package content;

import clock.LambdaClock;
import org.json.JSONObject;
import rest.Request;
import rest.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ContentServer {
    LambdaClock clock;
    final Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    ContentServer(String hostname, int port) throws IOException {
        clock = new LambdaClock();
        socket = new Socket(hostname, port);
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new ObjectInputStream(socket.getInputStream());
    }
    void putData(String data) {
        clock.increment();
        try {
            outStream.writeObject(new Request("PUT", clock.get(), data));
            Response res = (Response) inStream.readObject();
            System.out.println(res.status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    JSONObject readInput(String filename) {
        JSONObject json = new JSONObject();

        return json;
    }
    public static void main(String[] args) throws IOException {
        ContentServer client = new ContentServer("localhost", 4567);
        client.putData("abc");
    }
}
