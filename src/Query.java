public class Query implements Comparable<Query>{
    private float timeOfEntry;
    private float totalTime;
    private boolean isInQueue;
    private boolean readOnly;
    private QueryType type;
    private ModuleType currentModule;

    public Query(float timeOfEntry , QueryType type, ModuleType currentModule){
        this.timeOfEntry = timeOfEntry;
        this.type = type;
        totalTime = 0;
        isInQueue = false;
        this.currentModule = currentModule;
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

    public float getTotalTime() {
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
}
