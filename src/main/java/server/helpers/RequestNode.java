package server.helpers;


import rest.Request;

import java.net.Socket;

public class RequestNode {
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

