import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AggregationServer {
    private ServerSocket server;
    AggregationServer(int port) throws IOException {
        server = new ServerSocket(port);
    }
    public static void main(String[] args) throws IOException {
        AggregationServer server = new AggregationServer(4567);
        server.start();
    }

    public void start() throws IOException {
        while (true) {
            Socket client = server.accept();
            HandleClient clientThread = new HandleClient(client);
            clientThread.start();
        }
    }

    static class HandleClient extends Thread {
        private Socket socket;
        ObjectOutputStream outStream;
        ObjectInputStream inStream;
        HandleClient(Socket socket) throws IOException{
            this.socket = socket;
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());
        }
        @Override
        public void run() {
//            while (true) {
                try {
                    Object o = inStream.readObject();
                    System.out.println("Read " + o);
                    outStream.writeObject("200");
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            }
        }
    }
}
