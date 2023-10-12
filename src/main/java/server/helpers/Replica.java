package server.helpers;

import server.AggregationServer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class Replica implements Runnable {
    String path;
    File replica;
    Storage storage;
    Semaphore lock;

    /**
     * Holds replica/backup of the data.
     * @param server aggregation server
     * @param path path to the replica file.
     */
    public Replica(AggregationServer server, String path) {
        this.storage = server.getStorage();
        this.lock = server.getFileLock();
        this.path = path;

        // read/create file
        replica = new File(path);
        try {
            replica.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            lock.acquire();

            // write to file
            FileWriter writer = new FileWriter(replica);
            writer.write(storage.getWeatherData());
            writer.flush();
            writer.close();

            lock.release();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
