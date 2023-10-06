package server.helpers;


import org.json.JSONException;
import org.json.JSONObject;
import rest.HttpParser;
import rest.Request;
import rest.Response;
import server.AggregationServer;

import java.io.ObjectOutputStream;

public class RequestHandler {
    private AggregationServer server;
    private RequestNode reqNode;
    private Storage storage;

    public RequestHandler(RequestNode reqNode, AggregationServer server) {
        this.server = server;
        this.storage = server.getStorage();
        this.reqNode = reqNode;
    }

    /**
     * Handles the request and sends it back to the client through the output stream.
     */
    public void run() {
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(reqNode.socket.getOutputStream());
            Response res = getResponse(reqNode.request);
            String resHttpString = HttpParser.createResponse(res);
            outStream.writeObject(resHttpString); // send response
            System.out.println("server clock " + server.getClock().get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Interacts with the storage based on the request method and returns a response
     * @param req A request of the client
     * @return A corresponding response
     */
    private Response getResponse(Request req) {
        Response res = new Response();
        if (req.method.equals("GET")) {
            String data = storage.getWeatherData();
            res.setStatus(200);
            res.setBody(data);
        } else if (req.method.equals("PUT")) {
            if (req.body.isEmpty()) res.setStatus(204);
            else {
                try {
                    if (storage.isEmpty()) res.setStatus(201);
                    else res.setStatus(200);
                    JSONObject newObj = new JSONObject(req.body);
                    if (!req.headers.containsKey("Server-Timing")) storage.putWeatherData(newObj, 0);
                    else storage.putWeatherData(newObj, Integer.parseInt(req.headers.get("Server-Timing")));
                } catch (JSONException e) {
                    res.setStatus(500);
                }
            }
        } else res.setStatus(400);

        res.addHeader("Server-Timing", Integer.toString(server.getClock().get()));

        return res;
    }
}