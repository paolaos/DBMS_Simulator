import java.lang.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnectionModule extends Module{
    public static final double LAMBDA = 0.58333333;
    private List<Query> allQueries;
    private ModuleStatistics lastModuleStatistics;
    private int kConnections;
    private int rejectedConnections;
    private int currentConnections;
    private int currentId;
    private double averageQueryLifetime;

    public ClientConnectionModule(Simulation simulation, Module nextModule, int kConnections){
        this.simulation = simulation;
        this.nextModule = nextModule;
        this.kConnections = kConnections;
        allQueries = new LinkedList<>();
        queue = new LinkedBlockingQueue<>();
        currentId = 1;
        rejectedConnections = 0;
        currentConnections = 0;
        idleTime=0;
        totalIdleTime=0;
        averageQueryLifetime = 0;
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

    @Override
    public void processArrival(Query query) {
        if(query.isSolved()) {
            processArrivalLastModule(query);

        }else {
            processArrivalFirstModule(query);
            if (currentConnections == 0)
                totalIdleTime += simulation.getClock() - idleTime;
        }
    }

    private void processArrivalFirstModule(Query query){
        if(isBusy())
            rejectedConnections++;
        else {
            currentConnections++;
            simulation.addEvent(new Event(simulation.getClock() + getNextExitTime(), query,
                    EventType.EXIT, ModuleType.CLIENT_CONNECTION_MODULE));
            query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().setTimeOfEntryToModule(simulation.getClock());

            allQueries.add(query);
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

    }

    @Override
    public void generateServiceEvent(Query query){
        if(query == null){
            query = new Query(currentId++, simulation.getClock(), DistributionGenerator.generateType(),
                    ModuleType.CLIENT_CONNECTION_MODULE);
        }
        double nextArrivalTime = DistributionGenerator.getNextArrivalTime(LAMBDA);
        simulation.addEvent(new Event(simulation.getClock() + nextArrivalTime, query,
                EventType.ARRIVAL, ModuleType.CLIENT_CONNECTION_MODULE));
        Event killEvent =new Event(simulation.getClock() + nextArrivalTime + simulation.getTimeout(), query,
                EventType.KILL, null);
        simulation.addEvent(killEvent);
        //agregar kill con el id del query
        simulation.getKillEventsTable().put(query.getId(),killEvent);
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
        if (!query.isKill()) {
            nextModule.generateServiceEvent(query);
        }else {
            currentConnections--;
        }

        servedQueries++;

    }

    @Override
    public boolean isBusy() {
        return currentConnections == kConnections;
    }


    @Override
    public void processKill(Query query) {
        //para cuando vaya al siguiente modulo no enviarlo.
        query.setKill(true);
    }

    private void processDepartureOfSystem(Query query){
        currentConnections--;
        query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().setTimeOfExitFromModule(simulation.getClock());
        if (currentConnections==0)
            idleTime=simulation.getClock();
        //TODO restar tiempo de entrada al sistema
        query.setTotalTime(simulation.getClock() - query.getTimeOfEntry());
        //se elimina el Kill
        Event eventToRemove = simulation.getKillEventsTable().get(query.getId());
        simulation.getEventList().remove(eventToRemove);

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
    public void computeDdlAvgTime(List <Query> queryList) {
        double totalTime=0;
        int ddlCounter=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.DDL){
                double arrivalTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                double exitTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
                ddlCounter++;

            }

        }
        this.ddlAvgTime = totalTime/ddlCounter;
    }

    @Override
    public void computeUpdateAvgTime(List <Query> queryList) {
        double totalTime=0;
        int updateCounter=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.UPDATE){
                double arrivalTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                double exitTime=query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
                updateCounter++;
            }
        }
        this.updateAvgTime = totalTime/updateCounter;
    }

    @Override
    public void computeJoinAvgTime(List <Query> queryList) {
        double totalTime=0;
        int joinCounter=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.JOIN){

                double arrivalTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                double exitTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
                joinCounter++;
            }
        }
        this.joinAvgTime = totalTime/joinCounter;
    }

    @Override
    public void computeSelectAvgTime(List <Query> queryList) {
        double totalTime=0;
        int selectCounter=0;
        Iterator<Query> iterator = queryList.iterator();

        while (iterator.hasNext()){
            Query query = iterator.next();
            if (query.getQueryType()==QueryType.SELECT){
                double arrivalTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToModule();
                double exitTime= query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
                totalTime+=exitTime-arrivalTime;
                selectCounter++;
            }
        }
        this.selectAvgTime = totalTime/selectCounter;
    }


    //solo primer modulo
    public void computeAverageQueryLifetime(List<Query> queryList){
        double avgConnectionLife = 0;
        int size = queryList.size();
        for(int i = 0; i < size; i++){
            avgConnectionLife += queryList.get(i).getTotalTime();
        }

        averageQueryLifetime = avgConnectionLife / size;
    }

    public double getAverageQueryLifetime(){
        return averageQueryLifetime;
    }


    @Override
    public void computeAverageQueriesL(double averageQueriesLQ, double averageQueriesLS) {
        averageQueriesL = averageQueriesLQ + averageQueriesLS;
    }

    //TODO usando valor placeholder
    @Override
    public void computeAverageQueriesInQueue(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        Query query=iterator.next();

        while (iterator.hasNext()){

        }
    }

    @Override
    public void computeAverageQueriesInService(List<Query> queryList) {

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
            double entryTimeToQueue=query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToQueue();
            double exitTimeFromQueue=query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromQueue();
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
            double entryTimeToServer=query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfEntryToServer();
            double exitTimeFromServer=query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().getTimeOfExitFromModule();
            double totalTimeInServer = exitTimeFromServer - entryTimeToServer;
            if(totalTimeInServer > 0){
                counter++;
                totalTime+=totalTimeInServer;
            }
        }
        averageTimeInService=totalTime/counter;
    }
}
