import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This module validates whether the query is plausible and well written.
 */
public class QueryProcessingModule extends Module {
    /**
     * Maximum amount of user-defined processes
     */
    private int nAvailableProcesses;

    /**
     * Amount of processes in a specific instant.
     */
    private int currentProcesses;

    public QueryProcessingModule(Simulation simulation, Module nextModule, int nAvailableProcesses) {
        this.simulation = simulation;
        this.nextModule = nextModule;
        queue = new LinkedBlockingQueue<>();
        this.nAvailableProcesses = nAvailableProcesses;
        currentProcesses = 0;
        servers = nAvailableProcesses;
        moduleStatistics = new ModuleStatistics();
    }

    /**
     * Decides whether query has to wait in queue or can be served immediately.
     *
     * @param query specific query that's being processed in the module.
     */
    @Override
    public void processArrival(Query query) {
        counterArrivals++;
        query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToModule(simulation.getClock());
        if (isBusy()) {
            query.setIsInQueue(true);
            queue.offer(query);
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToQueue(simulation.getClock());
        } else {
            if (currentProcesses == 0)
                totalIdleTime += simulation.getClock() - idleTime;

            currentProcesses++;
            double exitTime = timeInQueryProcessingModule(query.getQueryType());
            simulation.addEvent(new Event(simulation.getClock() + exitTime,
                    query, EventType.EXIT, ModuleType.QUERY_PROCESSING_MODULE));
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToServer(simulation.getClock());
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfExitFromModule(simulation.getClock() + exitTime);
        }
    }

