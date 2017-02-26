/**
 * Abstract class from where the 5 types of modules of the system are derived.
 */

import java.util.List;
import java.util.Queue;

public abstract class Module {

    protected  double realLambda;
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
     * Temporary variable used to store the current idle time in a query.
     */
    protected  double idleTime;

    /**
     * Sums up the total amount of idle time per query.
     */
    protected  double totalIdleTime;

    // Variables used for statistics

    /**
     * Calculates the mean of the time spent in queue per query.
     */
    protected double avgTimeInQueue;

    /**
     * Stores the average amount of queries from its respective module. Mainly used for statistical purposes.
     */
    protected double averageQueriesL;

    /**
     * Stores the average amount of queries in queue from its respective module. Mainly used for statistical purposes.
     */
    protected double averageQueriesInQueue;

    /**
     * Stores the average amount of queries in service from its respective module. Mainly used for statistical purposes.
     */
    protected double averageQueriesInService;

    /**
     * Stores the average amount of time per query from its respective module. Mainly used for statistical purposes.
     */
    protected double averageTimeW;

    /**
     * Stores the average amount of time in queue from its respective module. Mainly used for statistical purposes.
     */
    protected double averageTimeInQueue;

    /**
     * Stores the average amount of time in service from its respective module. Mainly used for statistical purposes.
     */
    protected double averageTimeInService;

    /**
     * The effective service rate in its respective module.
     */
    protected double averageServiceTimeMu;

    /**
     * Average time a DDL query type lasts inside the module.
     */
    protected double ddlAvgTime;

    /**
     * Average time an Update query type lasts inside the module.
     */
    protected double updateAvgTime;

    /**
     * Average time a Join query type lasts inside the module.
     */
    protected double joinAvgTime;

    /**
     * Average time a Select query type lasts inside the module.
     */
    protected double selectAvgTime;

    /**
     * The server utilization rate in its respective module.
     */
    protected double averageOccupiedTimeRho;

    /**
     * The effective arrival date in its respective module.
     */
    protected double averageArrivalTimeLambda;

    /**
     * Calculates the average queries in the module and assigns the result to its global variable.
     * @param averageQueriesLQ average amount of queries in queue
     * @param averageQueriesLS average amount of queries in service
     */
    public void computeAverageQueriesL(double averageQueriesLQ, double averageQueriesLS){
        averageQueriesL = averageQueriesLQ + averageQueriesLS;
    }

    /**
     * Calculates the average amount of queries in queue and assigns the result to its global variable.
     * @param queryList list that contains all the queries that passed through *this.
     */
    public abstract void computeAverageQueriesInQueue(List<Query> queryList) ;


    /**
     * Calculates the average amount of queries in service and assigns the result to its global variable.
     * @param queryList list that contains all of the queries that passed through *this.
     */
    public abstract void computeAverageQueriesInService(List<Query> queryList);

    /**
     * Calculates the average time a query was in *this and assigns the result to its global variable.
     * @param averageTimeWQ the average time of a query in queue.
     * @param averageTimeWS the average time of a query in service.
     */
    public void computeAverageTimeW(double averageTimeWQ , double averageTimeWS){
        averageTimeW = averageTimeWQ + averageTimeWS;
    }

    /**
     * Calculates the average time a query was in queue and assigns the result to its global variable.
     * @param queryList list that contains all the queries that passed through *this.
     */
    public abstract void computeAverageTimeInQueue(List<Query> queryList);

    /**
     * Calculates the average time a query was in service and assigns the result to its global variable.
     * @param queryList list that contains all the queries that passed through *this.
     */
    public abstract void computeAverageTimeInService(List<Query> queryList);

    /**
     * Calculates and stores the effective service rate for the module.
     */
    public void computeAverageServiceTimeMu(){
        averageServiceTimeMu = 1/ averageTimeInService;
    }

    /**
     * Decides whether query has to wait in queue or can be served immediately.
     * @param query specific query that's being processed in the module.
     */
    public abstract void processArrival(Query query);

    /**
     * Manages the query's exit from the module once it's done being served.
     * @param query specific query that was being processed in the module.
     */
    public abstract void processDeparture(Query query);

    /**
     * Hunts and kills a query inside the module in case the event containing the query indicates so.
     * @param query specific query to be killed.
     */
    public abstract void processKill(Query query);

    /**
     * Creates an arrival event (and a kill event in the first module) of a
     * query to *this and inserts it to the system's event list
     * @param query specific query that arrives to the module.
     */
    public abstract void generateServiceEvent(Query query);

    /**
     * Verifies if all the servers inside the module are busy.
     * @return true if they are all busy, false otherwise.
     */
    public abstract boolean isBusy();

    /**
     * Fetches the variable that contains the amount of free servers.
     * @return the amount of free servers in *this.
     */
    public abstract int getNumberOfFreeServers();

    /**
     * Shows the amount of queries that are in *this's queue.
     * @return how many queries are in queue.
     */
    public abstract int getQueueSize();

    /**
     * Uses the basic formula of lambda/mu in order to store its value.
     */
    public void computeAverageOccupiedTimeRho(double lambda){
        averageOccupiedTimeRho = lambda / (servers*averageServiceTimeMu);
    }


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

    public double getAverageOccupiedTimeRho(){
        return averageOccupiedTimeRho;
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



    public  double  computeFactorial(int factorialOf){
        if(factorialOf ==1|| factorialOf == 0)
            return 1;

        else
            return factorialOf*computeFactorial(factorialOf-1);
    }

    public  double computePi0( double rho ){
        double pi0=0;
        int servers=this.servers;
        if(servers ==1){
            pi0 = 1-rho;

        }else {
            double valOfSumatory=0;
            for (int i =0; i <= servers-1 ; i++){
                valOfSumatory+=  Math.pow((servers * rho), i)/ computeFactorial(i);
            }
            pi0 = 1/(valOfSumatory + (Math.pow((servers*rho),servers )/(computeFactorial(servers)*(1-rho))));
        }

        return  pi0;
    }

    public double computePropabilityThatNBeMajorThanS(double rho  , double pi0 ) {
        int servers=this.servers;
        return  ((Math.pow((rho* servers),servers)*pi0))/(computeFactorial(servers)*(1-rho));
    }


    public  double computeLq( double lambda , double mu , boolean hasQueue ){
        double lq =0;
        if(hasQueue) {
            int servers = this.servers;
            if (servers == 1) {
                lq = (Math.pow(lambda, 2)) / (mu * (mu - lambda));

            } else {
                double rho = lambda / (servers * mu);
                double pi0 = computePi0(rho);
                lq = (computePropabilityThatNBeMajorThanS(rho, pi0) * rho) / (1 - rho);
            }

        }
        return lq;
    }

    double computeLs(double lambda , double mu){
        return  lambda/mu;
    }


    public double  computeWq(double lambda , double mu , boolean hasQueue){
        double wq=0;
        if(hasQueue){
            int servers = this.servers;
            if(servers ==1 ){
                wq =lambda / (mu*(mu - lambda));

            }else {
                double rho = lambda/(mu * servers);
                double pi0= computePi0(rho);
                wq = computePropabilityThatNBeMajorThanS(rho,pi0)/((servers*mu)- lambda);
            }
        }
        return  wq;
    }

    public abstract void fillStatistics( double lambda  );





}
