import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
public class QueryProcessingModule extends Module {
    private int nAvailableProcesses;
    private int currentProcesses;

    public QueryProcessingModule(Simulation simulation, Module nextModule, int nAvailableProcesses) {
        this.simulation = simulation;
        this.nextModule = nextModule;
        queue = new LinkedBlockingQueue<>();
        timeQueue = new LinkedBlockingQueue<>();
        this.nAvailableProcesses = nAvailableProcesses;
        currentProcesses = 0;
        hasBeenInQueue = 0;
    }

    public int getnAvailableProcesses() {
        return nAvailableProcesses;
    }


    public int getCurrentProcesses() {
        return currentProcesses;
    }
    @Override
    public void processArrival(Query query) {
        if (isBusy()) {
            queue.offer(query);
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToQueue(simulation.getClock());
        } else {
            if(currentProcesses==0)
                totalIdleTime+= simulation.getClock()-idleTime;

            currentProcesses++;
            double exitTime = timeInQueryProcessingModule(query.getQueryType());

            simulation.addEvent(new Event(simulation.getClock() + exitTime,
                    query, EventType.EXIT, ModuleType.QUERY_PROCESSING_MODULE));

            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToServer(simulation.getClock());
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfExitFromModule(simulation.getClock() + exitTime);
        }
    }

    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.QUERY_PROCESSING_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.QUERY_PROCESSING_MODULE));
        query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToModule(simulation.getClock());
        servedQueries++;
    }

    //Se saca de la cola el siguiente y el query que llega de parámetro se envia al siguiente modulo
    @Override
    public void processDeparture(Query query) {
        if(queue.size()>0){
            double exitTime = timeInQueryProcessingModule(queue.peek().getQueryType());
            simulation.addEvent(new Event(simulation.getClock()+ exitTime,
                                queue.poll(), EventType.EXIT, ModuleType.QUERY_PROCESSING_MODULE));
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToServer(simulation.getClock());
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfExitFromModule(simulation.getClock() + exitTime);
        }else {
            currentProcesses--;
            if(currentProcesses==0)
                idleTime=simulation.getClock();
        }
        nextModule.generateServiceEvent(query);
    }

    @Override
    public void processKill(Query query) {
        query.setTotalTime(simulation.getClock());
    }

    @Override
    public boolean isBusy() {
       return nAvailableProcesses == currentProcesses;
    }

    @Override
    public double getNextExitTime() {
        return 0;
    }

    private double timeInQueryProcessingModule(QueryType query) {
        Random rnd = new Random();
        double totalTime = 0;
        double lexicalValidationTime = 0;
        double queryOptimizationTime = 0;
        double randomNumber = rnd.nextDouble();

        if (randomNumber < 0.7) {
            lexicalValidationTime = 0.1;
        } else {
            lexicalValidationTime = 0.4;
        }
        double syntacticalValidationTime = DistributionGenerator.getNextRandomValueByUniform(0, 0.8);
        double semanticValidationTime = DistributionGenerator.getNextRandomValueByNormal(1, 0.5);
        double permitVerificationTime = DistributionGenerator.getNextRandomValueByExponential(1 / 0.7);

        if (query.equals(QueryType.SELECT) || query.equals(QueryType.JOIN)) {
            queryOptimizationTime = 0.1;
        } else {
            queryOptimizationTime = 0.5;
        }
        totalTime = lexicalValidationTime + syntacticalValidationTime + semanticValidationTime + permitVerificationTime + queryOptimizationTime;
        return totalTime;
    }


    @Override
    public int getNumberOfFreeServers() {
        return  nAvailableProcesses-currentProcesses;
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
                arrivalTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
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
            totalTime+= temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToServer()
                    - temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToQueue();
        }
        averageTimeInQueue = totalTime / queryList.size();
    }

    @Override
    public void setAverageTimeInService(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        double totalTime = 0;
        while(iterator.hasNext()){
            Query temp = iterator.next();
            totalTime+= temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule()
                    - temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToServer();
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