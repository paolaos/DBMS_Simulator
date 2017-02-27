import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This module executes all the orders and petitions that the query carries.
 */
public class ExecutionModule extends Module {

    /**
     * The fixed amount of time taken for a DDL query type to execute.
     */
    private final double DDL_RESTRUCTRATION_TIME = 0.5;

    /**
     * The fixed amount of time taken for an Update query type to execute.
     */
    private final double UPDATE_RESTRUCTURATION_TIME = 1;

    /**
     * The maximum amount of sentences defined by the user in this module.
     */
    private int mSentences;

    /**
     * The amount of sentences being occupied at an instant.
     */
    private int currentSentences;

    public ExecutionModule(Simulation simulation, int mSentences) {
        this.simulation = simulation;
        queue = new LinkedBlockingQueue<>();
        this.mSentences = mSentences;
        currentSentences = 0;
        servers = mSentences;
    }

    /**
     * Decides whether query has to wait in queue or can be served immediately.
     *
     * @param query specific query that's being processed in the module.
     */
    @Override
    public void processArrival(Query query) {
        query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToModule(simulation.getClock());
        if (isBusy()) {
            query.setIsInQueue(true);
            queue.offer(query);
            query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToQueue(simulation.getClock());
        } else {
            if (currentSentences == 0)
                totalIdleTime += simulation.getClock() - idleTime;

            currentSentences++;
            double time= getTotalTime(query);
            double exitTime = simulation.getClock() +time;
            //java.lang.System.out.println(time);
            simulation.addEvent(new Event(exitTime, query, EventType.EXIT, ModuleType.EXECUTION_MODULE));
            query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToServer(simulation.getClock());
            query.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromModule(exitTime);

        }
    }

    /**
     * Manages the query's exit from the module once it's done being served.
     *
     * @param query specific query that was being processed in the module.
     */
    @Override
    public void processDeparture(Query query) {
        totalProcessedQueries++;
       // query.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromModule(simulation.getClock());
        if (queue.size() > 0) {
            double time=getTotalTime(query);
            double exitTime = simulation.getClock() + time;
           // java.lang.System.out.println(time);
            Query quer = queue.poll();
            quer.setIsInQueue(false);
            simulation.addEvent(new Event(exitTime, quer, EventType.EXIT, ModuleType.EXECUTION_MODULE));
            quer.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromQueue(simulation.getClock());
            quer.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToServer(simulation.getClock());
            quer.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromModule(exitTime);

        } else {
            currentSentences--;
            if (currentSentences == 0)
                idleTime = simulation.getClock();

        }

        if (!query.isKill()) {
            nextModule.generateServiceEvent(query);
            query.setSolved(true);

        } else {
            int actualConnections = simulation.getClientConnectionModule().getCurrentConnections() - 1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
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
        query.setCurrentModule(ModuleType.EXECUTION_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.EXECUTION_MODULE));
    }

