import java.util.PriorityQueue;

public class TransactionAndDataAccessModule extends Module{
    private int pQueries;
    private int currentQueries;

    public TransactionAndDataAccessModule(int pQueries){
        queue = new PriorityQueue<>();
        this.pQueries = pQueries;
        currentQueries = 0;
    }
}
