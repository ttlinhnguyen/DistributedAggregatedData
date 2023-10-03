import client.getclient.GETClient;
import client.content.ContentServer;
import server.AggregationServer;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class ScenarioTests {
    private void clientReconnect() {
        try {
            testDivider();
            String testname = "TEST: Client tries to reconnect to server\n";
            System.out.println(testname);

            AggregationServer server = new AggregationServer(4567);
            GETClient client1 = new GETClient("localhost", 4567);

            Thread tClient1 = new Thread(client1);
            tClient1.start();
            sleep(2000);
            Thread tServer = new Thread(server);
            tServer.start();

            sleep(1000);
            server.stop();
            client1.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clientReconnectFail() {
        try {
            testDivider();
            String testname = "TEST: Client tries to reconnect to server but fails\n";
            System.out.println(testname);

            GETClient client1 = new GETClient("localhost", 4567);

            Thread tClient1 = new Thread(client1);
            tClient1.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPutGet() {
        try {
            testDivider();
            String testname = "TEST: GET - PUT - GET\n";
            System.out.println(testname);

            AggregationServer server = new AggregationServer(4567);
            GETClient client1 = new GETClient("localhost", 4567);
            GETClient client2 = new GETClient("localhost", 4567);
            ContentServer content = new ContentServer("localhost", 4567);
            content.readInput("src/main/java/client/content/data1.txt");

            Thread tServer = new Thread(server);
            Thread tContent = new Thread(content);
            Thread tClient1 = new Thread(client1);
            Thread tClient2 = new Thread(client1);

            tServer.start();

            sleep(100);
            tClient1.start();
            sleep(100);
            tContent.start();
            sleep(100);
            tClient2.start();

            sleep(4000);
            server.stop();
            content.stop();
            client1.stop();
            client2.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void concurrentGetPut() {
        try {
            testDivider();
            String testname = "TEST: Concurrent GET and PUT\n";
            System.out.println(testname);

            AggregationServer server = new AggregationServer(4567);
            GETClient client1 = new GETClient("localhost", 4567);
            ContentServer content = new ContentServer("localhost", 4567);
            content.readInput("src/main/java/client/content/data1.txt");

            Thread tServer = new Thread(server);
            Thread tContent = new Thread(content);
            Thread tClient1 = new Thread(client1);

            tServer.start();

            sleep(100);
            tContent.start();
            tClient1.start();

            sleep(4000);
            server.stop();
            content.stop();
            client1.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testDivider() {
        System.out.println("===============================");
    }
    public static void main(String[] args) {
        ScenarioTests tests = new ScenarioTests();
//        tests.clientReconnect();
        tests.clientReconnectFail();
//        tests.getPutGet();
//        tests.concurrentGetPut();
    }
}