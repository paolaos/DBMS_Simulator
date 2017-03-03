/**
 * Abstract class from which the 5 types of modules of the system are derived.
 */

import java.lang.*;
import java.util.List;
import java.util.Queue;

public abstract class Module {
    /**
     * Every child from *this contains a queue (with the exception of the first module)
     */
    protected Queue<Query> queue;

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
    protected double idleTime;

    /**
     * Sums up the total amount of idle time per query.
     */
    protected double totalIdleTime;

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
     *
     * @param averageQueriesLQ average amount of queries in queue
     * @param averageQueriesLS average amount of queries in service
     */
    public double computeAverageQueriesL(double averageQueriesLQ, double averageQueriesLS) {
        return averageQueriesLQ + averageQueriesLS;
    }

    /**
     * Calculates the average time a query was in *this and assigns the result to its global variable.
     *
     * @param averageTimeWQ the average time of a query in queue.
     * @param averageTimeWS the average time of a query in service.
     */
    public void computeAverageTimeW(double averageTimeWQ, double averageTimeWS) {
        averageTimeW = averageTimeWQ + averageTimeWS;
    }

    /**
     * Calculates the average time a query was in queue and assigns the result to its global variable.
     *
     * @param queryList list that contains all the queries that passed through *this.
     */
    public abstract double computeAverageTimeInQueue(List<Query> queryList);

    /**
     * Calculates the average time a query was in service and assigns the result to its global variable.
     *
     * @param queryList list that contains all the queries that passed through *this.
     */
    public abstract double computeAverageTimeInService(List<Query> queryList);

    /**
     * Calculates and stores the effective service rate for the module.
     */
    public double computeAverageServiceTimeMu() {
        return 1 / averageTimeInService;
    }

    /**
     * Decides whether query has to wait in queue or can be served immediately.
     *
     * @param query specific query that's being processed in the module.
     */
    public abstract void processArrival(Query query);

    /**
     * Manages the query's exit from the module once it's done being served.
     *
     * @param query specific query that was being processed in the module.
     */
    public abstract void processDeparture(Query query);

    /**
     * Hunts and kills a query inside the module in case the event containing the query indicates so.
     *
     * @param query specific query to be killed.
     */
    public abstract void processKill(Query query);

    /**
     * Creates an arrival event (and a kill event in the first module) of a
     * query to *this and inserts it to the system's event list
     *
     * @param query specific query that arrives to the module.
     */
    public abstract void generateServiceEvent(Query query);

    /**
     * Verifies if all the servers inside the module are busy.
     *
     * @return true if they are all busy, false otherwise.
     */
    public abstract boolean isBusy();

    /**
     * Fetches the variable that contains the amount of free servers.
     *
     * @return the amount of free servers in *this.
     */
    public abstract int getNumberOfFreeServers();

    /**
     * Shows the amount of queries that are in *this's queue.
     *
     * @return how many queries are in queue.
     */
    public int getQueueSize() {
        return queue.size();
    }


    protected boolean hasQueue = true;

    protected int counterArrivals = 0;

    public double computeRealLambda() {
        java.lang.System.out.println("Número de llegadas " + counterArrivals);
        java.lang.System.out.println("Tiempo de simulación " + this.simulation.getTotalTimeSimulation());
        java.lang.System.out.println("Promedio de arribos" + this.simulation.getTotalTimeSimulation());
        averageArrivalTimeLambda = counterArrivals / this.simulation.getTotalTimeSimulation();
        return averageArrivalTimeLambda;
    }

    /**
     * Uses the basic formula of lambda/mu in order to store its value.
     */
    public double computeAverageOccupiedTimeRho(double lambda) {
        return lambda / (servers * averageServiceTimeMu);
    }

    /**
     * Calculates factorial numbers for statistical purposes.
     *
     * @param factorialOf the number to be used to calculate the factorial.
     * @return the number's factorial.
     */
    public double computeFactorial(int factorialOf) {
        double factorial = 1;
        for (int i = 1; i <= factorialOf; i++) {
            factorial *= i;
        }
        return factorial;
    }


    /**
     * Calculates the probability that all servers are idle.
     *
     * @param rho the module's traffic.
     * @return the probability that all servers are idle.
     */
    public double computePi0(double rho) {
        double pi0;
        int servers = this.servers;
        if (servers == 1) {
            pi0 = 1 - rho;

        } else {
            double valOfSumatory = 0;
            for (int i = 0; i <= servers - 1; i++) {
                valOfSumatory += Math.pow((servers * rho), i) / computeFactorial(i);
            }
            pi0 = 1 / (valOfSumatory + (Math.pow((servers * rho), servers) / (computeFactorial(servers) * (1 - rho))));
        }

        return pi0;
    }

    /**
     * Used to calculate average time in system and average time in queue when *this has more than one server.
     *
     * @param rho the module's traffic.
     * @param pi0 the probability that there are cero queries being served.
     * @return
     */
    public double computeProbabilityThatNBeMajorThanS(double rho, double pi0) {
        int servers = this.servers;
        return ((Math.pow((rho * servers), servers) * pi0)) / (computeFactorial(servers) * (1 - rho));
    }

