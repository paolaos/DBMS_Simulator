/**
 * Created by jesus on 06/02/2017.
 */
public class Event implements Comparable<Event> {

    private double time;
    private EventType eventType;
    private ModuleType destinationModule; // para kill no aplica
    private Query query;

    public Event(double time, Query query, EventType eventType, ModuleType destinationModule) {
        this.destinationModule = destinationModule;
        this.query = query;
        this.eventType = eventType;
        this.time = time;
    }

    public Event(double time, ModuleType destinationModule, Query query) {
        this.time = time;
        this.destinationModule = destinationModule;
        this.query = query;
    }


    public void setTime(double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }


    public EventType getEventType() {
        return eventType;
    }

    public ModuleType getDestinationModule() {
        return destinationModule;
    }


    @Override
    public int compareTo(Event o) {
        int toReturn = 0;
        if (time < o.getTime())
            toReturn = -1;

        else if (time >= o.getTime())
            toReturn = 1;

        return toReturn;
    }

    public Query getQuery() {
        return query;
    }
}
