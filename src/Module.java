import java.util.Queue;

public class Module {
    protected Queue<Query> queue;
    protected Queue<Float> timeQueue;
    protected float accumulatedTimeInQueue;
    protected float accumulatedTimeInService;
    protected int totalProcessedQueries;
    protected float avgTimeInQueue;
    protected int hasBeenInQueue;

    public void insertQuery(Query query){
        queue.offer(query);
    }

    public Query getNextQuery(){
        return queue.poll();
    }

    public int getQueueSize(){
        return queue.size();
    }

    public float getAccumulatedTimeInQueue() {
        return accumulatedTimeInQueue;
    }

    public float getAccumulatedTimeInService() {
        return accumulatedTimeInService;
    }

    public int getTotalProcessedQueries() {
        return totalProcessedQueries;
    }

    public float getAvgTimeInQueue() {
        return avgTimeInQueue;
    }

    public void fillStatistics(Statistics statistics){

    }

    public float getNextExitTime(){
        return 0;
    }
}
