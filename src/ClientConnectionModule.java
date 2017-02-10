import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnectionModule extends Module{
    private final int LAMBDA = 35;
    private int kConnections;
    private int rejectedConnections;
    private int currentConnections;
    private int currentId;

    public ClientConnectionModule(Simulation simulation, Module nextModule, int kConnections){
        this.simulation = simulation;
        this.nextModule = nextModule;
        this.kConnections = kConnections;
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

    @Override
    public boolean isBusy() {
        return currentConnections == kConnections;
    }

    ///TODO implementar ultimo paso de la simulacion
    @Override
    public void processArrival(Query query) {
        if(isBusy())
            rejectedConnections++;
        else {
            currentConnections++;
            simulation.addEvent(new Event(simulation.getClock() + getNextExitTime(), query,
                    EventType.EXIT, ModuleType.CLIENT_CONNECTION_MODULE));
        }
        generateServiceEvent(null);
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
        currentConnections--;
        nextModule.generateServiceEvent(query);
    }

    @Override
    public double getNextExitTime(){
        return DistributionGenerator.getNextRandomValueByNormal(0.01,0.05);
    }

    public double getResultantTime(int numberOfBlocks) {
        double average = numberOfBlocks / 3;
        return average + numberOfBlocks / 2;
    }

}