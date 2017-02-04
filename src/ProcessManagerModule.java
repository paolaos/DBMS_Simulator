import java.util.concurrent.LinkedBlockingQueue;

public class ProcessManagerModule extends Module{
    private boolean isBusy;
    public ProcessManagerModule(){
        queue = new LinkedBlockingQueue<>();
        isBusy = false;
    }

    public boolean isBusy() {
        return isBusy;
    }
}
