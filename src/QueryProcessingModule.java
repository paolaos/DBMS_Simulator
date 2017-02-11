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

    @Override
    public void processArrival(Query query) {
        if (isBusy()) {
            queue.offer(query);
            query.getQueryStatistics().getQueryProcessingStatistics().setTimeOfEntryToQueue(simulation.getClock());
        } else {
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
        }
        nextModule.generateServiceEvent(query);
    }

    @Override
    public void processKill(Query query) {
      
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
}