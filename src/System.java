import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by Paola Ortega S on 2/3/2017.
 */
public class System {
    private ArrayList<Statistics> allStatistics;

    private int numberOfSimulations;
    private double qDelayTime;
    private int kConnections;
    private int systemCalls;
    private int nAvailableProcesses;
    private int pQueries;
    private int mSentences;
    private double timeout;
    private double timePerTrial;


    public System(int numberOfSimulations, double qDelayTime, int kConnections, int systemCalls, int nAvailableProcesses,
                  int pQueries, int mSentences,double timeout, double timePerTrial){
        this.numberOfSimulations = numberOfSimulations;
        this.qDelayTime = qDelayTime;
        this.kConnections = kConnections;
        this.systemCalls = systemCalls;
        this.nAvailableProcesses = nAvailableProcesses;
        this.pQueries = pQueries;
        this.mSentences = mSentences;
        this.timeout = timeout;
        this.timePerTrial = timePerTrial;
        allStatistics = new ArrayList<>(numberOfSimulations);
    }

    public void startSimulations(JTextArea txtData){
        for(int i = 0; i < numberOfSimulations; i++){
            Simulation simulation = new Simulation(i + 1, qDelayTime, kConnections, systemCalls,
                    nAvailableProcesses, pQueries, mSentences, timeout, timePerTrial);
            simulation.startSimulation(txtData);
            //llamar metodo que haga las probas.
            simulation.fillStatistics();

            //crear estadisticas
            Statistics statistics = new Statistics(simulation);


        }
    }


}
