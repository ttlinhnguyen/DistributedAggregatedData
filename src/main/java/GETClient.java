import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GETClient {
    private Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    GETClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new ObjectInputStream(socket.getInputStream());

        outStream.writeObject("Request to connect");
        try {
            System.out.println("Read " + inStream.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        GETClient client = new GETClient("localhost", 4567);
    }
}
