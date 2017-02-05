public class Query implements Comparable<Query>{
    private float timeOfEntry;
    private float totalTime;
    private String queryType;
    private boolean readOnly;

    public Query(float timeOfEntry, String queryType){
        this.timeOfEntry = timeOfEntry;
        this.queryType = queryType;
        totalTime = 0;

    }
    @Override
    public int compareTo(Query query) {

        return 0;
    }
}
