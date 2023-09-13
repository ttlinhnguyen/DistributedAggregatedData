import client.getclient.GETClient;
import client.content.ContentServer;
import server.AggregationServer;

import static java.lang.Thread.sleep;

public class Tests {
    private void test1() {
        try {
            AggregationServer server = new AggregationServer(4567);
            Thread tServer = new Thread(server);
            tServer.start();

            GETClient client = new GETClient("localhost", 4567);
            ContentServer content = new ContentServer("localhost", 4567);
            content.readInput("src/main/java/client/content/data1.txt");

            Thread tContent = new Thread(content);
            Thread tClient = new Thread(client);

            sleep(100);
            tContent.start();
            tClient.start();

            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Tests tests = new Tests();
        tests.test1();
    }
}