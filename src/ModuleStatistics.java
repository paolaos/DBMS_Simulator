import java.util.LinkedList;
import java.util.List;

/**
 * Created by Paola Ortega S on 2/8/2017.
 */
public class ModuleStatistics {
    private int servedQueries;
    private int averageQueueSize;
    //Esta en e primer modulo
    //private double averageQueryLifetime;
    private double idleTime;
    private double averageDdlTime;
    private double averageUpdateTime;
    private double averageJoinTime;
    private double averageSelectTime;
    private List<Query> queries;

    public ModuleStatistics(List<Query> queries){
        servedQueries = 0;
        averageQueueSize = 0;
        this.queries = queries;
    }

    public int getServedQueries() {
        return servedQueries;
    }

    public void setServedQueries(int servedQueries) {
        this.servedQueries = servedQueries;
    }

    public int getAverageQueueSize() {
        return 0;
    }

    public void setAverageQueueSize(int averageQueueSize) {
        this.averageQueueSize = averageQueueSize;
    }

    public double getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(double idleTime) {
        this.idleTime = idleTime;
    }

    public double getAverageDdlTime() {
        return averageDdlTime;
    }

    public void setAverageDdlTime(double averageDdlTime) {
        this.averageDdlTime = averageDdlTime;
    }

    public double getAverageUpdateTime() {
        return averageUpdateTime;
    }

    public void setAverageUpdateTime(double averageUpdateTime) {
        this.averageUpdateTime = averageUpdateTime;
    }

    public double getAverageJoinTime() {
        return averageJoinTime;
    }

    public void setAverageJoinTime(double averageJoinTime) {
        this.averageJoinTime = averageJoinTime;
    }

    public double getAverageSelectTime() {
        return averageSelectTime;
    }

    public void setAverageSelectTime(double averageSelectTime) {
        this.averageSelectTime = averageSelectTime;
    }
}