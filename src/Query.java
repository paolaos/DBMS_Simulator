public class Query implements Comparable<Query> {
    private double timeOfEntry;
    private double totalTime;
    private QueryType type;
    private ModuleType currentModule;
    private QueryStatistics queryStatistics;
    private int id;
    private int numberOfBlocks;
    private boolean solved;
    private boolean isInQueue;
    private boolean kill;

    public Query(int id, double timeOfEntry, QueryType type, ModuleType currentModule) {
        this.id = id;
        this.timeOfEntry = timeOfEntry;
        this.type = type;
        totalTime = 0;
        isInQueue = false;
        this.currentModule = currentModule;
        numberOfBlocks = 0;
        solved = false;
        queryStatistics = new QueryStatistics();
        kill = false;
    }

    /**
     * CompareTo method of the Query class. It compares the priority of the types of the two queries
     *
     * @param query query to be compared with
     * @return 0 if the queries have the same priority, -1 if the current one has less priority, 1 otherwise.
     */
    @Override
    public int compareTo(Query query) {
        int toReturn = 0;
        if (type.getPriority() < query.getPriority())
            toReturn = -1;

        else if (type.getPriority() > query.getPriority())
            toReturn = 1;

        return toReturn;
    }

    public boolean isSolved() {
        return solved;
    }

    public boolean getIsInQueue() {
        return isInQueue;
    }

    public void setIsInQueue(boolean isInQueue) {
        this.isInQueue = isInQueue;
    }

    public boolean isKill() {
        return kill;
    }

    public void setKill(boolean kill) {
        this.kill = kill;
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

    public QueryType getQueryType() {
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


    public double getTimeOfEntry() {
        return timeOfEntry;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public int getPriority() {
        return type.getPriority();
    }

    public int getId() {
        return id;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }
}
