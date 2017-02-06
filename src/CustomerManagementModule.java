import java.util.concurrent.LinkedBlockingQueue;

public class CustomerManagementModule extends Module{
    private final int LAMBDA = 35;
    private int kConnections;
    private int rejectedConnections;
    private int currentConnections;

    public CustomerManagementModule(int kConnections){
        this.kConnections = kConnections;
        queue = new LinkedBlockingQueue<>();
        timeQueue = new LinkedBlockingQueue<>();
        rejectedConnections = 0;
        currentConnections = 0;
        hasBeenInQueue = 0;
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

    @Override
    public void insertQuery(Query query){
        if(queue.size() < kConnections)
            queue.offer(query);

        else
            rejectedConnections++;
    }
}
