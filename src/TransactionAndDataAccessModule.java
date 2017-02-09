import java.util.PriorityQueue;

public class TransactionAndDataAccessModule extends Module{
    private final double DDL_RESTRUCTRATION_TIME = (double) 0.5;
    private final double UPDATE_RESTRUCTURATION_TIME = 1;
    private int pQueries;
    private int currentQueries;

    public TransactionAndDataAccessModule(Simulation simulation, Module nextModule, int pQueries){
        this.simulation = simulation;
        this.nextModule = nextModule;
        queue = new PriorityQueue<>();
        this.pQueries = pQueries;
        currentQueries = 0;
    }
    
    @Override
    public void processArrival(Query query) {

    }

    @Override
    public void generateServiceEvent(Query query) {

    }

    @Override
    public void processDeparture(Query query) {

    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public double getNextExitTime() {
        return 0;
    }

    public int getBlockNumber(QueryType query) {
        int numberOfBlocks = 0;
        switch (query) {
            case DDL:
                numberOfBlocks = 0;
                break;

            case UPDATE:
                numberOfBlocks = 0;
                break;

            case JOIN:
                int x = (int) Math.nextUp(DistributionGenerator.getNextRandomValueByUniform((double) 1, (double) 16));
                int y = (int) Math.nextUp(DistributionGenerator.getNextRandomValueByUniform((double) 1, (double) 12));
                numberOfBlocks = x + y;
                break;

            case SELECT:
                numberOfBlocks = (int) Math.nextUp(DistributionGenerator.getNextRandomValueByUniform((double) 1, (double) 64));
                break;
        }
        return numberOfBlocks;
    }

    public double getBlockLoadingTime(int numberOfBlocks) {
        return numberOfBlocks * (double) 0.1;
    }

    public double getRestructurationTime(QueryType query) {
        double time;
        if (query == QueryType.DDL) {
            time = DDL_RESTRUCTRATION_TIME;
        } else {
            time = UPDATE_RESTRUCTURATION_TIME;
        }
        return time;
    }
}
