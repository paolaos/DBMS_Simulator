public class Query implements Comparable<Query>{
    private float timeOfEntry;
    private float totalTime;
    private boolean readOnly;
    private QueryType type;

    public Query(float timeOfEntry, QueryType type){
        this.timeOfEntry = timeOfEntry;
        this.type = type;
        totalTime = 0;

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
