public class Query implements Comparable<Query>{
    private double timeOfEntry;
    private double totalTime;
    private boolean isInQueue;
    private QueryType type;
    private ModuleType currentModule;
    private boolean timeOut;
    private QueryStatistics queryStatistics;
    private int id;

    public Query(int id, double timeOfEntry , QueryType type, ModuleType currentModule){
        this.id = id;
        this.timeOfEntry = timeOfEntry;
        this.type = type;
        totalTime = 0;
        isInQueue = false;
        this.currentModule = currentModule;
        timeOut = false;

    }


    public QueryStatistics getQueryStatistics() {
        return queryStatistics;
    }


    public ModuleType getCurrentModule() {
        return currentModule;
    }

    public void setCurrentModule(ModuleType currentModule) {
        this.currentModule = currentModule;
    }

    public boolean isInQueue() {
        return isInQueue;
    }

    public void setInQueue(boolean inQueue) {
        isInQueue = inQueue;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public int getPriority(){
        return type.getPriority();
    }

    @Override
    public int compareTo(Query query) {
        int toReturn = 0;
        if(type.getPriority() < query.getPriority())
            toReturn = -1;

        else if(type.getPriority() > query.getPriority())
            toReturn = 1;

        return toReturn;
    }

    public int getId() {
        return id;
    }
}
