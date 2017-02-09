import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
public class QueryProcessingModule extends Module{
    private int nAvailableProcesses;
    private int currentProcesses;

    public QueryProcessingModule(Simulation simulation, Module nextModule,int nAvailableProcesses){
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

    }

    @Override
    public void generateServiceEvent(Query query) {

    }

    @Override
    public void processDeparture(Query query) {

    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public double getNextExitTime() {
        return 0;
    }

    public static double timeInQueryProcessingModule(QueryType query) {
        Random rnd = new Random();
        double totalTime = 0;
        double lexicalValidationTime;
        double syntacticalValidationTime;
        double semanticValidationTime;
        double permitVerificationTime;
        double queryOptimizationTime;
        double aleatoryNumber = rnd.nextFloat();

        if (aleatoryNumber < 0.7) {
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
