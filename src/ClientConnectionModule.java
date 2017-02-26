import java.lang.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnectionModule extends Module{
    /**
     * Average number of arrivals to the system per time second.
     */
    public static final double LAMBDA = 0.58333333;

    /**
     * A list of all the queries that have entered successfully to the system.
     */
    private List<Query> allQueries;

    /**
     * User defined parameter of the amount of concurrent connections the system can handle.
     */
    private int kConnections;

    /**
     * Counter that measures the amount of connections rejected by the system.
     */
    private int rejectedConnections;

    /**
     * The amount of connections managed by the system in a specific moment.
     */
    private int currentConnections;

    /**
     * Each query is tagged with an ID (name defined by the number of query that manages to connect into the system).
     * Essentially, this variable traces the current query number.
     */
    private int currentId;

    /**
     * Mainly for statistical purposes. Represents the mean of how much time passes until the query gets solved.
     */
    private double averageQueryLifetime;

    /**
     * Counter that verifies how many queries managed to exit the last module.
     */
    private int totalProcessedQueriesFromLastModule;


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
        totalProcessedQueriesFromLastModule=0;
        totalProcessedQueries=0;
        servers=kConnections;
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

    /**
     * Decides what to do with the query in case it's unresolved.
     * @param query specific unresolved query
     */
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

    /**
     * Decides what to do with the query in case it's resolved.
     * @param query specific resolved query.
     */
    private void processArrivalLastModule(Query query){
        //sumarle al total time del query.
        simulation.addEvent(new Event(getResultantTime(query.getNumberOfBlocks()) + simulation.getClock(),
                query, EventType.EXIT, ModuleType.CLIENT_CONNECTION_MODULE));
        query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().setTimeOfEntryToModule(this.simulation.getClock());

    }

    /**
     * Creates the first event and places it in this simulation in order to start in execution time.
     */
    public void generateFirstArrival(){
        Query query = new Query(currentId++, simulation.getClock(), DistributionGenerator.generateType(),
                ModuleType.CLIENT_CONNECTION_MODULE);
        simulation.addEvent(new Event(simulation.getClock() , query,
                EventType.ARRIVAL, ModuleType.CLIENT_CONNECTION_MODULE));

    }

    /**
     * Handles the query's departure to the next module
     * @param query
     */
    private void processDepartureToNextModule(Query query){
        query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().setTimeOfExitFromModule(simulation.getClock());
        totalProcessedQueries++;
        if (!query.isKill()) {
            nextModule.generateServiceEvent(query);

        }else {
            currentConnections--;
        }

    }

    private void processDepartureOfSystem(Query query){
        currentConnections--;
        query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().setTimeOfExitFromModule(simulation.getClock());
        totalProcessedQueriesFromLastModule++;
        if (currentConnections==0)
            idleTime=simulation.getClock();
        //TODO restar tiempo de entrada al sistema
        query.setTotalTime(simulation.getClock() - query.getTimeOfEntry());
        //se elimina el Kill
        Event eventToRemove = simulation.getKillEventsTable().get(query.getId());
        simulation.getEventList().remove(eventToRemove);

    }

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
    public boolean isBusy() {
        return currentConnections == kConnections;
    }

    @Override
    public void processKill(Query query) {
        //para cuando vaya al siguiente modulo no enviarlo.
        query.setKill(true);
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
    public double getIdleTime() {
        return totalIdleTime;
    }












    /////////////////////////////////////////////////////

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

    @Override
    public void computeAverageQueriesL(double averageQueriesLQ, double averageQueriesLS) {
        averageQueriesL = averageQueriesLQ + averageQueriesLS;
    }

    //TODO usando valor placeholder
    @Override
    public void computeAverageQueriesInQueue(List<Query> queryList) {
        Iterator<Query> iterator = queryList.iterator();
        Query query = iterator.next();
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

    //no se usa
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

    public int getRejectedConnections() {
        return rejectedConnections;
    }

    public int getCurrentConnections() {
        return currentConnections;
    }

    public void setCurrentConnections(int currentConnections) {
        this.currentConnections = currentConnections;
    }

    //Antes de Llamar este método se debe calcular el lambda real
    @Override
    public void fillStatistics( double lambda) {

        //gerar promedios
        this.computeAverageQueryLifetime(allQueries);


        this.computeAverageTimeInQueue(allQueries);//1/mu (Ws)
        this.computeAverageServiceTimeMu();
        this.computeWq(lambda,averageServiceTimeMu,false);
        this.computeAverageTimeW(averageTimeInQueue , averageTimeInService);

        this.computeLs(lambda,averageServiceTimeMu);
        this.computeLq(lambda,averageServiceTimeMu,false);
        this.computeAverageQueriesL(averageQueriesInQueue,averageQueriesInService);
        this.computeAverageOccupiedTimeRho(lambda);

        this.computeDdlAvgTime(allQueries);
        this.computeUpdateAvgTime(allQueries);
        this.computeSelectAvgTime(allQueries);
        this.computeJoinAvgTime(allQueries);
    }
}

