import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;

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
    private final int LAMBDA = 35;

    public Simulation(boolean slowMode, int qDelayTime, int kConnections, int nAvailableProcesses,
                      int pQueries, int mSentences, float timeout, Statistics statistics, float timePerTrial){

        // Variable initialization
        this.timeout = timeout;
        clock = 0;
        numberOfTrials = 0;
        this.timePerTrial = timePerTrial;
        eventList = new PriorityQueue<>();
        customerManagementModule = new CustomerManagementModule(kConnections);
        processManagerModule = new ProcessManagerModule();
        queryProcessingModule = new QueryProcessingModule(nAvailableProcesses);
        transactionAndDataAccessModule = new TransactionAndDataAccessModule(pQueries);
        executionModule = new ExecutionModule(mSentences);
        totalTimeSimulation = 0;
        this.slowMode = slowMode;
        this.qDelayTime = qDelayTime;
        rho = 0;
        this.statistics = statistics;

        // First arrival
        eventList.add(new Event(0, EventType.ARRIVAL, ModuleType.CUSTOMER_MANAGEMENT_MODULE));

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

    private void manageArrivalEvent(Event event){
        switch (event.getDestinationModule()){
            case CUSTOMER_MANAGEMENT_MODULE:
                QueryType queryType = DistributionGenerator.generateType();
                Query query = new Query(clock,queryType, ModuleType.CUSTOMER_MANAGEMENT_MODULE );
                queryList.add(query);
                if(customerManagementModule.wasInserted(query)){
                    eventList.offer(new Event(customerManagementModule.getNextExitTime() + clock, EventType.EXIT,
                            ModuleType.PROCESS_MANAGER_MODULE));
                }

                eventList.offer(new Event(DistributionGenerator.getNextArrivalTime(LAMBDA) + clock, EventType.ARRIVAL,
                        ModuleType.CUSTOMER_MANAGEMENT_MODULE));

                break;

            case PROCESS_MANAGER_MODULE:
                break;

            case QUERY_PROCESSING_MODULE:
                break;

            case TRANSACTION_AND_DATA_ACCESS_MODULE:
                break;

            case EXECUTION_MODULE:
                break;
        }


    }

    private void manageExitEvent(Event event){
        switch (event.getDestinationModule()){

            case CUSTOMER_MANAGEMENT_MODULE:


                break;

            case PROCESS_MANAGER_MODULE:
                break;

            case QUERY_PROCESSING_MODULE:
                break;

            case TRANSACTION_AND_DATA_ACCESS_MODULE:
                break;

            case EXECUTION_MODULE:
                break;
        }



    }


    private void manageKillEvent(Event event){




    }
    public void startSimulation(){


        while(clock<timePerTrial){
            Event e = eventList.poll();
            clock = e.getExecutionTime();
            switch (e.getEventType()){

                case ARRIVAL:
                    this.manageArrivalEvent(e);
                    break;

                case EXIT:
                    break;

                case KILL:
                    break;
            }




        }
    }

    public void createKillEvents(List<Query> l){
        Iterator<Query> iterator = l.iterator();
        while(iterator.hasNext()){
            Query temp = iterator.next();
            Event event = new Event(clock, temp.getCurrentModule(), temp);
            eventList.add(event);
        }
    }


    public void runSimulation(){}

    public void fillStatistics(Statistics statistics){

    }


}
