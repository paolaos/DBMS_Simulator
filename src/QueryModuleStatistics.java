public class QueryModuleStatistics {
    private double timeOfEntryToModule=0;
    private double timeOfEntryToQueue=0;
    private double timeOfEntryToServer=0;
    private double timeOfExitFromQueue=0;
    private double timeOfExitFromModule=0;

    public double getTimeOfEntryToModule() {
        return timeOfEntryToModule;
    }

    public void setTimeOfEntryToModule(double timeOfEntryToModule) {
        this.timeOfEntryToModule = timeOfEntryToModule;
    }

    public double getTimeOfExitFromQueue() {
        return timeOfExitFromQueue;
    }

    public void setTimeOfExitFromQueue(double timeOfExitFromQueue) {
        this.timeOfExitFromQueue = timeOfExitFromQueue;
    }

    public double getTimeOfEntryToQueue() {
        return timeOfEntryToQueue;
    }

    public void setTimeOfEntryToQueue(double timeOfEntryToQueue) {
        this.timeOfEntryToQueue = timeOfEntryToQueue;
    }

    public double getTimeOfEntryToServer() {
        return timeOfEntryToServer;
    }

    public void setTimeOfEntryToServer(double timeOfEntryToServer) {
        this.timeOfEntryToServer = timeOfEntryToServer;
    }

    public double getTimeOfExitFromModule() {
        return timeOfExitFromModule;
    }

    public void setTimeOfExitFromModule(double timeOfExitFromModule) {
        this.timeOfExitFromModule = timeOfExitFromModule;
    }

}