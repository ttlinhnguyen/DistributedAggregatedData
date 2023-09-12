import client.GETClient;
import content.ContentServer;
import org.json.JSONArray;
import org.json.JSONObject;
import server.AggregationServer;

public class Tests {
    private void test1() {
        try {
            AggregationServer server = new AggregationServer(4567);
            server.start();
            Thread.sleep(1000);

            ContentServer content = new ContentServer("localhost", 4567);
            GETClient client = new GETClient("localhost", 4567);
            JSONObject data1 = content.readInput("src/main/java/content/data1.txt");
            JSONObject data2 = content.readInput("src/main/java/content/data2.txt");

            Thread clientThread1 = new Thread(client::getData);
            Thread clientThread2 = new Thread(client::getData);
            Thread contentThread1 = new Thread(() -> content.putData(data1));
            Thread contentThread2 = new Thread(() -> content.putData(data2));

            contentThread1.start();
            clientThread1.start();

            Thread.sleep(500);
            contentThread2.start();
            clientThread2.start();
//            content.putData(data1);
//            client.getData();
//            Thread.sleep(500);
//            content.putData(data1);
//            client.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Tests tests = new Tests();
        tests.test1();
    }
}
