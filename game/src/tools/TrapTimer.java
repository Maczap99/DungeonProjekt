package tools;

public class TrapTimer implements Runnable {
    private int timeInMs;
    private boolean isRunning;
    private boolean isFinished;
    private long start;
    private long ende;

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

    public int getCurrentTimeInSec(){
        ende = System.currentTimeMillis();
        int time = (int) ((int) (ende - start));
        time = (timeInMs / 1000) - (time / 1000);

        if(time <= 0){
            return 0;
        }else{
            return time;
        }
    }

    @Override
    public void run() {
        isRunning = true;
        isFinished = false;

        start = System.currentTimeMillis();

        try {
            Thread.sleep(timeInMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isRunning = false;
        isFinished = true;
    }
}
