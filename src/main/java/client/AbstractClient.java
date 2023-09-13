package client;

import clock.LamportClock;
import rest.Request;
import rest.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class AbstractClient {
    protected LamportClock clock;
    protected Socket socket;
    protected ObjectOutputStream outStream;
    protected ObjectInputStream inStream;

    public AbstractClient(String hostname, int port) throws IOException, InterruptedException {
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
    }

    protected void sendRequest(Request req) throws IOException {
        outStream = new ObjectOutputStream(socket.getOutputStream());
        outStream.writeObject(req);
    }

    protected Response getResponse() throws IOException, ClassNotFoundException {
        inStream = new ObjectInputStream(socket.getInputStream());
        return (Response) inStream.readObject();
    }
}
