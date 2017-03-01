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
    private ModuleStatistics clientConnectionStatisticsWithASolvedQueryStatistics;

    private double timeout;
    private double clock;
    private int numberOfTrials;
    private double timePerTrial;
    private double totalTimeSimulation;
    private boolean slowMode;
    private double qDelayTime;
    private int systemCalls;
    private int kConnections;
    private int nAvailableProcesses;
    private int pQueries;
    private int mSentences;
    private double rejectedConnections;
    private double avgQueryLifetime;

    /**
     * Constructor of class statistics
     *
     * @param simulation Simulation to save statistical information.
     */
    public Statistics(Simulation simulation) {
        this.clientConnectionStatistics = simulation.getClientConnectionModule().moduleStatistics;
        this.processManagerStatistics = simulation.getProcessManagerModule().moduleStatistics;
        this.queryProcessingStatistics = simulation.getQueryProcessingModule().moduleStatistics;
        this.transactionAndDataStatistics = simulation.getTransactionAndDataAccessModule().moduleStatistics;
        this.executionStatistics = simulation.getExecutionModule().moduleStatistics;
        this.clientConnectionStatisticsWithASolvedQueryStatistics = simulation.getClientConnectionModule().getModuleStatisticsOfLastModule();
        //this.clientConnectionStatisticsWithASolvedQueryStatistics = new ModuleStatistics(simulation.getClientConnectionModule(), false);

        this.timeout = simulation.getTimeout();
        this.clock = simulation.getClock();
        this.numberOfTrials = simulation.getNumberOfTrials();
        this.timePerTrial = simulation.getTimePerTrial();
        this.totalTimeSimulation = simulation.getTotalTimeSimulation();
        this.slowMode = simulation.isSlowMode();
        this.qDelayTime = simulation.getqDelayTime();
        this.systemCalls = simulation.getAvailableSystemCalls();
        this.kConnections = simulation.getkConnections();
        this.nAvailableProcesses = simulation.getnAvailableProcesses();
        this.pQueries = simulation.getpQueries();
        this.mSentences = simulation.getmSentences();
        this.rejectedConnections = simulation.getClientConnectionModule().getRejectedConnections();
        this.avgQueryLifetime = simulation.getClientConnectionModule().getAverageQueryLifetime();
    }

    /**
     * Method that computes all general statistics from all executions.
     *
     * @param statistics Contains all executions.
     */
    public Statistics(List<Statistics> statistics) {
        Statistics statistic = statistics.get(0);
        this.timeout = statistic.getClock();
        this.clock = statistic.getClock();
        this.numberOfTrials = statistic.getNumberOfTrials();
        this.timePerTrial = statistic.getTimePerTrial();
        this.totalTimeSimulation = statistic.getTotalTimeSimulation();
        this.slowMode = statistic.isSlowMode();
        this.qDelayTime = statistic.getqDelayTime();
        this.systemCalls = statistic.getSystemCalls();
        this.kConnections = statistic.getkConnections();
        this.nAvailableProcesses = statistic.getnAvailableProcesses();
        this.pQueries = statistic.getpQueries();
        this.mSentences = statistic.getmSentences();
        this.avgQueryLifetime = statistic.getAvgQueryLifetime();

        this.clientConnectionStatistics = new ModuleStatistics();
        this.processManagerStatistics = new ModuleStatistics();
        this.queryProcessingStatistics = new ModuleStatistics();
        this.transactionAndDataStatistics = new ModuleStatistics();
        this.executionStatistics = new ModuleStatistics();
        this.clientConnectionStatisticsWithASolvedQueryStatistics = new ModuleStatistics();


        Iterator<Statistics> iterator = statistics.iterator();
        while (iterator.hasNext()) {
            Statistics currentStatistic = iterator.next();
            this.addStatistics(clientConnectionStatistics, currentStatistic.getClientConnectionStatistics());
            this.addStatistics(processManagerStatistics, currentStatistic.getProcessManagerStatistics());
            this.addStatistics(queryProcessingStatistics, currentStatistic.getQueryProcessingStatistics());
            this.addStatistics(transactionAndDataStatistics, currentStatistic.getTransactionAndDataStatistics());
            this.addStatistics(executionStatistics, currentStatistic.getExecutionStatistics());
            this.addStatistics(clientConnectionStatisticsWithASolvedQueryStatistics, currentStatistic.getClientConnectionStatisticsWithASolvedQueryStatistics());
            this.rejectedConnections += currentStatistic.getRejectedConnections();
        }

        int totalSimulations = statistics.size();
        this.rejectedConnections /= totalSimulations;
        this.setAverageStatistics(clientConnectionStatistics, totalSimulations);
        this.setAverageStatistics(processManagerStatistics, totalSimulations);
        this.setAverageStatistics(queryProcessingStatistics, totalSimulations);
        this.setAverageStatistics(transactionAndDataStatistics, totalSimulations);
        this.setAverageStatistics(executionStatistics, totalSimulations);
        this.setAverageStatistics(clientConnectionStatisticsWithASolvedQueryStatistics, totalSimulations);
    }

    /**
     * Method that adds to a ModuleStatistics object, the values inside another ModuleStatistics
     * object, used to add the values with the objective of getting the average of all
     * simulations
     *
     * @param average ModuleStatistics object intended to contain the averages
     * @param toAdd   ModuleStatistics that contains the values that will be added
     */
    private void addStatistics(ModuleStatistics average, ModuleStatistics toAdd) {
        average.setTotalProcessedQueries(average.getTotalProcessedQueries() + toAdd.getTotalProcessedQueries());
        average.setAverageQueueSize(average.getAverageQueueSize() + toAdd.getAverageQueueSize());
        average.setAverageQueryLifetime(average.getAverageQueryLifetime() + toAdd.getAverageQueryLifetime());
        average.setIdleTime(average.getIdleTime() + toAdd.getIdleTime());
        average.setAverageDdlTime(average.getAverageDdlTime() + toAdd.getAverageDdlTime());
        average.setAverageUpdateTime(average.getAverageUpdateTime() + toAdd.getAverageUpdateTime());
        average.setAverageJoinTime(average.getAverageJoinTime() + toAdd.getAverageJoinTime());
        average.setAverageSelectTime(average.getAverageSelectTime() + toAdd.getAverageSelectTime());
    }

    /**
     * Method that takes a ModuleStatistics object, takes its values and divides them by the total int sent as parameter
     *
     * @param moduleStatistics object intended to hold the module averages
     * @param total            total integer to divide in order to get the averages
     */
    private void setAverageStatistics(ModuleStatistics moduleStatistics, int total) {
        moduleStatistics.setTotalProcessedQueries(moduleStatistics.getTotalProcessedQueries() / total);
        moduleStatistics.setAverageQueueSize(moduleStatistics.getAverageQueueSize() / total);
        moduleStatistics.setAverageQueryLifetime(moduleStatistics.getAverageQueryLifetime() / total);
        moduleStatistics.setIdleTime(moduleStatistics.getIdleTime() / total);
        moduleStatistics.setAverageDdlTime(moduleStatistics.getAverageDdlTime() / total);
        moduleStatistics.setAverageUpdateTime(moduleStatistics.getAverageUpdateTime() / total);
        moduleStatistics.setAverageJoinTime(moduleStatistics.getAverageJoinTime() / total);
        moduleStatistics.setAverageSelectTime(moduleStatistics.getAverageSelectTime() / total);
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

    public double getRejectedConnections() {
        return rejectedConnections;
    }

    public void setRejectedConnections(int rejectedConnections){
        this.rejectedConnections = rejectedConnections;
    }

    public double getAvgQueryLifetime() {
        return avgQueryLifetime;
    }

    public ModuleStatistics getClientConnectionStatisticsWithASolvedQueryStatistics() {
        return clientConnectionStatisticsWithASolvedQueryStatistics;
    }

    public int getSystemCalls() {
        return systemCalls;
    }
}
