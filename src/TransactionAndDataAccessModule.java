import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This module is in charge of fetching data to and from the database.
 */
public class TransactionAndDataAccessModule extends Module {
    /**
     * Maximum amount of user-defined servers
     */
    private int pQueries;

    /**
     * Current amount of queries being processed concurrently at a specific time
     */
    private int currentProcessedQueries;

    /**
     * Defines if the entry to the servers is blocked (whenever there is a DDL-type query being processed)
     */
    private boolean blocked;

    /**
     * Copy of the DDL-type query that has to wait in queue until all the servers are vacant.
     */
    private Query pendingQuery;

    public TransactionAndDataAccessModule(Simulation simulation, Module nextModule, int pQueries) {
        this.simulation = simulation;
        this.nextModule = nextModule;
        queue = new PriorityQueue<>();
        this.pQueries = pQueries;
        currentProcessedQueries = 0;
        pendingQuery = null;
        blocked = false; //booleano para caso DDL
        servers = pQueries;
    }

    /**
     * Decides whether query has to wait in queue or can be served immediately.
     *
     * @param query specific query that's being processed in the module.
     */
    @Override
    public void processArrival(Query query) {
        query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToModule(simulation.getClock());
        if (currentProcessedQueries == 0)
            totalIdleTime += simulation.getClock() - idleTime;

        //si está ocupado o bloqueado
        if (isBusy() || blocked) {
            //encole
            query.setIsInQueue(true);
            queue.offer(query);
            query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToQueue(simulation.getClock());
        } else {
            //en caso de que pueda atender

            //Si la consulta es DDL
            if (query.getQueryType() == QueryType.DDL) {
                //Bloquee el Sistema y almacene cual es la consulta por hacer
                blocked = true;
                pendingQuery = query;
                if (currentProcessedQueries == 0) {
                    // si la consulta es DDL pero no hay queries
                    currentProcessedQueries++;
                    // Agregar el tiempo Respectivo que se debe sumar al clock
                    double time=getTotalTime(pendingQuery);
                    simulation.addEvent(new Event(simulation.getClock() + time,
                            pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                    pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock());
                    pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromModule(simulation.getClock() + time);
                }

            } else {
                // si la consulta no es DDL => atienda
                currentProcessedQueries++;
                // Agregar el tiempo Respectivo que se debe sumar al clock
                double time=getTotalTime(query);
                simulation.addEvent(new Event((simulation.getClock() +time) ,
                        query, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE)); //REVISAR
                query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock());
                query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromModule(simulation.getClock()+time);

            }
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
        query.setCurrentModule(ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));

    }

