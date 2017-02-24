import java.util.List;
import java.util.Queue;

public abstract class Module {
    protected Queue<Query> queue;
    protected double accumulatedTimeInQueue;
    protected double accumulatedTimeInService;
    protected int totalProcessedQueries;
    protected double avgTimeInQueue;
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
    protected double averageQueriesL;
    protected double averageQueriesInQueue;
    protected double averageQueriesInService;
    protected double averageTimeW;
    protected double averageTimeInQueue;
    protected double averageTimeInService;
    protected double averageServiceTimeMu;
    protected double ddlAvgTime;
    protected double updateAvgTime;
    protected double joinAvgTime;
    protected double selectAvgTime;
    protected double averageOccupiedTimeRho;
    protected double averageArrivalTimeLambda;

    public abstract void computeAverageQueriesL(double averageQueriesLQ, double averageQueriesLS);

    public abstract void computeAverageQueriesInQueue(List<Query> queryList) ;

    public abstract void computeAverageQueriesInService(List<Query> queryList);

    public abstract void computeAverageTimeW(double averageTimeWQ , double averageTimeWS);

    public abstract void computeAverageTimeInQueue(List<Query> queryList);

    public abstract void computeAverageTimeInService(List<Query> queryList);


//TODO algoritmo general y métodos abstractos (override)

    public void computeAverageServiceTimeMu(){
        averageServiceTimeMu = 1/ averageTimeInService;
    }

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

    public double getDdlAvgTime(){
        return ddlAvgTime;
    }

    public abstract void computeDdlAvgTime(List<Query> queryList);

    public double getUpdateAvgTime(){
        return updateAvgTime;
    }

    public abstract void computeUpdateAvgTime(List<Query> queryList);

    public double getJoinAvgTime(){
        return joinAvgTime;
    }

    public abstract void computeJoinAvgTime(List<Query> queryList);

    public double getSelectAvgTime(){
        return selectAvgTime;
    }

    public abstract void computeSelectAvgTime(List<Query> queryList);

    public void computeAverageOccupiedTimeRho(){
        averageOccupiedTimeRho = averageArrivalTimeLambda / averageServiceTimeMu;
    }

    public double getAverageOccupiedTimeRho(){
        return averageOccupiedTimeRho;
    }
}
