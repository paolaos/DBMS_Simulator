import java.util.ArrayList;

/**
 * Created by Paola Ortega S on 2/3/2017.
 */
public class System {
    private ArrayList<Statistics> allStatistics;
    private int numberOfSimulations;
    private boolean slowMode;
    private int qDelayTime;
    private int kConnections;
    private int nAvailableProcesses;
    private int pQueries;
    private int mSentences;

    public System(int numberOfSimulations, boolean slowMode, int qDelayTime, int kConnections, int nAvailableProcesses,
                  int pQueries, int mSentences){
        if(!slowMode)
            throw new ArrayIndexOutOfBoundsException("This constructor only supports slow mode");
        this.numberOfSimulations = numberOfSimulations;
        this.slowMode = slowMode;
        this.qDelayTime = qDelayTime;
        this.kConnections = kConnections;
        this.nAvailableProcesses = nAvailableProcesses;
        this.pQueries = pQueries;
        this.mSentences = mSentences;
        allStatistics = new ArrayList<>(numberOfSimulations);
    }

    /*public System(int numberOfSimulations, int kConnections, int nAvailableProcesses,
                  int pQueries, int mSentences){
        this.numberOfSimulations = numberOfSimulations;
        this.slowMode = slowMode;
        if(!slowMode)
            this.qDelayTime = 0;
        else
            this.qDelayTime = 5;
        allStatistics = new ArrayList<>(numberOfSimulations);

    }*/

    public void startSimulations(){
        for(int i = 0; i < numberOfSimulations; i++){
            Statistics currentStatistics = new Statistics();
            //Simulation currentSimulation = new Simulation(slowMode, qDelayTime, currentStatistics);
            //currentSimulation.runSimulation();
            //currentSimulation.fillStatistics(currentStatistics);
            allStatistics.add(currentStatistics);
        }
    }


}
