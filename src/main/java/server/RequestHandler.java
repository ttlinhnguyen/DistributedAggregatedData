package server;


import org.json.JSONException;
import org.json.JSONObject;
import rest.Request;
import rest.Response;

import java.io.ObjectOutputStream;

class RequestHandler {
    private AggregationServer server;
    private RequestNode reqNode;

    public RequestHandler(RequestNode reqNode, AggregationServer server) {
        this.server = server;
        this.reqNode = reqNode;
    }
//    @Override
    public void run() {
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(reqNode.socket.getOutputStream());
            Response res = getResponse(reqNode);

            outStream.writeObject(res); // send response
            System.out.println("server clock " + server.clock.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Response getResponse(RequestNode reqNode) {
        Request req = reqNode.request;
        Response res;
        if (req.method.equals("GET")) {
            String data = server.getWeatherData();
            res = new Response(200, server.clock.get(), data);
        } else if (req.method.equals("PUT")) {
            if (req.body.isEmpty()) res = new Response(204, server.clock.get(), null);
            else {
                try {
                    JSONObject newObj = new JSONObject(req.body);
                    server.putWeatherData(newObj, req.clockTime);
                    res = new Response(200, server.clock.get(), null);
                } catch (JSONException e) {
                    res = new Response(500, server.clock.get(), null);
                }
            }
        } else res = new Response(400, server.clock.get(), null);

        return res;
    }
}