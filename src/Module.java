import java.util.List;
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
    protected int servedQueries;
    protected  double idleTime;
    protected  double totalIdleTime;

    // Statistics
    protected  double averageQueriesL;
    protected  double averageQueriesInQueue;
    protected  double averageQueriesInService;
    protected  double averageTimeW;
    protected  double averageTimeInQueue;
    protected  double averageTimeInService;

    public abstract void setAverageQueriesL(double avergeQueriesLQ, double avergeQueriesLS ) ;

    public abstract void setAverageQueriesInQueue(List<Query> queryList) ;

    public abstract void setAverageQueriesInService(List<Query> queryList);

    public abstract void setAverageTimeW(double avergeTimeWQ , double avergeTimeWS);

    public abstract void setAverageTimeInQueue(List<Query> queryList);

    public abstract void setAverageTimeInService(List<Query> queryList);


//TODO algoritmo general y métodos abstractos (override)

    public abstract void processArrival(Query query);

    public abstract void processDeparture(Query query); //procesar salida

    public abstract void processKill(Query query);

    public abstract void generateServiceEvent(Query query);


    public abstract boolean isBusy();

    public abstract double getNextExitTime();

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

    public void fillStatistics(Statistics statistics) {
    }

    public abstract int getNumberOfFreeServers();

    public abstract int getQueueSize();

    public abstract int getServedQueries();

    public abstract double getIdleTime();

    public abstract  double getDdlAvgTime(List <Query> queryList);

    public abstract  double getUpdateAvgTime(List <Query> queryList);

    public abstract  double getJoinAvgTime(List <Query> queryList);

    public abstract  double getSelectAvgTime(List <Query> queryList);

}
