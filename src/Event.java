import java.util.Comparator;

/**
 * Created by jesus on 06/02/2017.
 */
public class Event implements Comparable<Event>{

    private float executionTime;
    private ModuleType destinationModule;

    public Event(float executionTime, ModuleType destinationModule){
        this.destinationModule = destinationModule;
        this.executionTime = executionTime;
    }

    public void setExecutionTime(float executionTime) {
        this.executionTime = executionTime;
    }

    public float getExecutionTime() {
        return executionTime;
    }

    public ModuleType getDestinationModule(){
        return destinationModule;
    }

    @Override
    public int compareTo(Event o) {
        int toReturn = 0;
        if(executionTime < o.getExecutionTime())
            toReturn = -1;

        else if(executionTime > o.getExecutionTime())
            toReturn = 1;

        return toReturn;
    }
}
