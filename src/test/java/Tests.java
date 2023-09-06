import client.GETClient;
import content.ContentServer;
import org.json.JSONArray;
import server.AggregationServer;

public class Tests {
    private void test1() {
        try {
            AggregationServer server = new AggregationServer(4567);
            server.start();

            ContentServer content = new ContentServer("localhost", 4567);
            GETClient client = new GETClient("localhost", 4567);

            JSONArray data1 = new JSONArray().put(content.readInput("src/main/java/content/data1.txt"));
            content.putData(data1);
            client.getData();
            content.putData(data1);
            client.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Tests tests = new Tests();
        tests.test1();
    }
}
