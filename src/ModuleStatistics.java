/**
 * Created by Paola Ortega S on 2/8/2017.
 */


/**
 * Class that store all statistics from some module.
 */
public class ModuleStatistics {
    private int totalProcessedQueries;
    private double averageQueueSize;
    private double trueLambda;
    private double avgServiceTimeMu;
    private double averageQueryLifetime;
    private double idleTime;
    private double averageDdlTime;
    private double averageUpdateTime;
    private double averageJoinTime;
    private double averageSelectTime;
    private double averageOccupiedTimeRho;
    private int rejectedConnections;
    private double averageQueriesL;

    private double averageQueriesLS;
    private double averageTimeW;
    private double averageTimeWQ;

    /**
     * Store the statistics information of the module.
     *
     * @param module Module from which statistics will be saved
     */

    public ModuleStatistics(Module module) {
        this.totalProcessedQueries = module.getTotalProcessedQueries();
        this.averageQueueSize = module.getAverageQueriesInQueue();
        //TODO pasar a estadistica general
        //this.averageQueryLifetime = module.simulation.getClientConnectionModule().getAverageQueryLifetime();
        this.idleTime = module.getIdleTime();
        this.averageDdlTime = module.getDdlAvgTime();
        this.averageUpdateTime = module.getUpdateAvgTime();
        this.averageJoinTime = module.getJoinAvgTime();
        this.averageSelectTime = module.getSelectAvgTime();
        this.averageOccupiedTimeRho = module.getAverageOccupiedTimeRho();
        this.trueLambda = module.computeRealLambda();
        this.avgServiceTimeMu = module.getAverageServiceTimeMu();
        this.averageQueriesL = module.getAverageQueriesL();

        this.averageQueriesLS = module.getAverageQueriesInService();
        this.averageTimeW = module.getAverageTimeW();
        this.averageTimeWQ = module.getAvgTimeInQueue();
    }

    public ModuleStatistics(ClientConnectionModule module, boolean isLastModule) {
        if(!isLastModule) {
            this.totalProcessedQueries = module.getTotalProcessedQueriesFromLastModule();
            this.averageQueueSize =0;
            this.idleTime = module.getIdleTime();
            this.averageDdlTime = module.getDdlAvgTimeInLastModule();
            this.averageUpdateTime = module.getUpdateAvgTimeInLastModule();
            this.averageJoinTime = module.getJoinAvgTimeInLastModule();
            this.averageSelectTime = module.getSelectAvgTimeInLastModule();
            this.averageOccupiedTimeRho = module.getAverageOccupiedTimeRho();
        }
    }

    public ModuleStatistics() {
        this.totalProcessedQueries = 0;
        this.averageQueueSize = 0;
        this.averageQueryLifetime = 0;
        this.idleTime = 0;
        this.averageDdlTime = 0;
        this.averageUpdateTime = 0;
        this.averageJoinTime = 0;
        this.averageSelectTime = 0;
        this.averageOccupiedTimeRho = 0;
    }

    public int getTotalProcessedQueries() {
        return totalProcessedQueries;
    }

    public void setTotalProcessedQueries(int totalProcessedQueries) {
        this.totalProcessedQueries = totalProcessedQueries;
    }

    public double getAverageQueueSize() {
        return averageQueueSize;
    }

    public double getIdleTime() {
        return idleTime;
    }

    public double getAverageDdlTime() {
        return averageDdlTime;
    }

    public double getAverageUpdateTime() {
        return averageUpdateTime;
    }

    public double getAverageJoinTime() {
        return averageJoinTime;
    }

    public double getAverageSelectTime() {
        return averageSelectTime;
    }

    public double getAverageQueryLifetime() {
        return averageQueryLifetime;
    }

    public double getAverageOccupiedTimeRho() {
        return averageOccupiedTimeRho;
    }

    public void setAverageQueueSize(double averageQueueSize) {
        this.averageQueueSize = averageQueueSize;
    }

    public void setAverageQueryLifetime(double averageQueryLifetime) {

        this.averageQueryLifetime = averageQueryLifetime;
    }

    public void setIdleTime(double idleTime) {
        this.idleTime = idleTime;
    }

    public void setAverageDdlTime(double averageDdlTime) {
        this.averageDdlTime = averageDdlTime;
    }

    public void setAverageUpdateTime(double averageUpdateTime) {
        this.averageUpdateTime = averageUpdateTime;
    }

    public void setAverageJoinTime(double averageJoinTime) {
        this.averageJoinTime = averageJoinTime;
    }

    public void setAverageSelectTime(double averageSelectTime) {
        this.averageSelectTime = averageSelectTime;
    }

    public void setAverageOccupiedTimeRho(double averageOccupiedTimeRho) {
        this.averageOccupiedTimeRho = averageOccupiedTimeRho;
    }

    public int getRejectedConnections() {
        return rejectedConnections;
    }

    public void setRejectedConnections(int rejectedConnections) {
        this.rejectedConnections = rejectedConnections;
    }

    public double getTrueLambda() {
        return trueLambda;
    }

    public double getAvgServiceTimeMu() {
        return avgServiceTimeMu;
    }

    public double getAverageQueriesL(){
        return averageQueriesL;
    }

    public double getAverageQueriesLQ() {
        return averageQueueSize;
    }

    public double getAverageQueriesLS() {
        return averageQueriesLS;
    }

    public double getAverageTimeW() {
        return averageTimeW;
    }

    public double getAverageTimeWQ() {
        return averageTimeWQ;
    }
}
