package server.helpers;

import clock.LamportClock;
import org.json.JSONArray;
import org.json.JSONObject;
import server.AggregationServer;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Storage {
    String dbPath;
    private JSONObject data;
    private LamportClock clock;
    public Storage(AggregationServer server, String filename) {
        this.clock = server.getClock();
        this.dbPath = filename;

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
            FileWriter writer = new FileWriter(dbPath);
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
            File db = new File(dbPath);
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

}
