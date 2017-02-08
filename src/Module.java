import java.util.Queue;

public abstract class Module {
    protected Queue<Query> queue;
    protected Queue<Double> timeQueue;
    protected double accumulatedTimeInQueue;
    protected double accumulatedTimeInService;
    protected int totalProcessedQueries;
    protected double avgTimeInQueue;
    protected int hasBeenInQueue;
    protected ModuleType type;
    protected int servers;
    protected Module nextModule;
    protected Simulation simulation;
    protected ModuleStatistics moduleStatistics;
    protected boolean busy;
    
    //TODO algoritmo general y métodos abstractos (override)

    public abstract void processArrival(Query query);

    public abstract void generateServiceEvent(Query query);

    public abstract void processDeparture(Query query); //procesar salida

    public abstract boolean isBusy();

    public int getQueueSize(){
        return queue.size();
    }

    public double getAccumulatedTimeInQueue() {
        return accumulatedTimeInQueue;
    }

    public double getAccumulatedTimeInService() {
        return accumulatedTimeInService;
    }

    public int getTotalProcessedQueries() {
        return totalProcessedQueries;
    }

    public double getAvgTimeInQueue() {
        return avgTimeInQueue;
    }

    public void fillStatistics(Statistics statistics){

    }



    public abstract double getNextExitTime();
}
