package rest;

import java.io.Serializable;

public class Response implements Serializable {
    public int status;
    public int clockTime;
    public String body;
    public Response(int status, int clockTime, String body) {
        this.status = status;
        this.clockTime = clockTime;
        this.body = body;
    }
}
