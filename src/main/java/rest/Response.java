package rest;

import java.io.Serializable;

public class Response extends Messages implements Serializable {
    public int status;

    /**
     * Creates a Response that contains the status, Lamport timestamp, and the body.<br>
     * It implements the {@code Serializable} interface to be
     * passed onto {@code ObjectOutputStream} and {@code ObjectInputStream}.
     * @param status the status code such as {@code 200}, {@code 400}, and {@code 500}
     */
    public Response(int status) {
        super();
        this.status = status;
    }
    /**
     * Creates a Response that contains the status, Lamport timestamp, and the body.<br>
     * It implements the {@code Serializable} interface to be
     * passed onto {@code ObjectOutputStream} and {@code ObjectInputStream}.
     */
    public Response() {
        super();
    }

    /**
     * Set the status code of the response.
     * @param status Status code.
     */
    public void setStatus(int status) {
        this.status = status;
    }
}
