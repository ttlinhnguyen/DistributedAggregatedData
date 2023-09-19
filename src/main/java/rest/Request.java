package rest;

import java.io.Serializable;

public class Request extends Messages implements Serializable {
    public String method;

    /**
     * Creates a Request that contains the method, Lamport timestamp, and the body.<br>
     * It implements the {@code Serializable} interface to be
     * passed onto {@code ObjectOutputStream} and {@code ObjectInputStream}.
     * @param method the request method such as {@code GET} and {@code PUT}
     */
    public Request(String method) {
        super();
        this.method = method;
    }
}
