import rest.Request;
import rest.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AggregationServer {
    private ServerSocket server;
    public AggregationServer(int port) throws IOException {
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
                    Request req = (Request) inStream.readObject();
                    System.out.println("Read " + req.method);
                    outStream.writeObject(new Response(200, 0, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            }
        }
    }
}
