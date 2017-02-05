import java.util.concurrent.LinkedBlockingQueue;

public class ExecutionModule extends Module{
    private int mSentences;
    private int currentSentences;

    public ExecutionModule(int mSentences){
        queue = new LinkedBlockingQueue<>();
        timeQueue = new LinkedBlockingQueue<>();
        this.mSentences = mSentences;
        currentSentences = 0;
        hasBeenInQueue = 0;
    }
}
