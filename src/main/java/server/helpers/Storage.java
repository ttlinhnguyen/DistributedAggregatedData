package server.helpers;

import clock.LamportClock;
import org.json.JSONArray;
import org.json.JSONObject;
import server.AggregationServer;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Semaphore;

public class Storage {
    int CAP = 20;
    String path;
    private AggregationServer server;
    private JSONObject data;
    private LamportClock clock;
    private Semaphore lock;
//    private Replica replica;
    private File db;
    public Storage(AggregationServer server, String path) {
        this.server = server;
        this.clock = server.getClock();
        this.lock = server.getFileLock();
//        this.replica = server.getReplica();

        this.path = path;
        this.db = new File(this.path);
        updateLocalData();
    }

    /**
     * Retrieves the weather data stored in the server
     * @return A JSON string of the weather data
     */
    public String getWeatherData() throws InterruptedException {
        clock.increment();
        return data.toString();
    }

    /**
     * Updates the weather data stored in the server.
     * @param obj the new data to be added
     * @param clockTime the LamportClock timestamp from the client
     */
    public void putWeatherData(JSONObject obj, int clockTime) throws InterruptedException {
        clock.update(clockTime);
        String clientId = obj.getString("id");

        // update local data
        if (!data.has(clientId)) data.put(clientId, new JSONArray());
        data.getJSONArray(clientId).put(obj);
        keepLatestData(clientId, CAP);

        updateDbFile();
        replicate();
    }

    /**
     * Remove the weather data submitted by the content server with the specified id.
     * @param id The id of the content server.
     */
    public void removeWeatherData(String id) throws InterruptedException {
        if (id!=null && data.has(id)) {
            data.remove(id);
            updateDbFile();
            System.out.println("Delete content from " + id);
        }
        replicate();
    }

    /**
     * Remove all weather data.
     */
    public void removeAllData() throws InterruptedException {
        data = new JSONObject();
        updateDbFile();
        replicate();
    }

    private void keepLatestData(String clientId, int capacity) {
        if (data.getJSONArray(clientId).length() > capacity) {
            JSONArray jsonArr = data.getJSONArray(clientId);
            ArrayList<JSONObject> arr = new ArrayList<>();
            for (int i=0; i<jsonArr.length(); i++) {
                arr.add(jsonArr.getJSONObject(i));
            }
            arr.sort((o1, o2) -> {
                Timestamp t1 = Timestamp.valueOf((String) o1.get("timestamp"));
                Timestamp t2 = Timestamp.valueOf((String) o2.get("timestamp"));
                return t1.compareTo(t2);
            });

            int i = arr.size()-1;
            while (i>=CAP) {
                arr.remove(i);
                i--;
            }

            data.getJSONArray(clientId).clear();
            data.getJSONArray(clientId).putAll(arr);
        }
    }

    /**
     * Updates the data file using the local data.
     */
    private void updateDbFile() {
        try {
            lock.acquire();
            FileWriter writer = new FileWriter(db);
            writer.write(data.toString());
            writer.flush();
            writer.close();
            lock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the local data from the data file.
     */
    private void updateLocalData() {
        try {
            db.createNewFile();
            Scanner scanner = new Scanner(db);
            String dbText = "";
            if (scanner.hasNextLine()) {
                dbText += scanner.nextLine();
            }
            scanner.close();
            if (dbText.isEmpty()) data = new JSONObject();
            else data = new JSONObject(dbText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replicate() {
        Thread t = new Thread(new Replica(server, "src/main/java/server/replica.json"));
        t.start();
    }

    public boolean isEmpty() { return data.isEmpty(); }

}
