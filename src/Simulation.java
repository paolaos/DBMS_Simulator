import com.sun.org.apache.bcel.internal.generic.RETURN;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Paola Ortega S on 2/3/2017.
 */
public class Simulation {
    private float timeout;
    private float clock;
    private int numberOfTrials;
    private float timePerTrial;
    private PriorityQueue<Event> eventList;
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
    private List<Query> queryList;

    public Simulation(boolean slowMode, int qDelayTime, float timeout,Statistics statistics){
        this.timeout = timeout;
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

    public List<Query> getTimeoutQueries(){
        List<Query> l= new LinkedList<>();
        Iterator<Query>  iterator = l.iterator();

        while(iterator.hasNext()){
            Query temp = iterator.next();
            if(temp.getTotalTime()>= timeout){
                l.add(temp);
            }
        }
        return  l;
    }


    public void runSimulation(){}

    public void fillStatistics(Statistics statistics){

    }

}
