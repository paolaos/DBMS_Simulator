import java.util.concurrent.LinkedBlockingQueue;

public class ExecutionModule extends Module{
    private final double DDL_RESTRUCTRATION_TIME =  0.5;
    private final double UPDATE_RESTRUCTURATION_TIME = 1;
    private int mSentences;
    private int currentSentences;

    public ExecutionModule(Simulation simulation, int mSentences){
        this.simulation= simulation;
        queue = new LinkedBlockingQueue<>();
        timeQueue = new LinkedBlockingQueue<>();
        this.mSentences = mSentences;
        currentSentences = 0;
        hasBeenInQueue = 0;
    }



    @Override
    public void processArrival(Query query) {
        if (isBusy()) {
            queue.offer(query);
            query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToQueue(simulation.getClock());
        } else {
            currentSentences++;
            double exitTime = simulation.getClock() + getTotalTime(query);
            simulation.addEvent(new Event(exitTime, query, EventType.EXIT, ModuleType.EXECUTION_MODULE));
            query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToServer(simulation.getClock());
            query.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromModule(exitTime);
        }
    }


    @Override
    public void processDeparture(Query query) {
        if(queue.size() > 0){
            double exitTime = simulation.getClock() + getTotalTime(query);
            simulation.addEvent(new Event(exitTime, queue.poll(), EventType.EXIT, ModuleType.EXECUTION_MODULE)); //TODO hora estaba mala, faltaba sumar el getTotalTime(query)
            query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToServer(simulation.getClock());
            query.getQueryStatistics().getExecutionStatistics().setTimeOfExitFromModule(exitTime);
        }else {
            currentSentences--;
        }
        query.setSolved(true);
        nextModule.generateServiceEvent(query);
    }

    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.EXECUTION_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.EXECUTION_MODULE));
        query.getQueryStatistics().getExecutionStatistics().setTimeOfEntryToModule(simulation.getClock());
    }

    @Override
    public boolean isBusy(){
        return currentSentences == mSentences;
    }

    @Override
    public void processKill(Query query) {

    }

    @Override
    public double getNextExitTime() {
        return 0;
    }

    public double getBlockExecutingTime(int numberOfBlocks) {
        return Math.pow(numberOfBlocks, 2) / 1000;
    }


    public double getTotalTime(Query query){
        double totalTime = this.getBlockExecutingTime(query.getNumberOfBlocks());
        totalTime += getRestructurationTime(query.getQueryType());
        return totalTime;
    }

    public void setNextModule(Module nextModule){
        this.nextModule = nextModule;
    }

    private double getRestructurationTime(QueryType query) {
        double time = 0;
        if (query == QueryType.DDL) {
            time = DDL_RESTRUCTRATION_TIME;
        } else if (query == QueryType.UPDATE) {
            time = UPDATE_RESTRUCTURATION_TIME;
        }
        return time;
    }

}
