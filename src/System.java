import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paola Ortega S on 2/3/2017.
 */
public class System {
    private List<Statistics> allStatistics;
    private int numberOfSimulations;
    private double qDelayTime;
    private int kConnections;
    private int systemCalls;
    private int nAvailableProcesses;
    private int pQueries;
    private int mSentences;
    private double timeout;
    private double timePerTrial;

    public List<Statistics> getAllStatistics() {
        return allStatistics;
    }

    /**
     * Constructor for the System class. It takes the parameters chosen by the user and passed from the GUI and creates
     * a list of Statistics.
     *
     * @param numberOfSimulations number of simulations to be run
     * @param qDelayTime          delay time between events
     * @param kConnections        number of connections to be handled
     * @param systemCalls         maximum number of system calls
     * @param nAvailableProcesses available processes for query processing
     * @param pQueries            available processes for query transactions
     * @param mSentences          available processes for query executions
     * @param timeout             timeout of the queries
     * @param timePerTrial        amount of time the simulation is allowed to run
     */
    public System(int numberOfSimulations, double qDelayTime, int kConnections, int systemCalls, int nAvailableProcesses,
                  int pQueries, int mSentences, double timeout, double timePerTrial) {
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

    /**
     * Method to start the simulations. It takes a JTextArea and sends the reference to the current simulation.
     * The method uses the parameters set by the user to make a cycle of simulations. In the cycle, a Simulation
     * is created with the user input, set to run and then a Statistics object is made and filled with the Simulation's
     * data and added to the Statistics list.
     *
     * @param txtData text field that will display the data of the simulations
     */
    public void startSimulations(JTextArea txtData) {
        for (int i = 0; i < numberOfSimulations; i++) {
            Simulation simulation = new Simulation(i + 1, qDelayTime, kConnections, systemCalls,
                    nAvailableProcesses, pQueries, mSentences, timeout, timePerTrial);
            simulation.startSimulation(txtData);
            simulation.fillStatistics();
            Statistics statistics = new Statistics(simulation);
            allStatistics.add(statistics);
        }
    }


}