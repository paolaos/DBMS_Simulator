import java.lang.*;
import java.lang.System;
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
    private int kConnections;
    private int availableSystemCalls;
    private int nAvailableProcesses;
    private int pQueries;
    private int mSentences;
    private int killNumber;
    private Hashtable<Integer, Event> killEventsTable;


    public Simulation(boolean slowMode, int qDelayTime, int kConnections, int availableSystemCalls, int nAvailableProcesses,
                      int pQueries, int mSentences, double timeout, double timePerTrial){

        // Variable initialization
        this.timeout = timeout;
        clock = 0;
        numberOfTrials = 0;
        this.timePerTrial = timePerTrial;
        eventList = new PriorityQueue<>();

        executionModule = new ExecutionModule(this, mSentences);
        transactionAndDataAccessModule = new TransactionAndDataAccessModule(this, executionModule, pQueries);
        queryProcessingModule = new QueryProcessingModule(this, transactionAndDataAccessModule, nAvailableProcesses);
        processManagerModule = new ProcessManagerModule(this, queryProcessingModule,availableSystemCalls);
        clientConnectionModule = new ClientConnectionModule(this, processManagerModule, kConnections);
        executionModule.setNextModule(clientConnectionModule);
        totalTimeSimulation = 0;
        this.slowMode = slowMode;
        this.qDelayTime = qDelayTime;
        this.kConnections = kConnections;
        this.availableSystemCalls=availableSystemCalls;
        this.nAvailableProcesses = nAvailableProcesses;
        this.pQueries = pQueries;
        this.mSentences = mSentences;
        clientConnectionModule.generateFirstArrival();
        killEventsTable= new Hashtable<Integer,Event>();
        killNumber=0;
    }

    public Hashtable<Integer, Event> getKillEventsTable() {
        return killEventsTable;
    }

    public void addEvent(Event event){
        eventList.add(event);
    }

    public PriorityQueue<Event> getEventList() {
        return eventList;
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


                transactionAndDataAccessModule.processArrival(event.getQuery());

                break;

            case EXECUTION_MODULE:
                executionModule.processArrival(event.getQuery());
                break;
        }


    }

    private void manageExitEvent(Event event){
        switch (event.getDestinationModule()){

            case CLIENT_CONNECTION_MODULE:
                clientConnectionModule.processDeparture(event.getQuery());
                break;

            case PROCESS_MANAGER_MODULE:
                processManagerModule.processDeparture(event.getQuery());
                break;

            case QUERY_PROCESSING_MODULE:
                queryProcessingModule.processDeparture(event.getQuery());
                break;

            case TRANSACTION_AND_DATA_ACCESS_MODULE:

                    transactionAndDataAccessModule.processDeparture(event.getQuery());
                break;

            case EXECUTION_MODULE:
                executionModule.processDeparture(event.getQuery());
                break;
        }
    }


    private void manageKillEvent(Event event){
        switch (event.getQuery().getCurrentModule()){

            case CLIENT_CONNECTION_MODULE:
                clientConnectionModule.processKill(event.getQuery());
                break;

            case PROCESS_MANAGER_MODULE:
                processManagerModule.processKill(event.getQuery());
                break;

            case QUERY_PROCESSING_MODULE:
                queryProcessingModule.processKill(event.getQuery());
                break;

            case TRANSACTION_AND_DATA_ACCESS_MODULE:

                transactionAndDataAccessModule.processKill(event.getQuery());
                break;

            case EXECUTION_MODULE:
                executionModule.processKill(event.getQuery());
                break;
        }

    }
    public String getData(Event event){

        String parameters = "Available connections(k): "+ clientConnectionModule.servers +
                "\nAvailable Systems Calls: " + processManagerModule.servers +
                "\nAvailable Processes for query processing(n): "+ queryProcessingModule.servers+
                "\nAvailable processes for query transactions(p): "+ transactionAndDataAccessModule.servers+
                "\nAvailable processes for query executions(m): "+executionModule.servers;

        String clock="\n"+ getClock()+"";

        String eventInExecution= "\n Exececuting "+ event.getEventType()+ " in "+ event.getDestinationModule() +" module";

        String clientConnectionData = "Client Connection Module: \n" +
                "Occupied servers: " + clientConnectionModule.getCurrentConnections() + "\n" +
                "Free Servers: " + clientConnectionModule.getNumberOfFreeServers() + "\n" +
                "Size of the Queue: " + clientConnectionModule.getQueueSize() + "\n" +
                "Processed queries: " + clientConnectionModule.getServedQueries() + "\n";

        String processManagerData = "Process Manager Module: \n" +
                "Occupied servers: " + processManagerModule + "\n" +
                "Free Servers: " + processManagerModule.getNumberOfFreeServers() + "\n" +
                "Size of the Queue: " + processManagerModule.getQueueSize() + "\n" +
                "Processed queries: " + processManagerModule.getServedQueries() + "\n";

        String queryProcessingData = "Query Processing Module: \n" +
                "Occupied servers: " + queryProcessingModule.getCurrentProcesses() + "\n" +
                "Free Servers: " + queryProcessingModule.getNumberOfFreeServers() + "\n" +
                "Size of the Queue: " + queryProcessingModule.getQueueSize() + "\n" +
                "Processed queries: " + queryProcessingModule.getServedQueries() + "\n";

        String transactionAndDataAccessData = "Transaction and Data Access Module: \n" +
                "Occupied servers: " + transactionAndDataAccessModule.getCurrentProcesses() + "\n" +
                "Free Servers: " + clientConnectionModule.getNumberOfFreeServers() + "\n" +
                "Size of the Queue: " + transactionAndDataAccessModule.getQueueSize() + "\n" +
                "Processed queries: " + transactionAndDataAccessModule.getServedQueries() + "\n";

        String executionData = "Execution Module: \n" +
                "Occupied servers: " + executionModule.getCurrentProcesses() + "\n" +
                "Free Servers: " + clientConnectionModule.getNumberOfFreeServers() + "\n" +
                "Size of the Queue: " + executionModule.getQueueSize() + "\n" +
                "Processed queries: " + executionModule.getServedQueries() + "\n";

        String rejectedQueries = "Rejected Queries:" + this.clientConnectionModule.getRejectedConnections() + "\n\n";
        return parameters + clock + eventInExecution + clientConnectionData + executionData +
                processManagerData + queryProcessingData + transactionAndDataAccessData + rejectedQueries;
    }

    public void startSimulation(){
        while(getClock() < timePerTrial){
            Event e = eventList.poll();
            clock = e.getTime();
            if(e.getQuery().getId() == -1)
                System.out.println(e.getEventType() + " " + e.getDestinationModule());

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




    public void runSimulation(){}

    public void fillStatistics(Statistics statistics){

    }

    public double getClock() {
        return clock;
    }

    public double getTimeout() {
        return timeout;
    }

    public ClientConnectionModule getClientConnectionModule(){
        return clientConnectionModule;
    }

    public ProcessManagerModule getProcessManagerModule() {
        return processManagerModule;
    }

    public QueryProcessingModule getQueryProcessingModule() {
        return queryProcessingModule;
    }

    public TransactionAndDataAccessModule getTransactionAndDataAccessModule() {
        return transactionAndDataAccessModule;
    }

    public ExecutionModule getExecutionModule() {
        return executionModule;
    }

    public static void main(String[]args){
        java.lang.System.out.println("Simulación DBMS");
        Simulation s = new Simulation(false, 0, 15,1, 3, 2, 1, 15, 15000);
        s.startSimulation();

        System.out.println("Conexiones actuales "+ s.clientConnectionModule.getCurrentConnections());
        System.out.println("Tamaño Cola módulo 2 "+s.processManagerModule.getQueueSize() );
        System.out.println("Tamaño Cola módulo 3 "+s.queryProcessingModule.getQueueSize());
        System.out.println("Tamaño Cola módulo 4 "+s.transactionAndDataAccessModule.getQueueSize());
        System.out.println("Tamaño Cola módulo 5 "+s.executionModule.getQueueSize());
        System.out.println("Consultas Éxitosas " +s.clientConnectionModule.getAllQueries().size());
        System.out.println("Número de Conexiones Rechazadas "+s.clientConnectionModule.getRejectedConnections());

    }

    public int getNumberOfTrials() {
        return numberOfTrials;
    }

    public double getTimePerTrial() {
        return timePerTrial;
    }

    public double getTotalTimeSimulation() {
        return totalTimeSimulation;
    }

    public boolean isSlowMode() {
        return slowMode;
    }

    public int getqDelayTime() {
        return qDelayTime;
    }

    public int getkConnections() {
        return kConnections;
    }

    public int getnAvailableProcesses() {
        return nAvailableProcesses;
    }

    public int getpQueries() {
        return pQueries;
    }

    public int getmSentences() {
        return mSentences;
    }
}