    /**
     * Creates an arrival event (and a kill event in the first module) of a
     * query to *this and inserts it to the system's event list
     *
     * @param query specific query that arrives to the module.
     */
    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.QUERY_PROCESSING_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.QUERY_PROCESSING_MODULE));
    }

    /**
     * Manages the query's exit from the module once it's done being served.
     *
     * @param query specific query that was being processed in the module.
     */
    @Override
    public void processDeparture(Query query) {
        totalProcessedQueries++;
        if (queue.size() > 0) {
            double exitTime = timeInQueryProcessingModule(queue.peek().getQueryType());
            Query query1 = queue.poll();
            query1.setIsInQueue(false);
            simulation.addEvent(new Event(simulation.getClock() + exitTime,
                    query1, EventType.EXIT, ModuleType.QUERY_PROCESSING_MODULE));
            query1.getQueryStatistics().getQueryProcessingStatistics().setTimeOfExitFromQueue(simulation.getClock());
            query1.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToServer(simulation.getClock());
            query1.getQueryStatistics().getQueryProcessingStatistics().setTimeOfExitFromModule(simulation.getClock() + exitTime);
        } else {
            currentProcesses--;
            if (currentProcesses == 0)
                idleTime = simulation.getClock();
        }

        if (!query.isKill()) {
            nextModule.generateServiceEvent(query);
        } else {
            int actualConnections = simulation.getClientConnectionModule().getCurrentConnections() - 1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
        }

    }

    /**
     * Hunts and kills a query inside the module in case the event containing the query indicates so.
     *
     * @param query specific query to be killed.
     */
    @Override
    public void processKill(Query query) {
        if (query.getIsInQueue()) {
            queue.remove(query);
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfExitFromQueue(simulation.getClock());
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfExitFromModule(simulation.getClock());

            int actualConnections = simulation.getClientConnectionModule().getCurrentConnections() - 1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);

        } else {
            query.setKill(true);
        }
        Event killEventToRemove = simulation.getKillEventsTable().get(query.getId());
        simulation.getKillEventsTable().remove(killEventToRemove);
        simulation.getEventList().remove(killEventToRemove);
    }

    /**
     * Verifies if all the servers inside the module are busy.
     *
     * @return true if they are all busy, false otherwise.
     */
    @Override
    public boolean isBusy() {
        return nAvailableProcesses == currentProcesses;
    }

    /**
     * Fetches the variable that contains the amount of free servers.
     *
     * @return the amount of free servers in *this.
     */
    @Override
    public int getNumberOfFreeServers() {
        return nAvailableProcesses - currentProcesses;
    }

    /**
     * Uses a query list in order to accumulate the total amount of DDL queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */
    @Override
    public void computeDdlAvgTime(List<Query> queryList) {
        double totalTime = 0;
        int counter = 0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()) {
            Query query = iterator.next();
            if (query.getQueryType() == QueryType.DDL) {
                double arrivalTime = query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
                double totalTimeInServer = exitTime - arrivalTime;
                if (totalTimeInServer > 0) {
                    counter++;
                    totalTime += totalTimeInServer;
                }

            }

        }
        if (counter == 0) {
            this.ddlAvgTime = 0;
        } else
            this.ddlAvgTime = totalTime / counter;
    }

    /**
     * Uses a query list in order to accumulate the total amount of Update queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */
    @Override
    public void computeUpdateAvgTime(List<Query> queryList) {
        double totalTime = 0;
        int counter = 0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()) {
            Query query = iterator.next();
            if (query.getQueryType() == QueryType.UPDATE) {
                double arrivalTime = query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
                if(exitTime - arrivalTime > 0) {
                    totalTime += (exitTime - arrivalTime);
                    counter++;
                }
            }

        }

        this.updateAvgTime = totalTime / counter;
    }

    /**
     * Uses a query list in order to accumulate the total amount of Join queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */
    @Override
    public void computeJoinAvgTime(List<Query> queryList) {
        double totalTime = 0;
        int counter = 0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()) {
            Query query = iterator.next();
            if (query.getQueryType() == QueryType.JOIN) {
                double arrivalTime = query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
                if(exitTime - arrivalTime > 0) {
                    totalTime += (exitTime - arrivalTime);
                    counter++;
                }

            }

        }

        this.joinAvgTime = totalTime / counter;
    }

    /**
     * Uses a query list in order to accumulate the total amount of Select queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */
    @Override
    public void computeSelectAvgTime(List<Query> queryList) {
        double totalTime = 0;
        int counter = 0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()) {
            Query query = iterator.next();
            if (query.getQueryType() == QueryType.SELECT) {
                double arrivalTime = query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
                if(exitTime - arrivalTime > 0) {
                    totalTime += (exitTime - arrivalTime);
                    counter++;
                }

            }

        }

        this.selectAvgTime = totalTime / counter;
    }

    /**
     * Calculates the average time a query was in *this and assigns the result to its global variable.
     *
     * @param averageTimeWQ the average time of a query in queue.
     * @param averageTimeWS the average time of a query in service.
     */
    @Override
    public void computeAverageTimeW(double averageTimeWQ, double averageTimeWS) {
        averageTimeW = averageTimeWQ + averageTimeWS;
    }

    /**
     * Calculates the average time a query was in queue and assigns the result to its global variable.
     *
     * @param queryList list that contains all the queries that passed through *this.
     */
    @Override
    public double computeAverageTimeInQueue(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        double totalTimeInQueue = 0;
        int counter = 0;
        while (iterator.hasNext()) {
            Query temp = iterator.next();
            double totalTime = temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromQueue()
                    - temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToQueue();
            if (totalTime > 0) {
                totalTimeInQueue += totalTime;
                counter++;
            }
        }
        return totalTimeInQueue / counter;
    }

    /**
     * Calculates the average time a query was in service and assigns the result to its global variable.
     *
     * @param queryList list that contains all the queries that passed through *this.
     */
    @Override
    public double computeAverageTimeInService(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        double totalTimeInService = 0;
        int counter = 0;
        while (iterator.hasNext()) {
            Query temp = iterator.next();
            double totalTime = temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule()
                    - temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToServer();
            if (totalTime > 0) {
                totalTimeInService += totalTime;
                counter++;
            }
        }
        return totalTimeInService / counter;
    }

    /**
     * Calculates the average queries in the module and assigns the result to its global variable.
     *
     * @param averageQueriesLQ average amount of queries in queue
     * @param averageQueriesLS average amount of queries in service
     */
    @Override
    public double computeAverageQueriesL(double averageQueriesLQ, double averageQueriesLS) {
        return averageQueriesLQ + averageQueriesLS;
    }


    /**
     * Calculates all the statistics using their respective methods and inserts the values in a Statistics class.
     *
     * @param lambda the mean arrival rate
     */

    /**
     * Calculates the amount of validation time of a specific query inside the Query Processing Module.
     *
     * @param query to be validated
     * @return the amount of time it took for the query to be validated
     */
    private double timeInQueryProcessingModule(QueryType query) {
        Random rnd = new Random();
        double totalTime;
        double lexicalValidationTime;
        double syntacticalValidationTime;
        double semanticValidationTime;
        double permitVerificationTime;
        double queryOptimizationTime;
        double randomNumber = rnd.nextFloat();

        if (randomNumber < 0.7) {
            lexicalValidationTime = 0.1;
        } else {
            lexicalValidationTime = 0.4;
        }
        syntacticalValidationTime = DistributionGenerator.getNextRandomValueByUniform(0, 0.8);
        semanticValidationTime = DistributionGenerator.getNextRandomValueByNormal(1, 0.5);
        permitVerificationTime = DistributionGenerator.getNextRandomValueByExponential(1 / 0.7);

        if (query.equals(QueryType.SELECT) || query.equals(QueryType.JOIN)) {
            queryOptimizationTime = 0.1;
        } else {
            queryOptimizationTime = 0.5;
        }
        totalTime = lexicalValidationTime + syntacticalValidationTime + semanticValidationTime + permitVerificationTime + queryOptimizationTime;
        return totalTime;
    }

    public int getCurrentProcesses() {
        return currentProcesses;
    }
}