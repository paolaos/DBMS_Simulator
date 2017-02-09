import java.util.*;

/**
 * Created by Paola Ortega S on 2/3/2017.
 */
public class Simulation {
    private double timeout;
    private double clock;
    private int numberOfTrials;
    private double timePerTrial;
    private PriorityQueue<Event> eventList;
    private ClientConnectionModule clientConnectionModule;
    private ProcessManagerModule processManagerModule;
    private QueryProcessingModule queryProcessingModule;
    private TransactionAndDataAccessModule transactionAndDataAccessModule;
    private ExecutionModule executionModule;
    private double totalTimeSimulation;
    private boolean slowMode;
    private int qDelayTime;
    private Statistics statistics;
    private List<Query> queryList;
    private Hashtable<Integer, QueryStatistics> statisticsTable;


    public Simulation(boolean slowMode, int qDelayTime, int kConnections, int nAvailableProcesses,
                      int pQueries, int mSentences, double timeout, Statistics statistics, double timePerTrial){

        // Variable initialization
        this.timeout = timeout;
        clock = 0;
        numberOfTrials = 0;
        this.timePerTrial = timePerTrial;
        eventList = new PriorityQueue<>();

        executionModule = new ExecutionModule(this, mSentences);
        transactionAndDataAccessModule = new TransactionAndDataAccessModule(this, executionModule, pQueries);
        queryProcessingModule = new QueryProcessingModule(this, transactionAndDataAccessModule, nAvailableProcesses);
        processManagerModule = new ProcessManagerModule(this, queryProcessingModule);
        clientConnectionModule = new ClientConnectionModule(this, processManagerModule, kConnections);
        executionModule.setNextModule(clientConnectionModule);

        totalTimeSimulation = 0;
        this.slowMode = slowMode;
        this.qDelayTime = qDelayTime;
        this.statistics = statistics;

        // First arrival
        //eventList.add(new Event(0, EventType.ARRIVAL, ModuleType.CLIENT_CONNECTION_MODULE));

    }

    public void addEvent(Event event){
        eventList.add(event);
    }

    private List<Query> getTimeoutQueries(){
        List<Query> l= new LinkedList<>();
        Iterator<Query>  iterator = l.iterator();

        while(iterator.hasNext()){
            Query temp = iterator.next();
            if(temp.getTotalTime()>= getTimeout()){
                l.add(temp);
            }
        }
        return  l;
    }

    private void manageArrivalEvent(Event event){
        switch (event.getDestinationModule()){
            case CLIENT_CONNECTION_MODULE:
                clientConnectionModule.processArrival(event.getQuery());
                break;

            case PROCESS_MANAGER_MODULE:
                processManagerModule.processArrival(event.getQuery());
                break;

            case QUERY_PROCESSING_MODULE:
                queryProcessingModule.processArrival(event.getQuery());
                break;

            case TRANSACTION_AND_DATA_ACCESS_MODULE:
                queryProcessingModule.processArrival(event.getQuery());
                break;

            case EXECUTION_MODULE:
                executionModule.processArrival(event.getQuery());
                break;
        }


    }

    private void manageExitEvent(Event event){
        switch (event.getDestinationModule()){

            case CLIENT_CONNECTION_MODULE:

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
        switch (event.getQuery().getCurrentModule()){

            case CLIENT_CONNECTION_MODULE:
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



    public void startSimulation(){
        while(getClock() <timePerTrial){
            Event e = eventList.poll();
            clock = e.getTime();
            switch (e.getEventType()){

                case ARRIVAL:
                    this.manageArrivalEvent(e);
                    break;

                case EXIT:
                    manageExitEvent(e);
                    break;

                case KILL:
                    manageKillEvent(e);
                    break;
            }




        }
    }

    public void createKillEvents(List<Query> l){
        Iterator<Query> iterator = l.iterator();
        while(iterator.hasNext()){
            Query temp = iterator.next();
            Event event = new Event(getClock(), temp.getCurrentModule(), temp);
            eventList.add(event);
        }
    }


    public void runSimulation(){}

    public void fillStatistics(Statistics statistics){

    }

    public double getClock() {
        return clock;
    }

    public double getTimeout() {
        return timeout;
    }
}
