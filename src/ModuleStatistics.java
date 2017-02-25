/**
 * Created by Paola Ortega S on 2/8/2017.
 */


/**
 * Class that store all statistics from some module.
 */
public class ModuleStatistics {
    private int servedQueries;
    private int averageQueueSize;
    private double averageQueryLifetime;
    private double idleTime;
    private double averageDdlTime;
    private double averageUpdateTime;
    private double averageJoinTime;
    private double averageSelectTime;
    private double averageOccupiedTimeRho;

    /**
     *  Store the statistics information of the module.
     * @param module Module from which statistics will be saved
     */

    public ModuleStatistics(Module module){
        this.servedQueries = module.getServedQueries();
        this.averageQueueSize = module.getQueueSize();
        this.averageQueryLifetime = module.simulation.getClientConnectionModule().getAverageQueryLifetime();
        this.idleTime = module.getIdleTime();
        this.averageDdlTime = module.getDdlAvgTime();
        this.averageUpdateTime = module.getUpdateAvgTime();
        this.averageJoinTime = module.getJoinAvgTime();
        this.averageSelectTime = module.getSelectAvgTime();
        this.averageOccupiedTimeRho = module.getAverageOccupiedTimeRho();
    }

    public int getServedQueries() {
        return servedQueries;
    }

    public int getAverageQueueSize() {
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

    public void setServedQueries(int servedQueries) {
        this.servedQueries = servedQueries;
    }

    public void setAverageQueueSize(int averageQueueSize) {
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
}
