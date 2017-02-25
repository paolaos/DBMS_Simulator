import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ExecutionModule extends Module{
    private final double DDL_RESTRUCTRATION_TIME =  0.5;
    private final double UPDATE_RESTRUCTURATION_TIME = 1;
    private int mSentences;
    private int currentSentences;

    public ExecutionModule(Simulation simulation, int mSentences){
        this.simulation= simulation;
        queue = new LinkedBlockingQueue<>();
        this.mSentences = mSentences;
        currentSentences = 0;
        servers=mSentences;
    }



    @Override
    public void processArrival(Query query) {
        query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToModule(simulation.getClock());
        if (isBusy()) {
            query.setIsInQueue(true);
            queue.offer(query);
            query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToQueue(simulation.getClock());
        } else {
            if (currentSentences ==0)
                totalIdleTime += simulation.getClock() -idleTime;

            currentSentences++;
            double exitTime = simulation.getClock() + getTotalTime(query);
            simulation.addEvent(new Event(exitTime, query, EventType.EXIT, ModuleType.EXECUTION_MODULE));
            query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToServer(simulation.getClock());
        }
    }


    @Override
    public void processDeparture(Query query) {
        totalProcessedQueries++;
        query.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromModule(simulation.getClock());
        if(queue.size() > 0){
            double exitTime = simulation.getClock() + getTotalTime(query);
            Query quer = queue.poll();
            quer.setIsInQueue(false);
            simulation.addEvent(new Event(exitTime, quer, EventType.EXIT, ModuleType.EXECUTION_MODULE));
            quer.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromQueue(simulation.getClock());
            quer.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToServer(simulation.getClock());
        }else {
            currentSentences--;
            if (currentSentences==0)
                idleTime=simulation.getClock();

        }

        if (!query.isKill()) {
            nextModule.generateServiceEvent(query);
            query.setSolved(true);

        }else{
            int actualConnections=simulation.getClientConnectionModule().getCurrentConnections()-1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
        }
    }

    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.EXECUTION_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.EXECUTION_MODULE));
    }

    @Override
    public boolean isBusy(){
        return currentSentences == mSentences;
    }

    @Override
    public void processKill(Query query) {
        if(query.getIsInQueue()){
            queue.remove(query);
            //momento en que sale de la cola
            query.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromQueue(simulation.getClock());
            int actualConnections=simulation.getClientConnectionModule().getCurrentConnections()-1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
        }else {
            //si es el que tiene bloqueado el sistema
            query.setKill(true);
        }
        //se quita del mapeo porque ya va a morir
        Event killEventToRemove= simulation.getKillEventsTable().get(query.getId());
        simulation.getKillEventsTable().remove(killEventToRemove);
    }


    public double getBlockExecutingTime(int numberOfBlocks) {
        return Math.pow(numberOfBlocks, 2) / 1000;
    }


    public double getTotalTime(Query query){
        double totalTime = this.getBlockExecutingTime(query.getNumberOfBlocks());
        totalTime += getRestructurationTime(query.getQueryType());
        return totalTime;
    }

    public void setNextModule(Module nextModule){
        this.nextModule = nextModule;
    }

    private double getRestructurationTime(QueryType query) {
        double time = 0;
        if (query == QueryType.DDL) {
            time = DDL_RESTRUCTRATION_TIME;
        } else if (query == QueryType.UPDATE) {
            time = UPDATE_RESTRUCTURATION_TIME;
        }
        return time;
    }

    public  int getCurrentProcesses(){
        return currentSentences;
    }

    @Override
    public int getNumberOfFreeServers() {
        return mSentences - currentSentences;
    }

    @Override
    public int getQueueSize() {
        return queue.size();
    }

    @Override
    public double getIdleTime() {
        return totalIdleTime;
    }

    @Override
    public void computeDdlAvgTime(List<Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.DDL){
                arrivalTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        this.ddlAvgTime = totalTime;
    }

    @Override
    public void computeUpdateAvgTime(List <Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.UPDATE){
                arrivalTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }

        }
        this.updateAvgTime = totalTime;
    }

    @Override
    public void computeJoinAvgTime(List <Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.JOIN){
                arrivalTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        this.joinAvgTime = totalTime;
    }

    @Override
    public void computeSelectAvgTime(List <Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.SELECT){
                arrivalTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        this.selectAvgTime = totalTime;
    }

    @Override
    public void computeAverageTimeW(double averageTimeWQ, double averageTimeWS) {
        averageTimeW = averageTimeWQ + averageTimeWS;
    }



    @Override
    public void computeAverageTimeInQueue(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        int counter=0;
        double totalTime=0;

        while (iterator.hasNext()){
            Query query = iterator.next();
            double entryTimeToQueue=query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToQueue();
            double exitTimeFromQueue=query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromQueue();
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
            double entryTimeToServer=query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToServer();
            double exitTimeFromServer=query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
            double totalTimeInServer = exitTimeFromServer - entryTimeToServer;
            if(totalTimeInServer > 0){
                counter++;
                totalTime+=totalTimeInServer;
            }
        }
        averageTimeInService=totalTime/counter;
    }





    @Override
    public void computeAverageQueriesL(double averageQueriesLQ, double averageQueriesLS) {
        averageQueriesL = averageQueriesLQ + averageQueriesLS;
    }

    @Override
    public void computeAverageQueriesInQueue(List<Query> queryList) {
        averageQueriesInQueue = ClientConnectionModule.LAMBDA * averageTimeInQueue;
    }

    @Override
    public void computeAverageQueriesInService(List<Query> queryList) {
        averageQueriesInService = ClientConnectionModule.LAMBDA * averageTimeInService;
    }

}