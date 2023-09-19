package rest;

import java.util.HashMap;

public class Messages {
    public HashMap<String, String> headers;
    public String body;
    Messages() {
        headers = new HashMap<>();
    }
    public void addHeader (String key, String value) {
        headers.put(key, value);
    }
    public void addHeaders (HashMap<String, String> add) {
        headers.putAll(add);
    }
    public void setBody(String body) {
        this.body = body;
    }
}