    /**
     * Calculates the mean number of queries in queue.
     *
     * @param lambda   customer arrival rate.
     * @param mu       customer service rate.
     * @param hasQueue whether the module has a queue or not.
     * @return the mean number of queries in queue.
     */
    public double computeLq(double lambda, double mu, boolean hasQueue) {
        double lq = 0;
        if (hasQueue) {
            int servers = this.servers;
            if (servers == 1) {
                lq = (Math.pow(lambda, 2)) / (mu * (mu - lambda));

            } else {
                double rho = lambda / (servers * mu);
                double pi0 = computePi0(rho);
                lq = (computeProbabilityThatNBeMajorThanS(rho, pi0) * rho) / (1 - rho);
            }

        }
        return lq;
    }

    /**
     * Calculates the mean number of queries in service.
     *
     * @param lambda customer arrival rate.
     * @param mu     customer service rate.
     * @return the mean number of queries in service.
     */
    public double computeLs(double lambda, double mu) {
        return lambda / mu;
    }

    /**
     * Calculates the average time a query spends in queue.
     *
     * @param lambda   customer arrival rate.
     * @param mu       customer service rate.
     * @param hasQueue whether the module has a queue or not.
     * @return the average time a query spends in queue.
     */
    public double computeWq(double lambda, double mu, boolean hasQueue) {
        double wq = 0;
        if (hasQueue) {
            int servers = this.servers;
            if (servers == 1) {
                wq = lambda / (mu * (mu - lambda));

            } else {
                double rho = lambda / (mu * servers);
                double pi0 = computePi0(rho);
                wq = computeProbabilityThatNBeMajorThanS(rho, pi0) / ((servers * mu) - lambda);
            }
        }
        return wq;
    }

    /**
     * Calculates the amount of time that the server(s) were idle.
     *
     * @return the amount of time the servers were idle.
     */
    public double getIdleTime() {
        return totalIdleTime;
    }

    /**
     * Uses a query list in order to accumulate the total amount of DDL queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */
    public abstract void computeDdlAvgTime(List<Query> queryList);

    /**
     * Uses a query list in order to accumulate the total amount of Update queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */
    public abstract void computeUpdateAvgTime(List<Query> queryList);

    /**
     * Uses a query list in order to accumulate the total amount of Join queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */
    public abstract void computeJoinAvgTime(List<Query> queryList);

    /**
     * Uses a query list in order to accumulate the total amount of Select queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */
    public abstract void computeSelectAvgTime(List<Query> queryList);

    /**
     * Calculates all the statistics using their respective methods and inserts the values in a Statistics class.
     *
     * @param lambda the mean arrival rate
     */
    public void fillStatistics(double lambda, boolean isTheLastModule) {
        if (!isTheLastModule) {
            this.averageTimeInService = this.computeAverageTimeInService(simulation.getClientConnectionModule().getAllQueries());//1/mu (Ws)
            //this.avgTimeInQueue = this.computeWq(lambda, averageServiceTimeMu, this.hasQueue);
            this.avgTimeInQueue = this.computeAverageTimeInQueue(simulation.getClientConnectionModule().getAllQueries());//Wq
            this.averageServiceTimeMu = this.computeAverageServiceTimeMu();

            this.averageOccupiedTimeRho = computeAverageOccupiedTimeRho(lambda);
            this.computeAverageTimeW(avgTimeInQueue, averageTimeInService);

            this.averageQueriesInService = this.computeLs(lambda, averageServiceTimeMu);
            this.averageQueriesInQueue = this.computeLq(lambda, averageServiceTimeMu, this.hasQueue);
            this.averageQueriesL = this.computeAverageQueriesL(averageQueriesInQueue, averageQueriesInService);

            this.computeDdlAvgTime(simulation.getClientConnectionModule().getAllQueries());
            this.computeUpdateAvgTime(simulation.getClientConnectionModule().getAllQueries());
            this.computeSelectAvgTime(simulation.getClientConnectionModule().getAllQueries());
            this.computeJoinAvgTime(simulation.getClientConnectionModule().getAllQueries());

            ModuleStatistics moduleStatistics = new ModuleStatistics(this);
            this.setModuleStatistics(moduleStatistics);
        }
    }

    public double getUpdateAvgTime() {
        return updateAvgTime;
    }

    public double getDdlAvgTime() {
        return ddlAvgTime;
    }

    public double getJoinAvgTime() {
        return joinAvgTime;
    }

    public double getSelectAvgTime() {
        return selectAvgTime;
    }

    public double getAverageOccupiedTimeRho() {
        return averageOccupiedTimeRho;
    }

    public int getTotalProcessedQueries() {
        return totalProcessedQueries;
    }

    public double getAvgTimeInQueue() {
        return avgTimeInQueue;
    }

    public void setModuleStatistics(ModuleStatistics moduleStatistics) {
        this.moduleStatistics = moduleStatistics;
    }

    public double getAverageServiceTimeMu() {
        return averageServiceTimeMu;
    }

    public double getAverageQueriesL() {
        return averageQueriesL;
    }

    public double getAverageQueriesInQueue() {
        return averageQueriesInQueue;
    }

    public double getAverageQueriesInService() {
        return averageQueriesInService;
    }

    public double getAverageTimeW() {
        return averageTimeW;
    }
}
