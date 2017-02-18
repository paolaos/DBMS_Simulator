import java.lang.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnectionModule extends Module{
    private List<Query> allQueries;
    public static final double LAMBDA = 0.58333333;
    private int kConnections;
    private int rejectedConnections;
    private int currentConnections;
    private int currentId;

    public ClientConnectionModule(Simulation simulation, Module nextModule, int kConnections){
        this.simulation = simulation;
        this.nextModule = nextModule;
        this.kConnections = kConnections;
        allQueries = new LinkedList<>();
        queue = new LinkedBlockingQueue<>();
        timeQueue = new LinkedBlockingQueue<>();
        currentId = -1;
        rejectedConnections = 0;
        currentConnections = 0;
        hasBeenInQueue = 0;
        idleTime=0;
        totalIdleTime=0;
    }

    public int getRejectedConnections() {
        return rejectedConnections;
    }

    public int getCurrentConnections() {
        return currentConnections;
    }

    public void setRejectedConnections(int rejectedConnections) {
        this.rejectedConnections = rejectedConnections;
    }



    @Override
    public void processArrival(Query query) {
       if(query.isSolved())
           processArrivalLastModule(query);

       else
           processArrivalFirstModule(query);
            if(currentConnections==0)
                totalIdleTime+= simulation.getClock()-idleTime;
    }

    private void processArrivalFirstModule(Query query){
        if(isBusy())
            rejectedConnections++;

        else {
            currentConnections++;
            simulation.addEvent(new Event(simulation.getClock() + getNextExitTime(), query,
                    EventType.EXIT, ModuleType.CLIENT_CONNECTION_MODULE));
            query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().setTimeOfEntryToModule(simulation.getClock());
        }
        generateServiceEvent(null);
    }


    private void processArrivalLastModule(Query query){
        //sumarle al total time del query.
        simulation.addEvent(new Event(getResultantTime(query.getNumberOfBlocks()) + simulation.getClock(),
                query, EventType.EXIT, ModuleType.CLIENT_CONNECTION_MODULE));
        query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().setTimeOfEntryToModule(this.simulation.getClock());

    }

    public void generateFirstArrival(){
        Query query = new Query(currentId++, simulation.getClock(), DistributionGenerator.generateType(),
                ModuleType.CLIENT_CONNECTION_MODULE);

        simulation.addEvent(new Event(simulation.getClock() , query,
                EventType.ARRIVAL, ModuleType.CLIENT_CONNECTION_MODULE));

        //allQueries.add(query);
    }

    @Override
    public void generateServiceEvent(Query query){
        if(query == null){
            query = new Query(currentId++, simulation.getClock(), DistributionGenerator.generateType(),
                    ModuleType.CLIENT_CONNECTION_MODULE);
            allQueries.add(query);
        }
        double nextArrivalTime = DistributionGenerator.getNextArrivalTime(LAMBDA);
        simulation.addEvent(new Event(simulation.getClock() + nextArrivalTime, query,
                EventType.ARRIVAL, ModuleType.CLIENT_CONNECTION_MODULE));
        simulation.addEvent(new Event(simulation.getClock() + nextArrivalTime + simulation.getTimeout(), query,
                EventType.KILL, null));
    }

    @Override
    public void processDeparture(Query query) {
        if(query.isSolved())
            processDepartureOfSystem(query);

        else
            processDepartureToNextModule(query);

    }

    private void processDepartureToNextModule(Query  query){
        query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().setTimeOfExitFromModule(simulation.getClock());
        nextModule.generateServiceEvent(query);
        servedQueries++;

    }

    @Override
    public boolean isBusy() {
        return currentConnections == kConnections;
    }


    @Override
    public void processKill(Query query) {
        query.setTotalTime(simulation.getClock());
    }

    private void processDepartureOfSystem(Query query){
        currentConnections--;
        query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().setTimeOfExitFromModule(simulation.getClock());
        if (currentConnections==0)
            idleTime=simulation.getClock();

        query.setTotalTime(simulation.getClock());
    }


    @Override
    public double getNextExitTime(){
        return DistributionGenerator.getNextRandomValueByUniform(0.01,0.05);
    }

    public double getResultantTime(int numberOfBlocks) {
        double average = numberOfBlocks / 3; //hacerlo en entero y redondearlo para arriba?
        return average / 2;
    }

    public List<Query> getAllQueries(){
        return allQueries;
    }


    @Override
    public int getNumberOfFreeServers() {
        return kConnections - currentConnections;
    }

    @Override
    public int getQueueSize() {
        return 0;
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
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.DDL){
                double arrivalTime = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                double exitTime = query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
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
                    arrivalTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                    exitTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
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
                arrivalTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        return totalTime;
    }


    public double getDdlAvgTimeExitModule(List <Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.DDL){
                arrivalTime= query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;

            }

        }
        return totalTime;
    }


    public double getUpdateAvgTimeExitModule(List <Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.UPDATE){
                arrivalTime= query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }

        }
        return totalTime;
    }


    public double getJoinAvgTimeExitModule(List <Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.JOIN){
                arrivalTime= query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        return totalTime;
    }


    public double getSelectAvgTimeExitModule(List <Query> queryList) {
        double totalTime=0;
        double arrivalTime=0;
        double exitTime=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.SELECT){
                arrivalTime= query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfEntryToModule();
                exitTime= query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
            }
        }
        return totalTime;
    }

    public double getAvgConnectionLife(List<Query> queryList){
        double avgConnectionLife = 0;
        int size = queryList.size();
        for(int i = 0; i < size; i++){
            avgConnectionLife += queryList.get(i).getTotalTime();
        }

        return avgConnectionLife / size;
    }


    @Override
    public void setAverageQueriesL(double avergeQueriesLQ, double avergeQueriesLS) {
        averageQueriesL =avergeQueriesLQ+avergeQueriesLS;
    }

    //TODO preguntar a profe sobre este caso
    @Override
    public void setAverageQueriesInQueue(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        Query query;

    }

    @Override
    public void setAverageQueriesInService(List<Query> queryList) {

    }

    @Override
    public void setAverageTimeW(double avergeTimeWQ, double avergeTimeWS) {

    }

    @Override
    public void setAverageTimeInQueue(List<Query> queryList) {

    }

    @Override
    public void setAverageTimeInService(List<Query> queryList) {

    }
}

