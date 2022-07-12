package lib;

public class StopFlag {

    private boolean stopFlag;

    public StopFlag(boolean flag) {
        this.stopFlag = flag;
    }

    public synchronized boolean getStopFlag() {
        return stopFlag;
    }

    public synchronized void setStopFlag(boolean flag) {
        this.stopFlag = flag;
    }
}
