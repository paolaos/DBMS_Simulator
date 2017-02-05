import java.lang.*;
import java.util.concurrent.LinkedBlockingQueue;
public class QueryProcessingModule extends Module{
    private int nAvailableProcesses;
    private int currentProcesses;

    public QueryProcessingModule(int nAvailableProcesses){
        queue = new LinkedBlockingQueue<>();
        timeQueue = new LinkedBlockingQueue<>();
        this.nAvailableProcesses = nAvailableProcesses;
        currentProcesses = 0;
        hasBeenInQueue = 0;
    }

    public int getnAvailableProcesses() {
        return nAvailableProcesses;
    }

    public static void main(String... args){
        java.lang.System.out.println(QueryType.JOIN.getPriority());
    }
}
