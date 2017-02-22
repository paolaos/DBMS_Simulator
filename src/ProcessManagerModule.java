import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ProcessManagerModule extends Module{
    public ProcessManagerModule(Simulation simulation, Module nextModule){
        this.simulation = simulation;
        this.nextModule = nextModule;
        queue = new LinkedBlockingQueue<>();
        busy = false;
    }

    @Override // procesamientode arribo
    public void processArrival(Query query) {
        query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToModule(simulation.getClock());
        if(this.isBusy()){
            query.setIsInQueue(true);
            queue.offer(query);
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToQueue(simulation.getClock());
        }else{
            busy = true;
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
        query.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromModule(simulation.getClock());
        if(queue.size() > 0){
            busy = true;
            // 0.316227766 sqrt of 0.1
            double normalValue = DistributionGenerator.getNextRandomValueByNormal(1.5, Math.sqrt(0.1));
            Query quer =queue.poll();
            quer.setIsInQueue(false);
            simulation.addEvent(new Event(simulation.getClock() + normalValue,
                  quer, EventType.EXIT, ModuleType.PROCESS_MANAGER_MODULE));

            quer.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromQueue(simulation.getClock());
            quer.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToServer(simulation.getClock());
        }else {
            busy = false;
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
        return busy;
    }

    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.PROCESS_MANAGER_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.PROCESS_MANAGER_MODULE));
        servedQueries++;
    }

    @Override
    public double getNextExitTime() {
        return 0;
    }

    @Override
    public int getNumberOfFreeServers() {
        return isBusy()? 0 : 1;
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
    public double getDdlAvgTime(List <Query> queryList) {
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
                arrivalTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getProcessManagerStatistics().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        return totalTime;
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
}
