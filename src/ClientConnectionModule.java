import java.lang.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnectionModule extends Module{
    private List<Query> allQueries;
    private final double LAMBDA = 0.58333333;
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
    }

    private void processArrivalFirstModule(Query query){
        if(isBusy())
            rejectedConnections++;
        else {
            currentConnections++;
            simulation.addEvent(new Event(simulation.getClock() + getNextExitTime(), query,
                    EventType.EXIT, ModuleType.CLIENT_CONNECTION_MODULE));
            query.getQueryStatistics().getClientConnectionStatisticsWithoutResolvedQuery().setTimeOfEntryToModule(this.simulation.getClock());
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
    }

    @Override
    public boolean isBusy() {
        return currentConnections == kConnections;
    }


    @Override
    public void processKill(Query query) {

    }

    private void processDepartureOfSystem(Query query){
        currentConnections--;
        query.getQueryStatistics().getClientConnectionStatisticsWithResolvedQuery().setTimeOfExitFromModule(simulation.getClock());
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

}

