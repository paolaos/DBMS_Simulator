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
        this.nAvailableProcesses = nAvailableProcesses;
        currentProcesses = 0;
        servers=nAvailableProcesses;
    }

    public int getnAvailableProcesses() {
        return nAvailableProcesses;
    }

    public int getCurrentProcesses() {
        return currentProcesses;
    }
    @Override
    public void processArrival(Query query) {
        query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToModule(simulation.getClock());
        if (isBusy()) {
            query.setIsInQueue(true);
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

        }
    }

    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.QUERY_PROCESSING_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.QUERY_PROCESSING_MODULE));
    }

    //Se saca de la cola el siguiente y el query que llega de parámetro se envia al siguiente modulo
    @Override
    public void processDeparture(Query query) {
        totalProcessedQueries++;
        query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToModule(simulation.getClock());
        if(queue.size()>0){
            double exitTime = timeInQueryProcessingModule(queue.peek().getQueryType());
            Query query1 =queue.poll();
            query1.setIsInQueue(false);
            simulation.addEvent(new Event(simulation.getClock()+ exitTime,
                                query1, EventType.EXIT, ModuleType.QUERY_PROCESSING_MODULE));
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfExitFromQueue(simulation.getClock());
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToServer(simulation.getClock());
        }else {
            currentProcesses--;
            if(currentProcesses==0)
                idleTime=simulation.getClock();
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
        //Si está en cola, sacarlo
        if(query.getIsInQueue()){
            queue.remove(query);
            //momento en que sale de la cola
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfExitFromQueue(simulation.getClock());
            int actualConnections=simulation.getClientConnectionModule().getCurrentConnections()-1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);

        }else {
            //matar proceso en cambio de modulo
            query.setKill(true);
        }
            //quitar del mapeo
        Event killEventToRemove = simulation.getKillEventsTable().get(query.getId());
        simulation.getKillEventsTable().remove(killEventToRemove);

    }

    @Override
    public boolean isBusy() {
       return nAvailableProcesses == currentProcesses;
    }


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


    @Override
    public int getNumberOfFreeServers() {
        return  nAvailableProcesses-currentProcesses;
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
                arrivalTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        selectAvgTime = totalTime;
    }


    @Override
    public void computeAverageTimeW(double averageTimeWQ, double averageTimeWS) {
        averageTimeW = averageTimeWQ + averageTimeWS;
    }

    @Override
    public void computeAverageTimeInQueue(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        double totalTimeInQueue = 0;
        int counter = 0;
        while(iterator.hasNext()){
            Query temp = iterator.next();
            double totalTime= temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromQueue()
                    - temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToQueue();
            if(totalTime > 0) {
                totalTimeInQueue += totalTime;
                counter++;
            }
        }
        averageTimeInQueue = totalTimeInQueue / counter;
    }

    @Override
    public void computeAverageTimeInService(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        double totalTimeInService = 0;
        int counter = 0;
        while(iterator.hasNext()){
            Query temp = iterator.next();
            double totalTime= temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfExitFromModule()
                    - temp.getQueryStatistics().getQueryProcessingStatistics().getTimeOfEntryToServer();
            if(totalTime > 0) {
                totalTimeInService += totalTime;
                counter++;
            }
        }
        averageTimeInService = totalTimeInService / counter;
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