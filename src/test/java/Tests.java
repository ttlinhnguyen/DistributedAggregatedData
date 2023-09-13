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

            GETClient client1 = new GETClient("localhost", 4567);
            GETClient client2 = new GETClient("localhost", 4567);

            ContentServer content = new ContentServer("localhost", 4567);
            content.readInput("src/main/java/client/content/data1.txt");

            Thread tContent = new Thread(content);
            Thread tClient1 = new Thread(client1);
            Thread tClient2 = new Thread(client2);

            sleep(100);
            tContent.start();
            tClient1.start();

            sleep(1000);
            tClient2.start();

            sleep(3000);
            server.stop();
            content.stop();
            client1.stop();
            client2.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Tests tests = new Tests();
        tests.test1();
    }
}