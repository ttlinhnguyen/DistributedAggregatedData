package server;


import rest.Request;

import java.net.Socket;
import java.util.Comparator;

class RequestNode {
    private static long priority = 0;
    public Socket socket;
    public Request request;
    public RequestNode(Socket socket, Request req) {
        priority++;
        this.socket = socket;
        this.request = req;
    }
    public long getPriority() { return priority; }
}

class RequestComparator implements Comparator<RequestNode> {
    public int compare(RequestNode n1, RequestNode n2) {
        return Long.compare(n1.getPriority(), n2.getPriority());
    }
}