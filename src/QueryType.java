/**
 * Created by Ian on 5/2/2017.
 */
public enum QueryType {
    DDL(1, false), UPDATE(2, false), JOIN(3, true), SELECT(4, true);

    private int priority;
    private boolean readOnly;

    QueryType(int priority, boolean readOnly){
        this.priority = priority;
        this.readOnly = readOnly;
    }

    public int getPriority(){
        return priority;
    }

}
