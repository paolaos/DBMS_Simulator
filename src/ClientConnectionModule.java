import java.lang.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnectionModule extends Module{
    private List<Query> finishedQueries;
    private final int LAMBDA = 35;
    private int kConnections;
    private int rejectedConnections;
    private int currentConnections;
    private int currentId;

    public ClientConnectionModule(Simulation simulation, Module nextModule, int kConnections){
        this.simulation = simulation;
        this.nextModule = nextModule;
        this.kConnections = kConnections;
        finishedQueries = new LinkedList<>();
        queue = new LinkedBlockingQueue<>();
        timeQueue = new LinkedBlockingQueue<>();
        currentId = 0;
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



    ///TODO implementar ultimo paso de la simulacion
    @Override
    public void processArrival(Query query) {
       if(query.isSolved())
           processArrivalLastModule(query);

       else
           processArrivalFirsModule(query);
    }

    private void processArrivalFirsModule(Query query){
        if(isBusy())
            rejectedConnections++;
        else {
            currentConnections++;
            simulation.addEvent(new Event(simulation.getClock() + getNextExitTime(), query,
                    EventType.EXIT, ModuleType.CLIENT_CONNECTION_MODULE));
        }
        generateServiceEvent(null);
    }


    private void processArrivalLastModule(Query query){
        //sumarle al total time del query.
        simulation.addEvent(new Event(getResultantTime(query.getNumberOfBlocks()) + simulation.getClock(),
                query, EventType.EXIT, ModuleType.CLIENT_CONNECTION_MODULE));

    }

    public void generateFirstArrival(){
        Query quer = new Query(currentId, simulation.getClock(), DistributionGenerator.generateType(),
                ModuleType.CLIENT_CONNECTION_MODULE);
        simulation.addEvent(new Event(simulation.getClock() , quer,
                EventType.ARRIVAL, ModuleType.CLIENT_CONNECTION_MODULE));

    }

    @Override
    public void generateServiceEvent(Query query){
        Query quer = new Query(currentId, simulation.getClock(), DistributionGenerator.generateType(),
                ModuleType.CLIENT_CONNECTION_MODULE);
        double nextArrivalTime = DistributionGenerator.getNextArrivalTime(LAMBDA);
        simulation.addEvent(new Event(simulation.getClock() + nextArrivalTime, quer,
                EventType.ARRIVAL, ModuleType.CLIENT_CONNECTION_MODULE));
        simulation.addEvent(new Event(simulation.getClock() + nextArrivalTime + simulation.getTimeout(), quer,
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
        java.lang.System.out.println(" sale consulta en tiempo " + simulation.getClock());
        finishedQueries.add(query);
    }


    @Override
    public double getNextExitTime(){
        return DistributionGenerator.getNextRandomValueByNormal(0.01,0.05);
    }

    public double getResultantTime(int numberOfBlocks) {
        double average = numberOfBlocks / 3; //hacerlo en entero y redondearlo para arriba?
        return average / 2;
    }

    public List<Query> getFinishedQueries(){
        return finishedQueries;
    }

}

