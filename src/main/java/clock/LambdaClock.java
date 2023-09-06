package clock;

public class LambdaClock {
    private int t = 0;
    public synchronized int get() {
        return t;
    }
    public synchronized void update(int val) {
        t = Math.max(t, val) + 1;
    }
    public synchronized void increment() {
        t++;
    }
}
