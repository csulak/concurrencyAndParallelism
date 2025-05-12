package threads.syncBlock;

public class TimbreRunnable implements Runnable {

    private Timbre timbre;

    public TimbreRunnable(Timbre timbre) {
        this.timbre = timbre;
    }

    @Override
    public void run() {
        timbre.tocar();
    }
}
