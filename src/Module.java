import java.util.List;
import java.util.Queue;

public abstract class Module {
    /**
     * Every child from *this contains a queue (with the exception of the first module)
     */
    protected Queue<Query> queue;

    /**
     * Sums up every query's time in queue in order to be used with other statistics.
     */
    protected double accumulatedTimeInQueue;

    /**
     * Sums up every query's time in service in order to be used with other statistics.
     */
    protected double accumulatedTimeInService;

    /**
     * Counter for every query that has been served in the system.
     */
    protected int totalProcessedQueries;



    //protected ModuleType type;

    /**
     * How many servers the user defines for *this's child.
     */
    protected int servers;

    /**
     * Points out to the next module the query proceeds to.
     */
    protected Module nextModule;

    /**
     * Tags the simulation it belongs to in order to fetch back the user defined variables.
     */
    protected Simulation simulation;

    /**
     * Sets all the calculated statistics to the class in order to show them back to the user.
     */
    protected ModuleStatistics moduleStatistics;

    /**
     * Verifies if all the servers are occupied in its respective module.
     */
    protected boolean busy;

    /**
     * Counter that holds the value for all the queries that managed to
     */
    protected int servedQueries;


    /**
     * Temporary variable used to store the current idle time in a query.
     */
    protected  double idleTime;

    /**
     *
     */
    protected  double totalIdleTime;

    // Statistics

    /**
     * Calculates the median of the time spent in queue per query.
     */
    protected double avgTimeInQueue;
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

    /**
     *
     */
    public void computeAverageServiceTimeMu(){
        averageServiceTimeMu = 1/ averageTimeInService;
    }

    /**
     *
     * @param query
     */
    public abstract void processArrival(Query query);

    /**
     *
     * @param query
     */
    public abstract void processDeparture(Query query); //procesar salida

    /**
     *
     * @param query
     */
    public abstract void processKill(Query query);

    /**
     *
     * @param query
     */
    public abstract void generateServiceEvent(Query query);

    /**
     *
     * @return
     */
    public abstract boolean isBusy();

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */
    public abstract int getNumberOfFreeServers();

    /**
     *
     * @return
     */
    public abstract int getQueueSize();

    /**
     *
     * @return
     */
    public abstract int getServedQueries();

    /**
     *
     * @return
     */
    public abstract double getIdleTime();

    /**
     *
     * @return
     */
    public double getDdlAvgTime(){
        return ddlAvgTime;
    }

    /**
     *
     * @param queryList
     */
    public abstract void computeDdlAvgTime(List<Query> queryList);

    /**
     *
     * @return
     */
    public double getUpdateAvgTime(){
        return updateAvgTime;
    }

    /**
     *
     * @param queryList
     */
    public abstract void computeUpdateAvgTime(List<Query> queryList);

    /**
     *
     * @return
     */
    public double getJoinAvgTime(){
        return joinAvgTime;
    }

    /**
     *
     * @param queryList
     */
    public abstract void computeJoinAvgTime(List<Query> queryList);

    /**
     *
     * @return
     */
    public double getSelectAvgTime(){
        return selectAvgTime;
    }

    /**
     *
     * @param queryList
     */
    public abstract void computeSelectAvgTime(List<Query> queryList);

    /**
     *
     */
    public void computeAverageOccupiedTimeRho(int numberOfServers,double  averageArrivalTimeLambda , double averageServiceTimeMu ){
        averageOccupiedTimeRho = averageArrivalTimeLambda / (averageServiceTimeMu* numberOfServers);
    }

    /**
     *
     * @return
     */
    public double getAverageOccupiedTimeRho(){
        return averageOccupiedTimeRho;
    }



    public  void fillStatistics(int numberOfServers, double lambda , double mu ){

    }


    public double computePropabilityThatNBeMajorThanS(double lambda , double mu) {
       double probability =0;
       return  probability;

    }




}
