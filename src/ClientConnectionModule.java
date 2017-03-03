import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This module controls the amount of concurrent connections held within the system and also controls both the
 * entry and exit of queries in the system.
 */
public class ClientConnectionModule extends Module {

    //////////////////////////////////////////// BEFORE THE QUERY IS PROCESSED ///////////////////////////////////////
    /**
     * Average number of arrivals to the system per time second.
     */
    public static final double LAMBDA = 0.58333333;

    /**
     * A list of all the queries that have entered successfully to the system.
     */
    private List<Query> allQueries;

    /**
     * User defined parameter of the amount of concurrent connections the system can handle.
     */
    private int kConnections;

    /**
     * Counter that measures the amount of connections rejected by the system.
     */
    private int rejectedConnections;

    /**
     * The amount of connections managed by the system in a specific moment.
     */
    private int currentConnections;

    /**
     * Each query is tagged with an ID (name defined by the number of query that manages to connect into the system).
     * Essentially, this variable traces the current query number.
     */
    private int currentId;

    /**
     * Mainly for statistical purposes. Represents the mean of how much time passes until the query gets solved.
     */
    private double averageQueryLifetime;

    public ClientConnectionModule(Simulation simulation, Module nextModule, int kConnections) {
        this.simulation = simulation;
        this.nextModule = nextModule;
        this.kConnections = kConnections;
        allQueries = new LinkedList<>();
        queue = new LinkedBlockingQueue<>();
        currentId = 1;
        rejectedConnections = 0;
        currentConnections = 0;
        idleTime = 0;
        totalIdleTime = 0;
        averageQueryLifetime = 0;
        totalProcessedQueriesFromLastModule = 0;
        totalProcessedQueries = 0;
        servers = kConnections;
        hasQueue = false;
        counterArrivalsToLastModule = 0;
    }

    /**
     * Decides whether query has to wait in queue or can be served immediately.
     *
     * @param query specific query that's being processed in the module.
     */
    @Override
    public void processArrival(Query query) {
        if (query.isSolved()) {
            processArrivalLastModule(query);

        } else {
            processArrivalFirstModule(query);
            if (currentConnections == 0)
                totalIdleTime += simulation.getClock() - idleTime;
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
        if (query == null) {
            currentId++;
            query = new Query(currentId, simulation.getClock(), DistributionGenerator.generateType(),
                    ModuleType.CLIENT_CONNECTION_MODULE);
        }
        if (query.isSolved()) {
            simulation.addEvent(new Event(simulation.getClock(), query,
                    EventType.ARRIVAL, ModuleType.CLIENT_CONNECTION_MODULE));
        } else {
            double nextArrivalTime = DistributionGenerator.getNextArrivalTime(LAMBDA);
            simulation.addEvent(new Event(simulation.getClock() + nextArrivalTime, query,
                    EventType.ARRIVAL, ModuleType.CLIENT_CONNECTION_MODULE));
            Event killEvent = new Event(simulation.getClock() + nextArrivalTime + simulation.getTimeout(), query,
                    EventType.KILL, null);
            simulation.addEvent(killEvent);
            simulation.getKillEventsTable().put(query.getId(), killEvent);
        }
    }

    /**
     * Manages the query's exit from the module once it's done being served.
     *
     * @param query specific query that was being processed in the module.
     */
    @Override
    public void processDeparture(Query query) {
        if (query.isSolved())
            processDepartureOfSystem(query);

        else
            processDepartureToNextModule(query);

    }

    /**
     * Verifies if all the servers inside the module are busy.
     *
     * @return true if they are all busy, false otherwise.
     */
    @Override
    public boolean isBusy() {
        return currentConnections == kConnections;
    }

    /**
     * Hunts and kills a query inside the module in case the event containing the query indicates so.
     *
     * @param query specific query to be killed.
     */
    @Override
    public void processKill(Query query) {
        if (!query.isSolved())
            query.setKill(true);
    }

    /**
     * Fetches the variable that contains the amount of free servers.
     *
     * @return the amount of free servers in *this.
     */
    @Override
    public int getNumberOfFreeServers() {
        return kConnections - currentConnections;
    }

    /**
     * Shows the amount of queries that are in *this's queue.
     *
     * @return how many queries are in queue.
     */
    @Override
    public int getQueueSize() {
        return 0;
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
                double arrivalTime = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
                totalTime += (exitTime - arrivalTime);
                counter++;

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
                double arrivalTime = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
                totalTime += (exitTime - arrivalTime);
                counter++;

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
                double arrivalTime = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
                totalTime += (exitTime - arrivalTime);
                counter++;

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
                double arrivalTime = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
                totalTime += (exitTime - arrivalTime);
                counter++;

            }
        }
        this.selectAvgTime = totalTime / counter;
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

