package rest;

import java.io.Serializable;

public class Request implements Serializable {
    public String method;
    public int clockTime;
    public String body;

    /**
     * Creates a Request that contains the method, Lamport timestamp, and the body.<br>
     * It implements the {@code Serializable} interface to be
     * passed onto {@code ObjectOutputStream} and {@code ObjectInputStream}.
     * @param method the request method such as {@code GET} and {@code PUT}
     * @param clockTime the Lamport logical timestamp
     * @param body the body of the request
     */
    public Request(String method, int clockTime, String body) {
        this.method = method;
        this.clockTime = clockTime;
        this.body = body;
    }
}