    /**
     * Manages the query's exit from the module once it's done being served.
     *
     * @param query specific query that was being processed in the module.
     */
    @Override
    //Si el que sale es DDL y el que sigue no es DDL, entonces desbloquear,
    public void processDeparture(Query query) {
        totalProcessedQueries++;
        //query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromModule(simulation.getClock());
        if (query.getQueryType() == QueryType.DDL) {
            blocked = false;
        }
        currentProcessedQueries--;
        if (queue.size() > 0) {
            if (!blocked) {
                //agregar tiempo que suma al clock
                //que se pueda, que hayan y que el siguiente no sea DDL
                while (currentProcessedQueries < pQueries && queue.size() > 0 && queue.peek().getQueryType() != QueryType.DDL) {
                    Query quer = queue.poll();
                    quer.setIsInQueue(false);
                    double time=getTotalTime(quer);
                    simulation.addEvent(new Event(simulation.getClock() + time, quer, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                    currentProcessedQueries++;
                    quer.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
                    quer.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock());
                }
                if (queue.size() > 0 && queue.peek().getQueryType() == QueryType.DDL) {
                    blocked = true;
                    pendingQuery = queue.poll();
                    if (currentProcessedQueries == 0) {
                        double time=getTotalTime(pendingQuery);
                        simulation.addEvent(new Event(simulation.getClock() + time, pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                        currentProcessedQueries++;
                        pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
                        pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock());
                        pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromModule(simulation.getClock()+time);

                    }
                }
            } else {
                double time=getTotalTime(pendingQuery);
                simulation.addEvent(new Event(simulation.getClock() + time,
                        pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));

                currentProcessedQueries++;
                pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
                pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock());
                pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromModule(simulation.getClock()+time);

                pendingQuery = null;
            }
        } else {
            if (currentProcessedQueries == 0) {
                if (blocked) {
                    //Ejecuta consulta pendinte

                    double time=getTotalTime(pendingQuery);
                    simulation.addEvent(new Event(simulation.getClock() + time,
                            pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                    currentProcessedQueries++;
                    pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
                    pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock());
                    pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromModule(simulation.getClock()+time);

                    pendingQuery = null;
                } else {
                    idleTime = simulation.getClock();
                }
            }
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
            //momento en que sale de la cola
            query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
            query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromModule(simulation.getClock());
            int actualConnections = simulation.getClientConnectionModule().getCurrentConnections() - 1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
        } else {
            //si es el que tiene bloqueado el sistema
            if (query == pendingQuery) {
                blocked = false;
                pendingQuery = null;
                query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
                query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromModule(simulation.getClock());


                int actualConnections = simulation.getClientConnectionModule().getCurrentConnections() - 1;
                simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
                //entonces está siendo procesado
            } else {
                //matar proceso en cambio de módulo
                query.setKill(true);
            }
        }
        Event killEventToRemove = simulation.getKillEventsTable().get(query.getId());
        simulation.getKillEventsTable().remove(killEventToRemove);
    }

    /**
     * Verifies if all the servers inside the module are busy.
     *
     * @return true if they are all busy, false otherwise.
     */
    @Override
    public boolean isBusy() {
        return pQueries == currentProcessedQueries;
    }

    /**
     * Fetches the variable that contains the amount of free servers.
     *
     * @return the amount of free servers in *this.
     */
    @Override
    public int getNumberOfFreeServers() {
        return pQueries - currentProcessedQueries;
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
                double arrivalTime = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromModule();
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
                double arrivalTime = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromModule();
                double totalTimeInServer = exitTime-arrivalTime;
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
                double arrivalTime = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromModule();
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
                double arrivalTime = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromModule();
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
     * Calculates the average queries in the module and assigns the result to its global variable.
     *
     * @param averageQueriesLQ average amount of queries in queue
     * @param averageQueriesLS average amount of queries in service
     */
    @Override
    public void computeAverageQueriesL(double averageQueriesLQ, double averageQueriesLS) {

    }

    /**
     * Calculates the average amount of queries in queue and assigns the result to its global variable.
     *
     * @param queryList list that contains all the queries that passed through *this.
     */
    @Override
    public void computeAverageQueriesInQueue(List<Query> queryList) {

    }

    /**
     * Calculates the average amount of queries in service and assigns the result to its global variable.
     *
     * @param queryList list that contains all of the queries that passed through *this.
     */
    @Override
    public void computeAverageQueriesInService(List<Query> queryList) {

    }

    /**
     * Calculates the average time a query was in *this and assigns the result to its global variable.
     *
     * @param averageTimeWQ the average time of a query in queue.
     * @param averageTimeWS the average time of a query in service.
     */
    @Override
    public void computeAverageTimeW(double averageTimeWQ, double averageTimeWS) {

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
            double entryTimeToQueue = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToQueue();
            double exitTimeFromQueue = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromQueue();
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
            double entryTimeToServer = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToServer();
            double exitTimeFromServer = query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromModule();
            double totalTimeInServer = exitTimeFromServer - entryTimeToServer;
            if (totalTimeInServer > 0) {
                counter++;
                totalTime += totalTimeInServer;
            }
        }
        averageTimeInService = totalTime / counter;
    }

    /**
     * Defines how many blocks have to be retrieved from the database to the query depending on its type.
     *
     * @param query specific type of query to study which case it belongs to
     * @return the number of blocks from the query type.
     */
    private int getBlockNumber(QueryType query) {
        int numberOfBlocks = 0;
        switch (query) {
            case DDL:
                numberOfBlocks = 0;
                break;

            case UPDATE:
                numberOfBlocks = 0;
                break;

            case JOIN:
                int x = (int) Math.nextUp(DistributionGenerator.getNextRandomValueByUniform((double) 1, (double) 16));
                int y = (int) Math.nextUp(DistributionGenerator.getNextRandomValueByUniform((double) 1, (double) 12));
                numberOfBlocks = x + y;
                break;

            case SELECT:
                numberOfBlocks = (int) Math.nextUp(DistributionGenerator.getNextRandomValueByUniform((double) 1, (double) 64));
                break;
        }
        return numberOfBlocks;
    }

    /**
     * Calculates the coordination time between actions.
     *
     * @return the execution coordination time.
     */
    public double getExecutionCoordinationTime() {
        return pQueries * 0.03;
    }

    /**
     * Calculates the time it takes for the system to load the blocks that the query demands.
     *
     * @param numberOfBlocks the amount of blocks the query asks for.
     * @return the time it takes for the system to load the information.
     */
    public double getBlockLoadingTime(int numberOfBlocks) {
        return numberOfBlocks * 0.1;
    }

    /**
     * Calculates the total time the query takes in service while asking and receiving
     * the information it demands.
     *
     * @param query the specific query to be considered.
     * @return the amount of time the query will be served
     */
    public double getTotalTime(Query query) {
        int blockNumber = getBlockNumber(query.getQueryType());
        query.setNumberOfBlocks(blockNumber);
        double totalTime = getExecutionCoordinationTime() + getBlockLoadingTime(blockNumber);
        return totalTime;
    }

    public int getCurrentProcesses() {
        return currentProcessedQueries;
    }
}