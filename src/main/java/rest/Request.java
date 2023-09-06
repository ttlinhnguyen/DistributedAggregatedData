package rest;

import java.io.Serializable;

public class Request implements Serializable {
    public String method;
    public int clockTime;
    public String body;
    public Request(String method, int clockTime, String body) {
        this.method = method;
        this.clockTime = clockTime;
        this.body = body;
    }
}
