import javax.swing.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Paola Ortega S on 2/3/2017.
 */


public class Simulation {
    private int simulationNumber;
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
    private boolean slowMode;
    private double qDelayTime;
    private int kConnections;
    private int availableSystemCalls;
    private int nAvailableProcesses;
    private int pQueries;
    private int mSentences;
    private int killNumber;
    private double killPercentage;
    private Hashtable<Integer, Event> killEventsTable;


    public Simulation(int simulationNumber, double qDelayTime, int kConnections, int availableSystemCalls, int nAvailableProcesses,
                      int pQueries, int mSentences, double timeout, double timePerTrial) {

        // Variable initialization
        this.simulationNumber = simulationNumber;
        this.timeout = timeout;
        clock = 0;
        numberOfTrials = 0;
        this.timePerTrial = timePerTrial;
        eventList = new PriorityQueue<>();
        this.availableSystemCalls = availableSystemCalls;

        executionModule = new ExecutionModule(this, mSentences);
        transactionAndDataAccessModule = new TransactionAndDataAccessModule(this, executionModule, pQueries);
        queryProcessingModule = new QueryProcessingModule(this, transactionAndDataAccessModule, nAvailableProcesses);
        processManagerModule = new ProcessManagerModule(this, queryProcessingModule, availableSystemCalls);
        clientConnectionModule = new ClientConnectionModule(this, processManagerModule, kConnections);
        executionModule.setNextModule(clientConnectionModule);

        this.qDelayTime = qDelayTime;
        this.kConnections = kConnections;
        this.availableSystemCalls = availableSystemCalls;
        this.nAvailableProcesses = nAvailableProcesses;
        this.pQueries = pQueries;
        this.mSentences = mSentences;
        clientConnectionModule.generateFirstArrival();
        killEventsTable = new Hashtable<>();
        killNumber = 0;
    }


    public Hashtable<Integer, Event> getKillEventsTable() {
        return killEventsTable;
    }

    public void addEvent(Event event) {
        eventList.add(event);
    }

    public PriorityQueue<Event> getEventList() {
        return eventList;
    }

