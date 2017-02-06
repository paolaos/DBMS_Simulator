import java.util.Comparator;

/**
 * Created by jesus on 06/02/2017.
 */
public class Event implements Comparable<Event>{

    private float executionTime;
    private EventType eventType;
    private ModuleType destinationModule;
    private Query queryToKill;

    public Event(float executionTime, EventType eventType, ModuleType destinationModule){
        this.destinationModule = destinationModule;
        this.eventType= eventType;
        this.executionTime = executionTime;
    }

    public Event(float executionTime, ModuleType destinationModule, Query queryToKill){
        this.executionTime = executionTime;
        this.destinationModule = destinationModule;
        this.queryToKill = queryToKill;
    }


    public void setExecutionTime(float executionTime) {
        this.executionTime = executionTime;
    }

    public float getExecutionTime() {
        return executionTime;
    }


    public EventType getEventType() {
        return eventType;
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
