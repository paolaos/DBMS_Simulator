import java.util.PriorityQueue;

/**
 * Created by Paola Ortega S on 2/3/2017.
 */
public class Simulation {
    private float timeout;
    private float clock;
    private int numberOfTrials;
    private float timePerTrial;
    private PriorityQueue<Query> eventList;
    private CustomerManagementModule customerManagementModule;
    private ProcessManagerModule processManagerModule;
    private QueryProcessingModule queryProcessingModule;
    private TransactionAndDataAccessModule transactionAndDataAccessModule;
    private ExecutionModule executionModule;
    private float totalTimeSimulation;
    private boolean slowMode;
    private int qDelayTime;
    private float rho;
    private Statistics statistics;

    public Simulation(boolean slowMode, int qDelayTime, Statistics statistics){
        timeout = 0;
        clock = 0;
        numberOfTrials = 0;
        timePerTrial = 0;
        eventList = new PriorityQueue<>();
        customerManagementModule = new CustomerManagementModule(15);
        processManagerModule = new ProcessManagerModule();
        queryProcessingModule = new QueryProcessingModule(3);
        transactionAndDataAccessModule = new TransactionAndDataAccessModule(2);
        executionModule = new ExecutionModule(1);
        totalTimeSimulation = 0;
        this.slowMode = slowMode;
        this.qDelayTime = qDelayTime;
        rho = 0;
        this.statistics = statistics;

    }

    public void runSimulation(){}

    public void fillStatistics(Statistics statistics){

    }

}
