package server.helpers;

import clock.LamportClock;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import server.AggregationServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
    private File db;
    public Storage(AggregationServer server, String path) {
        this.server = server;
        this.clock = server.getClock();
        this.lock = server.getFileLock();

        this.path = path;
        this.db = new File(this.path);
        try {
            db.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
     * If the data file is faulty, retrieve it from the replica.
     */
    private void updateLocalData() {
        try {
            lock.acquire();
            String dbText = readFile(db);
            lock.release();

            if (dbText.isEmpty()) data = new JSONObject();
            else data = new JSONObject(dbText);
        } catch (JSONException e) {
            retrieveFromReplica();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replicate() {
        Thread t = new Thread(new Replica(server, server.replicaPath));
        t.start();
    }

    private void retrieveFromReplica() {
        try {
            lock.acquire();
            File replica = new File(server.replicaPath);
            String dbText = readFile(replica);
            lock.release();

            if (dbText.isEmpty()) data = new JSONObject();
            else data = new JSONObject(dbText);

            updateDbFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String readFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String res = "";
        if (scanner.hasNextLine()) {
            res += scanner.nextLine();
        }
        scanner.close();
        return res;
    }

    public boolean isEmpty() { return data.isEmpty(); }

}
