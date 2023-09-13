package server;

import rest.Response;

import java.net.Socket;
import java.util.Comparator;

public class ResponseNode {
    private static int priority = 0;
    public Socket socket;
    public Response response;
    public ResponseNode(Socket socket, Response req) {
        priority++;
        this.socket = socket;
        this.response = req;
    }
    public int getPriority() { return priority; }
}

class ResponseComparator implements Comparator<ResponseNode> {
    public int compare(ResponseNode n1, ResponseNode n2) {
        int p1 = n1.getPriority();
        int p2 = n2.getPriority();
        return Integer.compare(p2, p1);
    }
}