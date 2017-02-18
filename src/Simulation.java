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
    private Statistics statistics;
    private Hashtable<Integer, Event> killEventsTable;


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
        clientConnectionModule.generateFirstArrival();
        killEventsTable= new Hashtable<Integer,Event>();
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

    public static void main(String[]args){
        java.lang.System.out.println("Simulación DBMS");
        Simulation s = new Simulation(false, 0, 15, 3, 2, 1, 15, null, 15000);
        s.startSimulation();

        System.out.println("Conexiones actuales "+ s.clientConnectionModule.getCurrentConnections());
        System.out.println("Tamaño Cola módulo 2 "+s.processManagerModule.getQueueSize() );
        System.out.println("Tamaño Cola módulo 3 "+s.queryProcessingModule.getQueueSize());
        System.out.println("Tamaño Cola módulo 4 "+s.transactionAndDataAccessModule.getQueueSize());
        System.out.println("Tamaño Cola módulo 5 "+s.executionModule.getQueueSize());
        System.out.println("Consultas Éxitosas " +s.clientConnectionModule.getAllQueries().size());
        System.out.println("Número de Conexiones Rechazadas "+s.clientConnectionModule.getRejectedConnections());

    }

}
