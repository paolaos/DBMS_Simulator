import java.util.concurrent.LinkedBlockingQueue;

public class ExecutionModule extends Module{
    private int mSentences;
    private int currentSentences;

    public ExecutionModule(int mSentences){
        queue = new LinkedBlockingQueue<>();
        this.mSentences = mSentences;
        currentSentences = 0;
    }
}
