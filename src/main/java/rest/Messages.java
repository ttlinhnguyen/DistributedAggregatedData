package rest;

import java.util.HashMap;

public class Messages {
    public HashMap<String, String> headers;
    public String body;

    /**
     * Abstract Message class to be used for {@code Request} and {@code Response}
     */
    Messages() {
        headers = new HashMap<>();
    }

    /**
     * Add header to the HTTP message.
     * @param key Headers such as Host, User-Agent, etc.
     * @param value The value of the header.
     */
    public void addHeader (String key, String value) {
        headers.put(key, value);
    }
    /**
     * Add headers to the HTTP message.
     * @param add {@code HashMap} with headers and their values.
     */
    public void addHeaders (HashMap<String, String> add) {
        headers.putAll(add);
    }

    /**
     * Assign the body of the HTTP message to the specified value.
     * @param body The content of the body.
     */
    public void setBody(String body) {
        this.body = body;
    }
}