    private void manageArrivalEvent(Event event) {
        switch (event.getDestinationModule()) {
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

    private void manageExitEvent(Event event) {
        switch (event.getDestinationModule()) {

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


    private void manageKillEvent(Event event) {


        switch (event.getQuery().getCurrentModule()) {

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

    public String getData(Event event) {
        String simulation = "Simulation number " + simulationNumber + "\n";
        String parameters = "Available connections(k): " + clientConnectionModule.servers +
                "\nAvailable Systems Calls: " + processManagerModule.servers +
                "\nAvailable Processes for query processing(n): " + queryProcessingModule.servers +
                "\nAvailable processes for query transactions(p): " + transactionAndDataAccessModule.servers +
                "\nAvailable processes for query executions(m): " + executionModule.servers;

        String clock = "\nClock time: " + getClock();

        String eventInExecution = "\nExecuting " + event.getEventType() + " in " + event.getDestinationModule() + " module\n\n";

        String clientConnectionData = "Client Connection Module: \n" +
                "Occupied servers: " + clientConnectionModule.getCurrentConnections() + "\n" +
                "Free Servers: " + clientConnectionModule.getNumberOfFreeServers() + "\n" +
                "Size of the Queue: " + clientConnectionModule.getQueueSize() + "\n" +
                "Processed queries: " + clientConnectionModule.getTotalProcessedQueries() + "\n\n";

        String processManagerData = "Process Manager Module: \n" +
                "Occupied servers: " + processManagerModule.getCurrentSystemCalls() + "\n" +
                "Free Servers: " + processManagerModule.getNumberOfFreeServers() + "\n" +
                "Size of the Queue: " + processManagerModule.getQueueSize() + "\n" +
                "Processed queries: " + processManagerModule.getTotalProcessedQueries() + "\n\n";

        String queryProcessingData = "Query Processing Module: \n" +
                "Occupied servers: " + queryProcessingModule.getCurrentProcesses() + "\n" +
                "Free Servers: " + queryProcessingModule.getNumberOfFreeServers() + "\n" +
                "Size of the Queue: " + queryProcessingModule.getQueueSize() + "\n" +
                "Processed queries: " + queryProcessingModule.getTotalProcessedQueries() + "\n\n";

        String transactionAndDataAccessData = "Transaction and Data Access Module: \n" +
                "Occupied servers: " + transactionAndDataAccessModule.getCurrentProcesses() + "\n" +
                "Free Servers: " + transactionAndDataAccessModule.getNumberOfFreeServers() + "\n" +
                "Size of the Queue: " + transactionAndDataAccessModule.getQueueSize() + "\n" +
                "Processed queries: " + transactionAndDataAccessModule.getTotalProcessedQueries() + "\n\n";

        String executionData = "Execution Module: \n" +
                "Occupied servers: " + executionModule.getCurrentProcesses() + "\n" +
                "Free Servers: " + executionModule.getNumberOfFreeServers() + "\n" +
                "Size of the Queue: " + executionModule.getQueueSize() + "\n" +
                "Processed queries: " + executionModule.getTotalProcessedQueries() + "\n\n";

        String lastModuleData = "Exit Module: \n" +
                "Occupied servers: " + clientConnectionModule.getCurrentConnections() + "\n" +
                "Free Servers: " + clientConnectionModule.getNumberOfFreeServers() + "\n" +
                "Size of the Queue: " + clientConnectionModule.getQueueSize() + "\n" +
                "Processed queries: " + clientConnectionModule.getTotalProcessedQueriesFromLastModule() + "\n\n";

        return simulation + parameters + clock + eventInExecution + clientConnectionData +
                processManagerData + queryProcessingData + transactionAndDataAccessData + executionData + lastModuleData;
    }

    public void startSimulation(JTextArea txtData) {
        while (getClock() < timePerTrial) {
            Event event = eventList.poll();
            clock = event.getTime();
            switch (event.getEventType()) {

                case ARRIVAL:
                    this.manageArrivalEvent(event);
                    break;

                case EXIT:
                    manageExitEvent(event);
                    break;

                case KILL:
                    manageKillEvent(event);
                    break;
            }
            txtData.setText(this.getData(event));
            txtData.update(txtData.getGraphics());
            try {
                Thread.sleep((long) qDelayTime * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void fillStatistics() {
        clientConnectionModule.computeAverageQueryLifetime(clientConnectionModule.getAllQueries());
        //compute real lambda

        double lambda = clientConnectionModule.computeRealLambda();
        boolean isTheLastModule = true;

        clientConnectionModule.fillStatistics(lambda, !isTheLastModule);
        if (clientConnectionModule.getAverageOccupiedTimeRho() > 1) {
            //asignar el nuevo lambda
            lambda = processManagerModule.computeRealLambda();
        }
        processManagerModule.fillStatistics(lambda, !isTheLastModule);
        if (processManagerModule.getAverageOccupiedTimeRho() > 1) {
            //asignar el nuevo lambda
            lambda = queryProcessingModule.computeRealLambda();
        }

        queryProcessingModule.fillStatistics(lambda, !isTheLastModule);
        if (queryProcessingModule.getAverageOccupiedTimeRho() > 1) {
            //asignar el nuevo lambda
            lambda = transactionAndDataAccessModule.computeRealLambda();

        }

        transactionAndDataAccessModule.fillStatistics(lambda, !isTheLastModule);
        if (transactionAndDataAccessModule.getAverageOccupiedTimeRho() > 1) {
            //asignar el nuevo lambda
            lambda = executionModule.computeRealLambda();
        }

        executionModule.fillStatistics(lambda, !isTheLastModule);
        if (executionModule.getAverageOccupiedTimeRho() > 1) {
            //asignar el nuevo lambda
            lambda = clientConnectionModule.computeRealLambdaToLastModule();
        }

        clientConnectionModule.fillStatistics(lambda, isTheLastModule);
        this.getKillNumber(clientConnectionModule.getAllQueries());
        this.computeKillPercentage();
    }

    public double getClock() {
        return clock;
    }

    public double getTimeout() {
        return timeout;
    }

    public ClientConnectionModule getClientConnectionModule() {
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

    public int getKillNumber(List<Query> allQueries) {
        Iterator<Query> iterator = allQueries.iterator();
        while (iterator.hasNext()) {
            Query query = iterator.next();
            if (query.isKill())
                killNumber++;
        }
        return killNumber;
    }

    public int getNumberOfTrials() {
        return numberOfTrials;
    }

    public double getTimePerTrial() {
        return timePerTrial;
    }

    public double getTotalTimeSimulation() {
        return timePerTrial;
    }

    public boolean isSlowMode() {
        return slowMode;
    }

    public double getqDelayTime() {
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

    public int getAvailableSystemCalls() {
        return availableSystemCalls;
    }

    public double getKillPercentage() {
        return killPercentage;
    }

    public void computeKillPercentage() {
        killPercentage = (double) this.getKillNumber(this.clientConnectionModule.getAllQueries()) / (double) this.clientConnectionModule.getAllQueries().size();
    }

}
