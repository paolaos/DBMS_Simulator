import java.util.concurrent.LinkedBlockingQueue;

public class ExecutionModule extends Module{
    private int mSentences;
    private int currentSentences;

    public ExecutionModule(Simulation simulation, int mSentences){
        this.simulation= simulation;
        this.nextModule = nextModule;
        queue = new LinkedBlockingQueue<>();
        timeQueue = new LinkedBlockingQueue<>();
        this.mSentences = mSentences;
        currentSentences = 0;
        hasBeenInQueue = 0;
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

    public double getBlockExecutingTime(int numberOfBlocks) {
        return Math.pow(numberOfBlocks, 2) / 1000;
    }

    public double resultantTime(int numberOfBlocks){
        return numberOfBlocks/3;
    }

    public void setNextModule(Module nextModule){
        this.nextModule = nextModule;
    }

}
