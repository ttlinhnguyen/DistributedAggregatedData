import rest.Request;
import rest.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AggregationServer {
    final private ServerSocket server;
    private String data;
    public AggregationServer(int port) throws IOException {
        server = new ServerSocket(port);
        data = "";
    }
    public static void main(String[] args) throws IOException {
        AggregationServer server = new AggregationServer(4567);
        server.start();
    }

    public void start() throws IOException {
        while (true) {
            Socket client = server.accept();
            Thread clientThread = new Thread(() -> handleClient(client));
            clientThread.start();
        }
    }
    private String getWeatherData() {
        return data;
    }
    private void putWeatherData(String newData) {
        data += newData;
    }

    private void handleClient(Socket socket) {
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

            Request req = (Request) inStream.readObject();
            System.out.println("Read " + req.method);
            System.out.println(req.body);
            if (req.method.equals("GET")) {
                outStream.writeObject(new Response(200, 0, getWeatherData()));
            } else if (req.method.equals("PUT")) {
                putWeatherData(req.body);
                outStream.writeObject(new Response(200, 0, ""));
            } else {
                outStream.writeObject(new Response(500, 0, ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
