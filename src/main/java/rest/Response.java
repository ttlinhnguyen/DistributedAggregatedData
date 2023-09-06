package rest;

import java.io.Serializable;

public class Response implements Serializable {
    public int status;
    public int clockTime;
    public String body;

    /**
     * Creates a Response that contains the status, Lamport timestamp, and the body.<br>
     * It implements the {@code Serializable} interface to be
     * passed onto {@code ObjectOutputStream} and {@code ObjectInputStream}.
     * @param status the status code such as {@code 200}, {@code 400}, and {@code 500}
     * @param clockTime the Lamport logical timestamp
     * @param body the body of the response
     */
    public Response(int status, int clockTime, String body) {
        this.status = status;
        this.clockTime = clockTime;
        this.body = body;
    }
}
