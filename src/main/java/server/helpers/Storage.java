package server.helpers;

import clock.LamportClock;
import org.json.JSONArray;
import org.json.JSONObject;
import server.AggregationServer;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.*;

public class Storage {
    int CAP = 20;
    String dbPath;
    private JSONObject data;
    private LamportClock clock;
    private File db;
    public Storage(AggregationServer server, String filename) {
        this.clock = server.getClock();
        this.dbPath = filename;
        db = new File(dbPath);
        updateLocalData();
    }

    /**
     * Retrieves the weather data stored in the server
     * @return A JSON string of the weather data
     */
    public String getWeatherData() {
        clock.increment();
        return data.toString();
    }

    /**
     * Updates the weather data stored in the server.
     * @param obj the new data to be added
     * @param clockTime the LamportClock timestamp from the client
     */
    public void putWeatherData(JSONObject obj, int clockTime) {
        clock.update(clockTime);
        String clientId = obj.getString("id");
        if (!data.has(clientId)) data.put(clientId, new JSONArray());
        data.getJSONArray(clientId).put(obj);

        if (data.getJSONArray(clientId).length() > CAP) {
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
        updateDbFile();
    }

    /**
     * Remove the weather data submitted by the content server with the specified id.
     * @param id The id of the content server.
     */
    public void removeWeatherData(String id) {
        if (id!=null && data.has(id)) {
            data.remove(id);
            updateDbFile();
            System.out.println("Delete content from " + id);
        }
    }

    /**
     * Remove all weather data.
     */
    public void removeAllData() {
        data = new JSONObject();
        updateDbFile();
    }

    /**
     * Updates the data file using the local data.
     */
    private void updateDbFile() {
        try {
            FileWriter writer = new FileWriter(db);
            writer.write(data.toString());
            writer.flush();
            writer.close();
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

    public boolean isEmpty() { return data.isEmpty(); }

}
