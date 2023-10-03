import client.content.ContentServer;
import client.getclient.GETClient;
import org.junit.Test;
import org.junit.Assert;
import rest.Request;
import server.AggregationServer;

public class UnitTests {
    @Test
    public void getClientRequest() {
        try {
            AggregationServer server = new AggregationServer(4567);
            Thread tServer = new Thread(server);
            tServer.start();

            GETClient client = new GETClient("localhost", 4567);
            Request req = client.createRequest();
            Assert.assertEquals("GET", req.method);
            Assert.assertEquals("GETClient", req.headers.get("User-Agent"));

            server.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void contentRequest() {
        try {
            AggregationServer server = new AggregationServer(4567);
            Thread tServer = new Thread(server);
            tServer.start();

            ContentServer content = new ContentServer("localhost", 4567);
            content.readInput("src/main/java/client/content/data1.txt");
            Request req = content.createRequest();
            Assert.assertEquals("PUT", req.method);
            Assert.assertEquals("ContentServer", req.headers.get("User-Agent"));
            Assert.assertEquals("256", req.headers.get("Content-Length"));

            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
