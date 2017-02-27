import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This module assigns a thread to every single process that manages to enter the system.
 */
public class ProcessManagerModule extends Module {
    /**
     * Amount of system calls that are being processed at a specific moment.
     */
    private int currentSystemCalls;

    /**
     * Maximum amount of user-defined system calls.
     */
    private int availableSystemCalls;

    public ProcessManagerModule(Simulation simulation, Module nextModule, int availableSystemCalls) {
        this.simulation = simulation;
        this.nextModule = nextModule;
        this.queue = new LinkedBlockingQueue<>();
        busy = false;
        totalProcessedQueries = 0;
        currentSystemCalls = 0;
        this.availableSystemCalls = availableSystemCalls;
        servers = availableSystemCalls;

    }

    /**
     * Decides whether query has to wait in queue or can be served immediately.
     *
     * @param query specific query that's being processed in the module.
     */
    @Override
    public void processArrival(Query query) {
        query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToModule(simulation.getClock());
        if (this.isBusy()) {
            query.setIsInQueue(true);
            queue.offer(query);
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToQueue(simulation.getClock());
        } else {
            currentSystemCalls++;
            double normalValue = DistributionGenerator.getNextRandomValueByNormal(1.5, Math.sqrt(0.1));
            simulation.addEvent(new Event(simulation.getClock() + normalValue,
                    query, EventType.EXIT, ModuleType.PROCESS_MANAGER_MODULE));
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToServer(simulation.getClock());
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromModule(simulation.getClock()+normalValue);

            totalIdleTime += simulation.getClock() - idleTime;
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
        //query.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromModule(simulation.getClock());
        if (queue.size() > 0) {

            // 0.316227766 sqrt of 0.1
            double normalValue = DistributionGenerator.getNextRandomValueByNormal(1.5, Math.sqrt(0.1));
            Query quer = queue.poll();
            quer.setIsInQueue(false);
            simulation.addEvent(new Event(simulation.getClock() + normalValue,
                    quer, EventType.EXIT, ModuleType.PROCESS_MANAGER_MODULE));

            quer.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromQueue(simulation.getClock());
            quer.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToServer(simulation.getClock());
            quer.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromModule(simulation.getClock()+normalValue);

        } else {
            currentSystemCalls--;
            if (currentSystemCalls == 0)
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
        //Si está en cola, sacarlo
        if (query.getIsInQueue()) {
            queue.remove(query);
            int actualConnections = simulation.getClientConnectionModule().getCurrentConnections() - 1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
            //momento en que sale de la cola
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromQueue(simulation.getClock());
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromModule(simulation.getClock());


        } else {
            query.setKill(true);
        }
        //se quita del mapeo porque ya va a muere
        Event killEventToRemove = simulation.getKillEventsTable().get(query.getId());
        simulation.getKillEventsTable().remove(killEventToRemove);
    }

    /**
     * Verifies if all the servers inside the module are busy.
     *
     * @return true if they are all busy, false otherwise.
     */
    public boolean isBusy() {
        return availableSystemCalls == currentSystemCalls;
    }

    /**
     * Creates an arrival event (and a kill event in the first module) of a
     * query to *this and inserts it to the system's event list
     *
     * @param query specific query that arrives to the module.
     */
    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.PROCESS_MANAGER_MODULE);
        simulation.addEvent(new Event(simulation.getClock() , query, EventType.ARRIVAL, ModuleType.PROCESS_MANAGER_MODULE));
    }

    /**
     * Fetches the variable that contains the amount of free servers.
     *
     * @return the amount of free servers in *this.
     */
    @Override
    public int getNumberOfFreeServers() {
        return availableSystemCalls - currentSystemCalls;
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
                double arrivalTime = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
                double totalTimeInServer = exitTime-arrivalTime;
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
                double arrivalTime = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
                double totalTimeInServer = exitTime-arrivalTime;
                if (totalTimeInServer > 0) {
                    counter++;
                    totalTime += totalTimeInServer;
                }

            }

        }

        this.updateAvgTime = totalTime / counter;
        java.lang.System.out.println(updateAvgTime);
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
                double arrivalTime = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
                double totalTimeInServer = exitTime-arrivalTime;
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
                double arrivalTime = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
                double totalTimeInServer = exitTime-arrivalTime;
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
            double entryTimeToQueue = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToQueue();
            double exitTimeFromQueue = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromQueue();
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
        double totalTime = 0;
        int counter = 0;
        while (iterator.hasNext()) {
            Query query = iterator.next();
            double entryTimeToServer = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToServer();
            double exitTimeFromServer = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
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

}
