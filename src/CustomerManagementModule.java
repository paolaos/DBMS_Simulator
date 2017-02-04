import java.util.concurrent.LinkedBlockingQueue;

public class CustomerManagementModule extends Module{
    private final int LAMBDA = 35;
    private int kConnections;
    private int rejectedConnections;
    private int currentConnections;

    public CustomerManagementModule(int kConnections){
        this.kConnections = kConnections;
        queue = new LinkedBlockingQueue<>();
        rejectedConnections = 0;
        currentConnections = 0;
    }

    public int getRejectedConnections() {
        return rejectedConnections;
    }

    public int getCurrentConnections() {
        return currentConnections;
    }

    public void setRejectedConnections(int rejectedConnections) {
        this.rejectedConnections = rejectedConnections;
    }
}
