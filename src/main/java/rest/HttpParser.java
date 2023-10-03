package rest;

import java.util.Scanner;

public class HttpParser {
    static public String createRequest(Request request) {
        StringBuilder result = new StringBuilder();
        result.append(request.method).append(" /weather.json HTTP/1.1\n");

        for (String key : request.headers.keySet()) {
            result.append(key).append(": ").append(request.headers.get(key)).append("\n");
        }
        result.append("\n").append(request.body);
        return result.toString();
    }
    static public Request parseRequest(String request) {
        Scanner scanner = new Scanner(request);
        if (scanner.hasNextLine()) {
            String startLine = scanner.nextLine();
            String method = startLine.split(" ")[0];
            Request req = new Request(method);

            boolean header = true;
            StringBuilder body = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    header = false;
                    continue;
                }

                if (header) {
                    String key = line.split(": ")[0];
                    String value = line.split(": ")[1];
                    req.addHeader(key, value);
                } else {
                    body.append(line);
                }
            }
            req.setBody(body.toString());
            return req;
        }
        return null;
    }
    public String createResponse(Response response) {
        StringBuilder result = new StringBuilder();
        result.append("HTTP/1.1 ").append(response.status).append("\n");

        for (String key : response.headers.keySet()) {
            result.append(key).append(": ").append(response.headers.get(key)).append("\n");
        }
        result.append("\n").append(response.body);
        return result.toString();
    }
    static public Response parseResponse(String response) {
        Scanner scanner = new Scanner(response);
        if (scanner.hasNextLine()) {
            String startLine = scanner.nextLine();
            int status = Integer.parseInt(startLine.split(" ")[1]);
            Response res = new Response(status);

            boolean header = true;
            StringBuilder body = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    header = false;
                    continue;
                }

                if (header) {
                    String key = line.split(": ")[0];
                    String value = line.split(": ")[1];
                    res.addHeader(key, value);
                } else {
                    body.append(line);
                }
            }
            res.setBody(body.toString());
            return res;
        }
        return null;
    }
}
