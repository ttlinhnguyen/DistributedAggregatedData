import rest.Request;
import rest.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GETClient {
    final Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    public GETClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new ObjectInputStream(socket.getInputStream());
    }
    void getData() {
        try {
            outStream.writeObject(new Request("GET", 0, ""));
            Response res = (Response) inStream.readObject();
            System.out.println(res.status);
            System.out.println(res.body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        GETClient client = new GETClient("localhost", 4567);
        client.getData();
    }
}
