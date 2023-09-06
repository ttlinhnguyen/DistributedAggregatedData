package clock;

public class LamportClock {
    private int t = 0;

    /**
     * Creates a Lamport clock to keep track of
     * the logical time across different systems and servers.
     */
    public LamportClock() {}

    /**
     * Returns the logical timestamp of the Lamport clock.
     * @return the current timestamp
     */
    public synchronized int get() {
        return t;
    }

    /**
     * Updates the timestamp to the maximum value of itself
     * and the specified value.
     * @param val the current timestamp
     */
    public synchronized void update(int val) {
        t = Math.max(t, val) + 1;
    }

    /**
     * Increments the current timestamp by 1.
     */
    public synchronized void increment() {
        t++;
    }
}
