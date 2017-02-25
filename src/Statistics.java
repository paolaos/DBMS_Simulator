import java.util.Iterator;
import java.util.List;


/**
 * Store All the statistic information from some simulation.
 */
public class Statistics {
    private ModuleStatistics clientConnectionStatistics;
    private ModuleStatistics processManagerStatistics;
    private ModuleStatistics queryProcessingStatistics;
    private ModuleStatistics transactionAndDataStatistics;
    private ModuleStatistics executionStatistics;

    private double timeout;
    private double clock;
    private int numberOfTrials;
    private double timePerTrial;
    private double totalTimeSimulation;
    private boolean slowMode;
    private double qDelayTime;
    private int kConnections;
    private int nAvailableProcesses;
    private int pQueries;
    private int mSentences;

    /**
     * Constructor of class statistics
     * @param simulation  Simulation to save statistical information.
     */
    public Statistics(Simulation simulation){
        this.clientConnectionStatistics = simulation.getClientConnectionModule().moduleStatistics;
        this.processManagerStatistics = simulation.getProcessManagerModule().moduleStatistics;
        this.queryProcessingStatistics = simulation.getQueryProcessingModule().moduleStatistics;
        this.transactionAndDataStatistics = simulation.getTransactionAndDataAccessModule().moduleStatistics;
        this.executionStatistics = simulation.getExecutionModule().moduleStatistics;
        this.timeout = simulation.getTimeout();
        this.clock = simulation.getClock();
        this.numberOfTrials = simulation.getNumberOfTrials();
        this.timePerTrial = simulation.getTimePerTrial();
        this.totalTimeSimulation = simulation.getTotalTimeSimulation();
        this.slowMode = simulation.isSlowMode();
        this.qDelayTime = simulation.getqDelayTime();
        this.kConnections = simulation.getkConnections();
        this.nAvailableProcesses = simulation.getnAvailableProcesses();
        this.pQueries = simulation.getpQueries();
        this.mSentences = simulation.getmSentences();
    }

    /**
     * Method that compute all general statistics from all executions.
     * @param statistics Contains all executions.
     */
    public Statistics(List<Statistics> statistics){
        Iterator<Statistics> iterator = statistics.iterator();
        while (iterator.hasNext()){
            //TODO hacer las variables de w y s finales para calcularlas en este constructor.
        }
    }

    public ModuleStatistics getClientConnectionStatistics() {
        return clientConnectionStatistics;
    }

    public ModuleStatistics getProcessManagerStatistics() {
        return processManagerStatistics;
    }

    public ModuleStatistics getQueryProcessingStatistics() {
        return queryProcessingStatistics;
    }

    public ModuleStatistics getTransactionAndDataStatistics() {
        return transactionAndDataStatistics;
    }

    public ModuleStatistics getExecutionStatistics() {
        return executionStatistics;
    }

    public double getTimeout() {
        return timeout;
    }

    public double getClock() {
        return clock;
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

    public double getqDelayTime() {
        return qDelayTime;
    }
}
