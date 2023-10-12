import client.getclient.GETClient;
import client.content.ContentServer;
import server.AggregationServer;


import java.lang.reflect.Method;

import static java.lang.Thread.sleep;

public class ScenarioTests {
    private String data1 = "data1.txt";
    private String data2 = "data2.txt";

    @Test
    private void clientReconnect() {
        try {
            printTestTitle("Client tries to reconnect to server");
            printTestExpected("Client reconnects after every 1 second for 3 times. Will be connected after the 2nd time.");

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

    @Test
    private void clientReconnectFail() {
        try {
            printTestTitle("Client tries to reconnect to server but fails");
            printTestExpected("Client reconnects after every 1 second for 3 times. After the 3rd time, client will stop.");


            GETClient client1 = new GETClient("localhost", 4567);

            Thread tClient1 = new Thread(client1);
            tClient1.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    private void sendNoContent() {
        try {
            printTestTitle("Sends no content to the server");
            printTestExpected("Status code 204");

            AggregationServer server = new AggregationServer(4567);
            ContentServer content = new ContentServer("localhost", 4567);

            Thread tServer = new Thread(server);
            Thread tContent = new Thread(content);

            tServer.start();

            sleep(100);
            tContent.start();

            sleep(500);
            server.removeAllData();
            server.stop();
            content.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    private void getPutGet() {
        try {
            printTestTitle("Sequential GET - PUT - GET");
            printTestExpected("Follows the order GET, PUT, then GET. All of them returns 200 code.");


            AggregationServer server = new AggregationServer(4567);
            GETClient client1 = new GETClient("localhost", 4567);
            GETClient client2 = new GETClient("localhost", 4567);
            ContentServer content = new ContentServer("localhost", 4567);
            content.readInput(data1);

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

            sleep(500);
            server.removeAllData();
            server.stop();
            content.stop();
            client1.stop();
            client2.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    private void concurrentGetPut() {
        try {
            printTestTitle("Concurrent GET and PUT");
            printTestExpected("Returns GET - PUT or PUT - GET randomly with 200 code.\n"
            + "Lamport clock works correctly in ascending order.");

            AggregationServer server = new AggregationServer(4567);
            GETClient client1 = new GETClient("localhost", 4567);
            ContentServer content = new ContentServer("localhost", 4567);
            content.readInput(data1);

            Thread tServer = new Thread(server);
            Thread tContent = new Thread(content);
            Thread tClient1 = new Thread(client1);

            tServer.start();

            sleep(100);
            tContent.start();
            tClient1.start();

            sleep(500);
            server.removeAllData();
            server.stop();
            content.stop();
            client1.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    private void concurrentPutPut() {
        try {
            printTestTitle("Concurrent PUTs then GET");
            printTestExpected("Correct data change with 200 code. Correct Lamport clock with ascending order.");

            AggregationServer server = new AggregationServer(4567);
            GETClient client1 = new GETClient("localhost", 4567);
            ContentServer content1 = new ContentServer("localhost", 4567);
            ContentServer content2 = new ContentServer("localhost", 4567);
            content1.readInput(data1);
            content2.readInput(data2);

            Thread tServer = new Thread(server);
            Thread tContent1 = new Thread(content1);
            Thread tContent2 = new Thread(content2);
            Thread tClient1 = new Thread(client1);

            tServer.start();

            sleep(100);
            tContent1.start();
            tContent2.start();
            sleep(100);
            tClient1.start();

            sleep(500);
            server.removeAllData();
            server.stop();
            content1.stop();
            content2.stop();
            client1.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    private void removeContentAfter30s() {
        try {
            printTestTitle("Server removes content after 30 seconds");
            printTestExpected("Puts content to server. 1st client (within 30s) can retrieve that content. 2nd client (after 30s) retrieves nothing");

            AggregationServer server = new AggregationServer(4567);
            ContentServer content1 = new ContentServer("localhost", 4567);
            content1.readInput(data1);
            GETClient client1 = new GETClient("localhost", 4567);
            GETClient client2 = new GETClient("localhost", 4567);

            Thread tServer = new Thread(server);
            Thread tContent1 = new Thread(content1);
            Thread tClient1 = new Thread(client1);
            Thread tClient2 = new Thread(client2);

            tServer.start();

            sleep(100);
            tContent1.start();
            sleep(1000);
            tClient1.start();

            sleep(30*1000);
            tClient2.start();
            sleep(100);
            server.stop();
            content1.stop();
            client1.stop();
            client2.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    private void serverCrashes() {
        try {
            printTestTitle("Server crashes when clients are waiting for response.");
            printTestExpected("Status code 500");

            AggregationServer server = new AggregationServer(4567);
            ContentServer content1 = new ContentServer("localhost", 4567);
            content1.readInput(data1);

            Thread tServer = new Thread(server);
            Thread tContent1 = new Thread(content1);

            tServer.start();

            sleep(100);
            tContent1.start();
            sleep(10);
            server.removeAllData();
            server.stop();

            sleep(100);
            content1.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    private void clientCrashes() {
        try {
            printTestTitle("Client crashes when server is processing request");
            printTestExpected("None");

            AggregationServer server = new AggregationServer(4567);
            ContentServer content1 = new ContentServer("localhost", 4567);
            content1.readInput(data1);

            Thread tServer = new Thread(server);
            Thread tContent1 = new Thread(content1);

            tServer.start();

            sleep(100);
            tContent1.start();
            sleep(3);
            content1.stop();

            sleep(100);
            server.removeAllData();
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    private void lotsOfPuts() {
        try {
            printTestTitle("Lots of Puts. Delete the earliest content if it exceeds 20");
            printTestExpected("The result only shows 20 latest content from a content server");

            AggregationServer server = new AggregationServer(4567);
            GETClient client1 = new GETClient("localhost", 4567);
            ContentServer content1 = new ContentServer("localhost", 4567);
            content1.readInput(data1);

            Thread tServer = new Thread(server);
            Thread tContent1 = new Thread(content1);
            Thread tClient = new Thread(client1);

            tServer.start();

            sleep(100);
            tContent1.start();

            for (int i=0; i<21; i++) {
                sleep(100);
                content1.readInput(data1);
                content1.requestAndResponse();
            }

            tClient.start();

            sleep(100);
            server.removeAllData();
            server.stop();
            content1.stop();
            client1.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testDivider(String pattern) {
        String res = "";
        for (int i=0; i<20; i++) res+=pattern;
        System.out.println(res);
    }
    private void printTestTitle(String s) {
        System.out.println();
        testDivider("=");
        System.out.println("TEST: "+s);
    }
    private void printTestExpected(String s) {
        System.out.println("Expected: "+s);
        testDivider("-");
    }
    public static void main(String[] args) {
        ScenarioTests tests = new ScenarioTests();
        tests.clientReconnect();
        tests.clientReconnectFail();
        tests.sendNoContent();
        tests.serverCrashes();
        tests.clientCrashes();
        tests.getPutGet();
        tests.concurrentGetPut();
        tests.concurrentPutPut();
        tests.lotsOfPuts();
        tests.removeContentAfter30s();
    }
}