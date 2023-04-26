package tools;

public class TrapTimer implements Runnable {
    private int timeInMs;
    private boolean isRunning;
    private boolean isFinished;

    public TrapTimer(int timeInMs) {
        this.timeInMs = timeInMs;
        this.isRunning = false;
        this.isFinished = false;
    }

    public void start() {
        isRunning = true;
        isFinished = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void run() {
        isRunning = true;
        isFinished = false;

        try {
            Thread.sleep(timeInMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isRunning = false;
        isFinished = true;
    }
}
