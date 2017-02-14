
public class Query implements Comparable<Query>{
    private double timeOfEntry;
    private double totalTime;
    private boolean isInQueue;
    private QueryType type;
    private ModuleType currentModule;
    private boolean timeOut;
    private QueryStatistics queryStatistics;
    private int id;
    private int numberOfBlocks;
    private boolean solved;

    public Query(int id, double timeOfEntry , QueryType type, ModuleType currentModule){
        this.id = id;
        this.timeOfEntry = timeOfEntry;
        this.type = type;
        totalTime = 0;
        isInQueue = false;
        this.currentModule = currentModule;
        timeOut = false;
        numberOfBlocks=0;
        solved = false;
        queryStatistics = new QueryStatistics();
    }



    public boolean isSolved() {
        return solved;
    }


    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public int getNumberOfBlocks() {
        return numberOfBlocks;
    }

    public void setNumberOfBlocks(int numberOfBlocks) {
        this.numberOfBlocks = numberOfBlocks;
    }

    public QueryType getQueryType(){
        return type;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }
}
