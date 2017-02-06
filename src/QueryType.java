/**
 * Created by Ian on 5/2/2017.
 */
public enum QueryType {
    DDL(1), UPDATE(2), JOIN(3), SELECT(4);

    private int priority;

    QueryType(int priority){
        this.priority = priority;
    }

    public int getPriority(){
        return priority;
    }
     //prueba
}
