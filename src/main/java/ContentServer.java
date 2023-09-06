import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ContentServer {
    private Socket socket;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    ContentServer(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        outStream = new ObjectOutputStream(socket.getOutputStream());
        inStream = new ObjectInputStream(socket.getInputStream());
    }
    void getData() {
        try {
            outStream.writeObject("Request to connect");
            System.out.println("Read " + inStream.readObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        ContentServer client = new ContentServer("localhost", 4567);
        client.getData();
    }
}
