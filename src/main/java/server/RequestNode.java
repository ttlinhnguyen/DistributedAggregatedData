package server;


import rest.Request;

import java.net.Socket;

public class RequestNode {
    public Socket socket;
    public Request request;
    public RequestNode(Socket socket, Request req) {
        this.socket = socket;
        this.request = req;
    }
}