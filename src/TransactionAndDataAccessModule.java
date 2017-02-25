import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class TransactionAndDataAccessModule extends Module {
    private int pQueries;
    private int currentProcessedQueries;
    private boolean blocked;
    private Query pendingQuery;



    public TransactionAndDataAccessModule(Simulation simulation, Module nextModule, int pQueries) {
        this.simulation = simulation;
        this.nextModule = nextModule;
        queue = new PriorityQueue<>();
        this.pQueries = pQueries;
        currentProcessedQueries = 0;
        pendingQuery = null;
        blocked = false; //booleano para caso DDL
    }

    @Override
    public void processArrival(Query query) {
        query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToModule(simulation.getClock());
        if(currentProcessedQueries==0)
            totalIdleTime += simulation.getClock()- idleTime;

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
                    simulation.addEvent(new Event(simulation.getClock() + (getBlockNumber(query.getQueryType()) * 0.1),
                            pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                    query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock() );

                }

            } else {
                // si la consulta no es DDL => atienda
                currentProcessedQueries++;
                // Agregar el tiempo Respectivo que se debe sumar al clock
                simulation.addEvent(new Event((simulation.getClock() + (getBlockNumber(query.getQueryType())) * 0.1),
                        query, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE)); //REVISAR
                query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock());

            }
        }
    }

    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));

        servedQueries++;
    }

    @Override
    //Si el que sale es DDL y el que sigue no es DDL, entonces desbloquear,
    public void processDeparture(Query query) {
        query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromModule(simulation.getClock());
        if (query.getQueryType() == QueryType.DDL) {
            blocked = false;
        }
        currentProcessedQueries--;
        if (queue.size() > 0) {
            if (!blocked) {
                //agregar tiempo que suma al clock
                //que se pueda, que hayan y que el siguiente no sea DDL
                while (currentProcessedQueries < pQueries && queue.size() > 0 && queue.peek().getQueryType() != QueryType.DDL) {
                    Query quer= queue.poll();
                    quer.setIsInQueue(false);
                    simulation.addEvent(new Event(simulation.getClock()+ (getBlockNumber(query.getQueryType())) * 0.1,quer, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                    currentProcessedQueries++;
                    quer.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
                    quer.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock());
                }
                if (queue.size() > 0 && queue.peek().getQueryType() == QueryType.DDL) {
                    blocked = true;
                    pendingQuery=queue.poll();
                    if (currentProcessedQueries == 0 ){
                        simulation.addEvent(new Event(simulation.getClock()+ (getBlockNumber(query.getQueryType())) * 0.1,pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                        currentProcessedQueries++;
                        pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
                        pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock());
                    }
                }
            }else{
                simulation.addEvent(new Event(simulation.getClock()+ (getBlockNumber(query.getQueryType())) * 0.1,
                        pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                pendingQuery = null;
                currentProcessedQueries++;
                pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
                pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock());
            }
        } else {
            if (currentProcessedQueries == 0 ){
                if(blocked) {
                //Ejecuta consulta pendinte
                    simulation.addEvent(new Event(simulation.getClock()+ (getBlockNumber(query.getQueryType())) * 0.1,
                        pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                        currentProcessedQueries++;
                pendingQuery = null;
                    pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
                    pendingQuery.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfEntryToServer(simulation.getClock());
            }else {
                idleTime=simulation.getClock();
                }
            }
        }

        if (!query.isKill()) {
            nextModule.generateServiceEvent(query);

        }else {
            int actualConnections=simulation.getClientConnectionModule().getCurrentConnections()-1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
        }
    }

    @Override
    public void processKill(Query query) {
        if(query.getIsInQueue()){
            queue.remove(query);
            //momento en que sale de la cola
            query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
            int actualConnections=simulation.getClientConnectionModule().getCurrentConnections()-1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
        }else {
            //si es el que tiene bloqueado el sistema
            if(query == pendingQuery){
                blocked=false;
                pendingQuery=null;
                query.getQueryStatistics().getTransactionAndDataAccessStatistics().setTimeOfExitFromQueue(simulation.getClock());
                int actualConnections=simulation.getClientConnectionModule().getCurrentConnections()-1;
                simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
                //entonces está siendo procesado
            }else {
                //matar proceso en cambio de módulo
                query.setKill(true);
            }
        }
        Event killEventToRemove= simulation.getKillEventsTable().get(query.getId());
        simulation.getKillEventsTable().remove(killEventToRemove);
    }

    @Override
    public boolean isBusy() {
        return pQueries == currentProcessedQueries;
    }

    @Override
    public double getNextExitTime() {
        return 0;
    }

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


    public double getExecutionCoordinationTime() {
        return pQueries * 0.03;
    }

    public double getBlockLoadingTime(int numberOfBlocks) {
        return numberOfBlocks * 0.1;
    }

    public double getTotalTime(Query query){
        int blockNumber = getBlockNumber(query.getQueryType());
        query.setNumberOfBlocks(blockNumber);
        double totalTime= getExecutionCoordinationTime()+getBlockLoadingTime(blockNumber);
        return  totalTime;
    }

    private double getTransactionTime(Query query){
        return 0;
    }

    @Override
    public int getNumberOfFreeServers() {
        return pQueries - currentProcessedQueries;
    }

    @Override
    public int getQueueSize() {
        return queue.size();
    }

    @Override
    public int getServedQueries() {
        return servedQueries;
    }

    @Override
    public double getIdleTime() {
        return totalIdleTime;
    }

    @Override
    public double getDdlAvgTime(List<Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.DDL){
                arrivalTime= query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        return totalTime;
    }

    @Override
    public double getUpdateAvgTime(List <Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.UPDATE){
                arrivalTime= query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }

        }
        return totalTime;
    }

    @Override
    public double getJoinAvgTime(List <Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.JOIN){
                arrivalTime= query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        return totalTime;
    }

    @Override
    public double getSelectAvgTime(List <Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.SELECT){
                arrivalTime= query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        return totalTime;
    }

    @Override
    public void computeAverageQueriesL(double averageQueriesLQ, double averageQueriesLS) {

    }

    @Override
    public void computeAverageQueriesInQueue(List<Query> queryList) {

    }

    @Override
    public void computeAverageQueriesInService(List<Query> queryList) {

    }

    @Override
    public void computeAverageTimeW(double averageTimeWQ, double averageTimeWS) {

    }

    @Override
    public void computeAverageTimeInQueue(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        int counter=0;
        double totalTime=0;

        while (iterator.hasNext()){
            Query query = iterator.next();
            double entryTimeToQueue=query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToQueue();
            double exitTimeFromQueue=query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromQueue();
            double totalTimeInQueue = exitTimeFromQueue - entryTimeToQueue;
            if(totalTimeInQueue > 0){
                counter++;
                totalTime+=totalTimeInQueue;
            }
        }
        averageTimeInService=totalTime/counter;
    }



    @Override
    public void computeAverageTimeInService(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        int counter=0;
        double totalTime=0;

        while (iterator.hasNext()){
            Query query = iterator.next();
            double entryTimeToServer=query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfEntryToServer();
            double exitTimeFromServer=query.getQueryStatistics().getTransactionAndDataAccessStatistics().getTimeOfExitFromModule();
            double totalTimeInServer = exitTimeFromServer - entryTimeToServer;
            if(totalTimeInServer > 0){
                counter++;
                totalTime+=totalTimeInServer;
            }
        }
        averageTimeInService=totalTime/counter;
    }
}