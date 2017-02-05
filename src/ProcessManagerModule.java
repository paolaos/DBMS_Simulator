import java.util.concurrent.LinkedBlockingQueue;

public class ProcessManagerModule extends Module{
    private boolean isBusy;
    public ProcessManagerModule(){
        queue = new LinkedBlockingQueue<>();
        timeQueue = new LinkedBlockingQueue<>();
        isBusy = false;
        hasBeenInQueue = 0;
    }

    public boolean isBusy() {
        return isBusy;
    }
}
