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
        timeQueue = new LinkedBlockingQueue<>();
        this.mSentences = mSentences;
        currentSentences = 0;
        hasBeenInQueue = 0;
    }



    @Override
    public void processArrival(Query query) {
        if (isBusy()) {
            queue.offer(query);
            query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToQueue(simulation.getClock());
        } else {
            if (currentSentences ==0)
                totalIdleTime += simulation.getClock() -idleTime;
            currentSentences++;
            double exitTime = simulation.getClock() + getTotalTime(query);
            simulation.addEvent(new Event(exitTime, query, EventType.EXIT, ModuleType.EXECUTION_MODULE));
            query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToServer(simulation.getClock());
            query.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromModule(exitTime);
        }
    }


    @Override
    public void processDeparture(Query query) {
        if(queue.size() > 0){
            double exitTime = simulation.getClock() + getTotalTime(query);
            simulation.addEvent(new Event(exitTime, queue.poll(), EventType.EXIT, ModuleType.EXECUTION_MODULE)); //TODO hora estaba mala, faltaba sumar el getTotalTime(query)
            query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToServer(simulation.getClock());
            query.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromModule(exitTime);
        }else {
            currentSentences--;
            if (currentSentences==0)
                idleTime=simulation.getClock();
        }
        query.setSolved(true);
        nextModule.generateServiceEvent(query);
    }

    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.EXECUTION_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.EXECUTION_MODULE));
        query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToModule(simulation.getClock());
        servedQueries++;
    }

    @Override
    public boolean isBusy(){
        return currentSentences == mSentences;
    }

    @Override
    public void processKill(Query query) {
        query.setTotalTime(simulation.getClock());
    }

    @Override
    public double getNextExitTime() {
        return 0;
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

    @Override
    public int getNumberOfFreeServers() {
        return mSentences - currentSentences;
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
                arrivalTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        return totalTime;
    }


    @Override
    public void setAverageTimeW(double avergeTimeWQ, double avergeTimeWS) {
        averageTimeW = avergeTimeWQ + avergeTimeWS;
    }

    @Override
    public void setAverageTimeInQueue(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        double totalTime = 0;
        while(iterator.hasNext()){
            Query temp = iterator.next();
            totalTime+= temp.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToServer()
                    - temp.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToQueue();
        }
        averageTimeInQueue = totalTime / queryList.size();
    }

    @Override
    public void setAverageTimeInService(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        double totalTime = 0;
        while(iterator.hasNext()){
            Query temp = iterator.next();
            totalTime+= temp.getQueryStatistics().getExecutionStatistics().getTimeOfExitFromModule()
                    - temp.getQueryStatistics().getExecutionStatistics().getTimeOfEntryToServer();
        }
        averageTimeInService = totalTime / queryList.size();
    }


    @Override
    public void setAverageQueriesL(double avergeQueriesLQ, double avergeQueriesLS) {
        averageQueriesL = avergeQueriesLQ + avergeQueriesLS;
    }

    @Override
    public void setAverageQueriesInQueue(List<Query> queryList) {
        averageQueriesInQueue = simulation.getClientConnectionModule().getLAMBDA() * averageTimeInQueue;
    }

    @Override
    public void setAverageQueriesInService(List<Query> queryList) {
        averageQueriesInService = simulation.getClientConnectionModule().getLAMBDA() * averageTimeInService;
    }





}
