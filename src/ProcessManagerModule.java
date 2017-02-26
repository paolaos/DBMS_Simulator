import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;


public class ProcessManagerModule extends Module{
    private int currentSystemCalls;
    private  int availableSystemCalls;
    public ProcessManagerModule(Simulation simulation, Module nextModule , int availableSystemCalls){
        this.simulation = simulation;
        this.nextModule = nextModule;
        this.queue = new LinkedBlockingQueue<>();
        busy = false;
        totalProcessedQueries=0;
        currentSystemCalls=0;
       this.availableSystemCalls=availableSystemCalls;
       servers=availableSystemCalls;

    }

    @Override // procesamientode arribo
    public void processArrival(Query query) {
        query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToModule(simulation.getClock());
        if(this.isBusy()){
            query.setIsInQueue(true);
            queue.offer(query);
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToQueue(simulation.getClock());
        }else{
            currentSystemCalls++;
            double normalValue = DistributionGenerator.getNextRandomValueByNormal(1.5, Math.sqrt(0.1));
            simulation.addEvent(new Event(simulation.getClock() + normalValue,
                        query, EventType.EXIT, ModuleType.PROCESS_MANAGER_MODULE));
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToServer(simulation.getClock());

            totalIdleTime+= simulation.getClock()-idleTime;
        }

    }

    @Override //procesamiento de salida
    //por Brayan
    public void processDeparture(Query query) {
        totalProcessedQueries++;
        query.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromModule(simulation.getClock());
        if(queue.size() > 0){

            // 0.316227766 sqrt of 0.1
            double normalValue = DistributionGenerator.getNextRandomValueByNormal(1.5, Math.sqrt(0.1));
            Query quer =queue.poll();
            quer.setIsInQueue(false);
            simulation.addEvent(new Event(simulation.getClock() + normalValue,
                  quer, EventType.EXIT, ModuleType.PROCESS_MANAGER_MODULE));

            quer.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromQueue(simulation.getClock());
            quer.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToServer(simulation.getClock());
        }else {
            currentSystemCalls--;
            if(currentSystemCalls==0)
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
            int actualConnections=simulation.getClientConnectionModule().getCurrentConnections()-1;
            simulation.getClientConnectionModule().setCurrentConnections(actualConnections);
            //momento en que sale de la cola
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromQueue(simulation.getClock());

        }else {
            query.setKill(true);
        }
        //se quita del mapeo porque ya va a muere
        Event killEventToRemove= simulation.getKillEventsTable().get(query.getId());
        simulation.getKillEventsTable().remove(killEventToRemove);
    }

    public boolean isBusy() {
        return availableSystemCalls == currentSystemCalls;
    }

    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.PROCESS_MANAGER_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.PROCESS_MANAGER_MODULE));
    }

    @Override
    public int getNumberOfFreeServers() {
        return  availableSystemCalls - currentSystemCalls;
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
    public void computeDdlAvgTime(List <Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.DDL){
                arrivalTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
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
            double entryTimeToQueue=query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToQueue();
            double exitTimeFromQueue=query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromQueue();
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
        double totalTime = 0;
        int counter = 0;
        while(iterator.hasNext()){
            Query query = iterator.next();
            double entryTimeToServer = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToServer();
            double exitTimeFromServer = query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
            double totalTimeInServer = exitTimeFromServer - entryTimeToServer;
            if(totalTimeInServer > 0){
                counter++;
                totalTime+=totalTimeInServer;
            }
        }
        averageTimeInService = totalTime / counter;
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


    //Antes de Llamar este método se debe calcular el lambda real
    @Override
    public void fillStatistics( double lambda) {


        this.computeAverageTimeInQueue(simulation.getClientConnectionModule().getAllQueries());//1/mu (Ws)
        this.computeAverageServiceTimeMu();
        this.computeWq(lambda,averageServiceTimeMu,true);
        this.computeAverageTimeW(averageTimeInQueue , averageTimeInService);

        this.computeLs(lambda,averageServiceTimeMu);
        this.computeLq(lambda,averageServiceTimeMu,true);
        this.computeAverageQueriesL(averageQueriesInQueue,averageQueriesInService);
        this.computeAverageOccupiedTimeRho(lambda);

        this.computeDdlAvgTime(simulation.getClientConnectionModule().getAllQueries());
        this.computeUpdateAvgTime(simulation.getClientConnectionModule().getAllQueries());
        this.computeSelectAvgTime(simulation.getClientConnectionModule().getAllQueries());
        this.computeJoinAvgTime(simulation.getClientConnectionModule().getAllQueries());
    }
}