        return 0;
    }

    /**
     * Calculates the average time a query was in service and assigns the result to its global variable.
     *
     * @param queryList list that contains all the queries that passed through *this.
     */
    @Override
    public double computeAverageTimeInService(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        int counter = 0;
        double totalTime = 0;

        while (iterator.hasNext()) {
            Query query = iterator.next();
            double entryTimeToServer = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToServer();
            double exitTimeFromServer = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
            double totalTimeInServer = exitTimeFromServer - entryTimeToServer;
            if (totalTimeInServer > 0) {
                counter++;
                totalTime += totalTimeInServer;
            }
        }
        return totalTime / counter;
    }

    public double computeAverageTimeInServiceInLastModule(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        int counter = 0;
        double totalTime = 0;

        while (iterator.hasNext()) {
            Query query = iterator.next();
            double entryTimeToServer = query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfEntryToServer();
            double exitTimeFromServer = query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfExitFromModule();
            double totalTimeInServer = exitTimeFromServer - entryTimeToServer;
            if (totalTimeInServer > 0) {
                counter++;
                totalTime += totalTimeInServer;
            }
        }
        return totalTime / counter;
    }

    /**
     * Decides what to do with the query in case it's unresolved.
     *
     * @param query specific unresolved query
     */
    private void processArrivalFirstModule(Query query) {
        if (isBusy())
            rejectedConnections++;
        else {
            counterArrivals++;
            currentConnections++;
            double time = getNextExitTime();
            simulation.addEvent(new Event(simulation.getClock() + time, query,
                    EventType.EXIT, ModuleType.CLIENT_CONNECTION_MODULE));

            query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().setTimeOfEntryToModule(simulation.getClock());
            query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().setTimeOfEntryToServer(simulation.getClock());
            query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().setTimeOfExitFromModule(simulation.getClock() + time);
            allQueries.add(query);
        }
        generateServiceEvent(null);
    }

    /**
     * Decides what to do with the query in case it's resolved.
     *
     * @param query specific resolved query.
     */
    private void processArrivalLastModule(Query query) {
        counterArrivalsToLastModule++;
        double time = getResultantTime(query.getNumberOfBlocks());
        simulation.addEvent(new Event(time + simulation.getClock(),
                query, EventType.EXIT, ModuleType.CLIENT_CONNECTION_MODULE));
        query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().setTimeOfEntryToModule(this.simulation.getClock());
        query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().setTimeOfEntryToServer(this.simulation.getClock());

        query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().setTimeOfExitFromModule(this.simulation.getClock() + time);

    }

    /**
     * Creates the first event and places it in this simulation in order to start in execution time.
     */
    public void generateFirstArrival() {
        currentId++;
        Query query = new Query(currentId, simulation.getClock(), DistributionGenerator.generateType(),
                ModuleType.CLIENT_CONNECTION_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query,
                EventType.ARRIVAL, ModuleType.CLIENT_CONNECTION_MODULE));

    }

    /**
     * Handles the unresolved query's departure to the next module.
     *
     * @param query specific unresolved query.
     */
    private void processDepartureToNextModule(Query query) {
        //query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().setTimeOfExitFromModule(simulation.getClock());
        totalProcessedQueries++;
        if (!query.isKill()) {
            nextModule.generateServiceEvent(query);

        } else {
            currentConnections--;
        }

    }

    /**
     * Handles the resolved query's departure out of the system.
     *
     * @param query specific resolved query.
     */
    private void processDepartureOfSystem(Query query) {
        currentConnections--;
        totalProcessedQueriesFromLastModule++;
        if (currentConnections == 0)
            idleTime = simulation.getClock();
        query.setTotalTime(simulation.getClock() - query.getTimeOfEntry());
        Event eventToRemove = simulation.getKillEventsTable().get(query.getId());
        simulation.getEventList().remove(eventToRemove);


    }

    /**
     * Generates the exit time of the module using the uniform distribution random number generator.
     *
     * @return the random value.
     */
    public double getNextExitTime() {
        return DistributionGenerator.getNextRandomValueByUniform(0.01, 0.05);
    }

    /**
     * Creates the time the query takes to display its results to the user.
     *
     * @param numberOfBlocks the amount of blocks that had to be loaded for the query.
     * @return the resultant time.
     */
    public double getResultantTime(int numberOfBlocks) {
        double average = numberOfBlocks / 3;
        return average / 2;
    }

    /**
     * Calculates the mean of the times each query lasted in the system, whether it was solved
     * or not. Mainly for statistical purposes.
     *
     * @param queryList the list containing all the resolved queries.
     */
    public void computeAverageQueryLifetime(List<Query> queryList) {
        double avgConnectionLife = 0;
        int size = queryList.size();
        for (int i = 0; i < size; i++) {
            avgConnectionLife += queryList.get(i).getTotalTime();
        }

        averageQueryLifetime = avgConnectionLife / size;
    } //solo primer modulo

    public List<Query> getAllQueries() {
        return allQueries;
    }

    public double getAverageQueryLifetime() {
        return averageQueryLifetime;
    }

    public int getRejectedConnections() {
        return rejectedConnections;
    }

    public int getCurrentConnections() {
        return currentConnections;
    }

    public void setCurrentConnections(int currentConnections) {
        this.currentConnections = currentConnections;
    }

    //////////////////////////////////////////// AFTER THE QUERY IS PROCESSED ///////////////////////////////////////

    /**
     * Stores the average amount of queries from its respective module. Mainly used for statistical purposes.
     */
    private double averageQueriesLInLastModule;

    /**
     * Stores the average amount of queries in queue from its respective module. Mainly used for statistical purposes.
     */
    private double averageQueriesInQueueInLastModule;

    /**
     * Stores the average amount of queries in service from its respective module. Mainly used for statistical purposes.
     */
    private double averageQueriesInServiceInLastModule;

    /**
     * Stores the average amount of time per query from its respective module. Mainly used for statistical purposes.
     */
    private double averageTimeWInLastModule;

    /**
     * Stores the average amount of time in queue from its respective module. Mainly used for statistical purposes.
     */
    private double averageTimeInQueueInLastModule;

    /**
     * Stores the average amount of time in service from its respective module. Mainly used for statistical purposes.
     */
    private double averageTimeInServiceInLastModule;

    /**
     * The effective service rate in its respective module.
     */
    protected double averageServiceTimeMuInLastModule;

    /**
     * Counter that verifies how many queries managed to exit the last module.
     */
    private int totalProcessedQueriesFromLastModule;

    /**
     * Average time a DDL query type lasts inside the module.
     */
    private double ddlAvgTimeInLastModule;

    /**
     * Average time an Update query type lasts inside the module.
     */
    private double updateAvgTimeInLastModule;

    /**
     * Average time a Join query type lasts inside the module.
     */
    private double joinAvgTimeInLastModule;

    /**
     * Average time a Select query type lasts inside the module.
     */
    private double selectAvgTimeInLastModule;

    /**
     * Tracks down the amount of queries that arrive successfully into the module.
     */
    private int counterArrivalsToLastModule;

    /**
     * Stores the average occupied time in the module, only treating queries that have been solved.
     */
    private double averageOccupiedTimeRhoInLastModule;

    /**
     * Used to store all statistics from this module, considering the query has been already solved
     */
    private ModuleStatistics moduleStatisticsOfLastModule;

    public void setModuleStatisticsOfLastModule(ModuleStatistics moduleStatisticsOfLastModule) {
        this.moduleStatisticsOfLastModule = moduleStatisticsOfLastModule;
    }

    /**
     * Generates a new mean arrival rate value base on the current values, in case the previous module is unstable.
     * @return the new mean arrival rate value
     */
    public double computeRealLambdaToLastModule() {
        return counterArrivalsToLastModule / simulation.getTotalTimeSimulation();
    }

    /**
     * Uses a query list in order to accumulate the total amount of DDL queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */

    public void computeDdlAvgTimeInLastModule(List<Query> queryList) {
        double totalTime = 0;
        int counter = 0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()) {
            Query query = iterator.next();
            if (query.getQueryType() == QueryType.DDL) {
                double arrivalTime = query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfExitFromModule();
                totalTime += (exitTime - arrivalTime);
                counter++;

            }
        }
        if (counter == 0) {
            this.ddlAvgTimeInLastModule = 0;
        } else
            this.ddlAvgTimeInLastModule = totalTime / counter;

    }

    /**
     * Uses a query list in order to accumulate the total amount of Update queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */

    public void computeUpdateAvgTimeInLastModule(List<Query> queryList) {
        double totalTime = 0;
        int counter = 0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()) {
            Query query = iterator.next();
            if (query.getQueryType() == QueryType.UPDATE) {
                double arrivalTime = query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfExitFromModule();
                totalTime += (exitTime - arrivalTime);
                counter++;

            }
        }
        this.updateAvgTimeInLastModule = totalTime / counter;
    }

    /**
     * Uses a query list in order to accumulate the total amount of Join queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */

    public void computeJoinAvgTimeInLastModule(List<Query> queryList) {
        double totalTime = 0;
        int counter = 0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()) {
            Query query = iterator.next();
            if (query.getQueryType() == QueryType.JOIN) {
                double arrivalTime = query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfExitFromModule();
                totalTime += (exitTime - arrivalTime);
                counter++;

            }
        }
        this.joinAvgTimeInLastModule = totalTime / counter;
    }

    /**
     * Uses a query list in order to accumulate the total amount of Select queries'
     * time in the system and finds its mean.
     *
     * @param queryList the list of queries in the system.
     */
    public void computeSelectAvgTimeInLastModule(List<Query> queryList) {
        double totalTime = 0;
        int counter = 0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()) {
            Query query = iterator.next();
            if (query.getQueryType() == QueryType.SELECT) {
                double arrivalTime = query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfExitFromModule();
                totalTime += (exitTime - arrivalTime);
                counter++;

            }
        }
        this.selectAvgTimeInLastModule = totalTime / counter;
    }

    /**
     * Calculates all the statistics using their respective methods and inserts the values in a Statistics class.
     *
     * @param lambda the mean arrival rate
     */
    @Override
    public void fillStatistics(double lambda, boolean isTheLastModule) {
        if (!isTheLastModule)
            super.fillStatistics(lambda, false);
        else {
            averageTimeInServiceInLastModule = computeAverageTimeInServiceInLastModule(allQueries);
            averageServiceTimeMuInLastModule = computeAverageServiceTimeMu();
            averageTimeInQueueInLastModule = computeWq(lambda, averageServiceTimeMuInLastModule, false);
            averageOccupiedTimeRhoInLastModule = computeAverageOccupiedTimeRhoInLastModule(lambda);
            setAverageTimeWInLastModule(1 / averageServiceTimeMuInLastModule);
            averageQueriesInServiceInLastModule = computeLs(lambda, averageServiceTimeMuInLastModule);
            averageQueriesInQueueInLastModule = 0;
            averageQueriesLInLastModule = averageQueriesInServiceInLastModule;

            computeDdlAvgTimeInLastModule(this.allQueries);
            computeJoinAvgTimeInLastModule(this.allQueries);
            computeSelectAvgTimeInLastModule(this.allQueries);
            computeUpdateAvgTimeInLastModule(this.allQueries);

            ModuleStatistics moduleStatistics = new ModuleStatistics(this, true);
            setModuleStatisticsOfLastModule(moduleStatistics);
        }
    }

    public double getDdlAvgTimeInLastModule() {
        return ddlAvgTimeInLastModule;
    }

    public double getUpdateAvgTimeInLastModule() {
        return updateAvgTimeInLastModule;
    }

    public double getJoinAvgTimeInLastModule() {
        return joinAvgTimeInLastModule;
    }

    public double getSelectAvgTimeInLastModule() {
        return selectAvgTimeInLastModule;
    }

    public ModuleStatistics getModuleStatisticsOfLastModule() {
        return moduleStatisticsOfLastModule;
    }

    public double getAverageServiceTimeMuInLastModule() {
        return averageServiceTimeMuInLastModule;
    }

    public double computeAverageOccupiedTimeRhoInLastModule(double lambda) {
        return lambda / servers * averageServiceTimeMuInLastModule;
    }

    public void setAverageTimeWInLastModule(double averageTimeWInLastModule) {
        this.averageTimeWInLastModule = averageTimeWInLastModule;
    }

    public double getAverageQueriesLInLastModule() {
        return averageQueriesLInLastModule;
    }

    public double getAverageQueriesInServiceInLastModule() {
        return averageQueriesInServiceInLastModule;
    }

    public double getAverageTimeWInLastModule() {
        return averageTimeWInLastModule;
    }

    public double getAverageTimeInQueueInLastModule() {
        return averageTimeInQueueInLastModule;
    }

    public double getAverageOccupiedTimeRhoInLastModule() {
        return averageOccupiedTimeRhoInLastModule;
    }

    public int getTotalProcessedQueriesFromLastModule() {
        return totalProcessedQueriesFromLastModule;
    }

}