    /**
     * Verifies if all the servers inside the module are busy.
     *
     * @return true if they are all busy, false otherwise.
     */
    @Override
    public boolean isBusy() {
        return currentSentences == mSentences;
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
            //momento en que sale de la cola
            query.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromQueue(simulation.getClock());
            query.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromModule(simulation.getClock());

            int actualConnections = simulation.getClientConnectionModule().getCurrentConnections() - 1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
        } else {
            //si es el que tiene bloqueado el sistema
            query.setKill(true);
        }
        //se quita del mapeo porque ya va a morir
        Event killEventToRemove = simulation.getKillEventsTable().get(query.getId());
        simulation.getKillEventsTable().remove(killEventToRemove);
    }

    /**
     * Fetches the variable that contains the amount of free servers.
     *
     * @return the amount of free servers in *this.
     */
    @Override
    public int getNumberOfFreeServers() {
        return mSentences - currentSentences;
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
                double arrivalTime = query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
                double totalTimeInServer = exitTime - arrivalTime;
                java.lang.System.out.println( exitTime + " - " + arrivalTime+" = " + (exitTime - arrivalTime)+"\n");
                if (totalTimeInServer > 0) {
                    counter++;
                    totalTime += totalTimeInServer;
                }

            }

        }
        if(counter == 0){
            this.ddlAvgTime = 0;
        }else
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
                double arrivalTime = query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
                double totalTimeInServer = exitTime - arrivalTime;
                java.lang.System.out.println( exitTime + " - " + arrivalTime+" = " + (exitTime - arrivalTime)+"\n");
                if (totalTimeInServer > 0) {
                    counter++;
                    totalTime += totalTimeInServer;
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
                double arrivalTime = query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
                double totalTimeInServer = exitTime - arrivalTime;
                java.lang.System.out.println( exitTime + " - " + arrivalTime+" = " + (exitTime - arrivalTime)+"\n");
                if (totalTimeInServer > 0) {
                    counter++;
                    totalTime += totalTimeInServer;
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
                double arrivalTime = query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
                double totalTimeInServer = exitTime - arrivalTime;
                java.lang.System.out.println( exitTime + " - " + arrivalTime+" = " + (exitTime - arrivalTime)+"\n");
                if (totalTimeInServer > 0) {
                    counter++;
                    totalTime += totalTimeInServer;
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
    public void computeAverageTimeInQueue(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        int counter = 0;
        double totalTime = 0;

        while (iterator.hasNext()) {
            Query query = iterator.next();
            double entryTimeToQueue = query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToQueue();
            double exitTimeFromQueue = query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromQueue();
            double totalTimeInQueue = exitTimeFromQueue - entryTimeToQueue;
            if (totalTimeInQueue > 0) {
                counter++;
                totalTime += totalTimeInQueue;
            }
        }
        averageTimeInService = totalTime / counter;
    }

    /**
     * Calculates the average time a query was in service and assigns the result to its global variable.
     *
     * @param queryList list that contains all the queries that passed through *this.
     */
    @Override
    public void computeAverageTimeInService(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        int counter = 0;
        double totalTime = 0;

        while (iterator.hasNext()) {
            Query query = iterator.next();
            double entryTimeToServer = query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToServer();
            double exitTimeFromServer = query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
            double totalTimeInServer = exitTimeFromServer - entryTimeToServer;
            if (totalTimeInServer > 0) {
                counter++;
                totalTime += totalTimeInServer;
            }
        }
        averageTimeInService = totalTime / counter;
    }

    /**
     * Calculates the average queries in the module and assigns the result to its global variable.
     *
     * @param averageQueriesLQ average amount of queries in queue
     * @param averageQueriesLS average amount of queries in service
     */
    @Override
    public void computeAverageQueriesL(double averageQueriesLQ, double averageQueriesLS) {
        averageQueriesL = averageQueriesLQ + averageQueriesLS;
    }

    /**
     * Calculates the average amount of queries in queue and assigns the result to its global variable.
     *
     * @param queryList list that contains all the queries that passed through *this.
     */
    @Override
    public void computeAverageQueriesInQueue(List<Query> queryList) {
        averageQueriesInQueue = ClientConnectionModule.LAMBDA * averageTimeInQueue;
    }

    /**
     * Calculates the average amount of queries in service and assigns the result to its global variable.
     *
     * @param queryList list that contains all of the queries that passed through *this.
     */
    @Override
    public void computeAverageQueriesInService(List<Query> queryList) {
        averageQueriesInService = ClientConnectionModule.LAMBDA * averageTimeInService;
    }

    /**
     * Calculates the amount of time for a group of blocks to execute.
     *
     * @param numberOfBlocks the amount of blocks to be executed.
     * @return the time it takes to execute them.
     */
    public double getBlockExecutingTime(int numberOfBlocks) {
        return Math.pow(numberOfBlocks, 2) / 1000;
    }

    /**
     * Calculates the total time a query's blocks get executed and restructured.
     *
     * @param query the specific query to be executed.
     * @return the total time it takes for a query's blocks to get executed and restructured.
     */
    public double getTotalTime(Query query) {
        double totalTime = this.getBlockExecutingTime(query.getNumberOfBlocks());
        totalTime += getRestructurationTime(query.getQueryType());
        return totalTime;
    }

    /**
     * Sums the special restructuration time in case the query type is DDL or Update.
     *
     * @param query the specific query to be analized.
     * @return 0 if the query doesn't fall in any of the two cases.
     */
    private double getRestructurationTime(QueryType query) {
        double time = 0;
        if (query == QueryType.DDL) {
            time = DDL_RESTRUCTRATION_TIME;
        } else if (query == QueryType.UPDATE) {
            time = UPDATE_RESTRUCTURATION_TIME;
        }
        return time;
    }

    public void setNextModule(Module nextModule) {
        this.nextModule = nextModule;
    }

    public int getCurrentProcesses() {
        return currentSentences;
    }


}