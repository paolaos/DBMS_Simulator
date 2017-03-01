import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Paola Ortega S on 2/8/2017.
 */
public class GUI extends JFrame {
    private final String TITLE = "Simulation";
    private final String NUMBER_OF_SIMULATIONS = "Number of simulations";
    private final String MAX_TIME_PER_SIMULATION = "Maximum time to run the simulation";
    private final String SLOW_MODE = "Slow Mode";
    private final String K_CONNECTIONS = "Number of connections to be handled (k)";
    private final String SYSTEM_CALLS = "Number of threads for system calls";
    private final String N_AVAILABLE_PROCESSES = "Number of available processes for query processing (n)";
    private final String P_AVAILABLE_PROCESSES = "Number of available processes for query transactions (p)";
    private final String M_AVAILABLE_PROCESSES = "Number of available processes for query executions (m)";
    private final String T_TIMEOUT = "Connection timeout (t)";
    private final String DELAY = "Delay (in seconds)";
    private final String START = "Start";
    private final String O_FORTUNA = "O Fortuna!";

    private JTextField txtNumberOfSimulations;
    private JTextField txtMaxTimePerSimulation;
    private JTextField txtDelay;
    private JTextField txtKConnections;
    private JTextField txtSystemCalls;
    private JTextField txtNAvailableProcesses;
    private JTextField txtPAvailableProcesses;
    private JTextField txtMAvailableProcesses;
    private JTextField txtTimeout;

    private JCheckBox chkSlowMode;
    private JCheckBox chkFortuna;

    private JTextArea txtDataDisplay;

    private System system;
    private int numberOfSimulations;

    public GUI() {
        super.setTitle(TITLE);
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JLabel lblNumberOfSimulations = new JLabel(NUMBER_OF_SIMULATIONS);
        lblNumberOfSimulations.setFont(new Font("Normal", Font.BOLD, 18));
        txtNumberOfSimulations = new JTextField();
        txtNumberOfSimulations.setFont(new Font("Normal", Font.BOLD, 18));
        txtNumberOfSimulations.setColumns(20);
        JPanel panelNumberOfSimulations = new JPanel();
        panelNumberOfSimulations.setLayout(new BorderLayout());
        panelNumberOfSimulations.add(lblNumberOfSimulations, BorderLayout.WEST);
        panelNumberOfSimulations.add(txtNumberOfSimulations, BorderLayout.EAST);

        JLabel lblMaxTimePerSimulation = new JLabel(MAX_TIME_PER_SIMULATION);
        lblMaxTimePerSimulation.setFont(new Font("Normal", Font.BOLD, 18));
        txtMaxTimePerSimulation = new JTextField();
        txtMaxTimePerSimulation.setColumns(20);
        txtMaxTimePerSimulation.setFont(new Font("Normal", Font.BOLD, 18));
        JPanel panelMaxTimePerSimulation = new JPanel();
        panelMaxTimePerSimulation.setLayout(new BorderLayout());
        panelMaxTimePerSimulation.add(lblMaxTimePerSimulation, BorderLayout.WEST);
        panelMaxTimePerSimulation.add(txtMaxTimePerSimulation, BorderLayout.EAST);

        JLabel lblSlowMode = new JLabel(SLOW_MODE);
        lblSlowMode.setFont(new Font("Normal", Font.BOLD, 18));
        chkSlowMode = new JCheckBox();
        chkSlowMode.setSelected(false);
        chkSlowMode.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (chkSlowMode.isSelected()) {
                    txtDelay.setEnabled(true);
                } else {
                    txtDelay.setEnabled(false);
                }
            }
        });
        JPanel panelSlowMode = new JPanel();
        panelSlowMode.setLayout(new BorderLayout());
        panelSlowMode.add(lblSlowMode, BorderLayout.WEST);
        panelSlowMode.add(chkSlowMode, BorderLayout.EAST);

        JLabel lblDelay = new JLabel(DELAY);
        lblDelay.setFont(new Font("Normal", Font.BOLD, 18));
        txtDelay = new JTextField();
        txtDelay.setFont(new Font("Normal", Font.BOLD, 18));
        txtDelay.setColumns(20);
        txtDelay.setEnabled(false);
        JPanel panelDelay = new JPanel();
        panelDelay.setLayout(new BorderLayout());
        panelDelay.add(lblDelay, BorderLayout.WEST);
        panelDelay.add(txtDelay, BorderLayout.EAST);

        JLabel lblKConnections = new JLabel(K_CONNECTIONS);
        lblKConnections.setFont(new Font("Normal", Font.BOLD, 18));
        txtKConnections = new JTextField();
        txtKConnections.setColumns(20);
        txtKConnections.setFont(new Font("Normal", Font.BOLD, 18));
        JPanel panelKConnections = new JPanel();
        panelKConnections.setLayout(new BorderLayout());
        panelKConnections.add(lblKConnections, BorderLayout.WEST);
        panelKConnections.add(txtKConnections, BorderLayout.EAST);

        JLabel lblSystemCalls = new JLabel(SYSTEM_CALLS);
        lblSystemCalls.setFont(new Font("Normal", Font.BOLD, 18));
        txtSystemCalls = new JTextField();
        txtSystemCalls.setColumns(20);
        txtSystemCalls.setFont(new Font("Normal", Font.BOLD, 18));
        JPanel panelSystemCalls = new JPanel();
        panelSystemCalls.setLayout(new BorderLayout());
        panelSystemCalls.add(lblSystemCalls, BorderLayout.WEST);
        panelSystemCalls.add(txtSystemCalls, BorderLayout.EAST);

        JLabel lblNAvailableProcesses = new JLabel(N_AVAILABLE_PROCESSES);
        lblNAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        txtNAvailableProcesses = new JTextField();
        txtNAvailableProcesses.setColumns(20);
        txtNAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        JPanel panelNAvailableProcesses = new JPanel();
        panelNAvailableProcesses.setLayout(new BorderLayout());
        panelNAvailableProcesses.add(lblNAvailableProcesses, BorderLayout.WEST);
        panelNAvailableProcesses.add(txtNAvailableProcesses, BorderLayout.EAST);

        JLabel lblPAvailableProcesses = new JLabel(P_AVAILABLE_PROCESSES);
        lblPAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        txtPAvailableProcesses = new JTextField();
        txtPAvailableProcesses.setColumns(20);
        txtPAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        JPanel panelPAvailableProcesses = new JPanel();
        panelPAvailableProcesses.setLayout(new BorderLayout());
        panelPAvailableProcesses.add(lblPAvailableProcesses, BorderLayout.WEST);
        panelPAvailableProcesses.add(txtPAvailableProcesses, BorderLayout.EAST);

        JLabel lblMAvailableProcesses = new JLabel(M_AVAILABLE_PROCESSES);
        lblMAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        txtMAvailableProcesses = new JTextField();
        txtMAvailableProcesses.setColumns(20);
        txtMAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        JPanel panelMAvailableProcesses = new JPanel();
        panelMAvailableProcesses.setLayout(new BorderLayout());
        panelMAvailableProcesses.add(lblMAvailableProcesses, BorderLayout.WEST);
        panelMAvailableProcesses.add(txtMAvailableProcesses, BorderLayout.EAST);

        JLabel lblTimeout = new JLabel(T_TIMEOUT);
        lblTimeout.setFont(new Font("Normal", Font.BOLD, 18));
        txtTimeout = new JTextField();
        txtTimeout.setColumns(20);
        txtTimeout.setFont(new Font("Normal", Font.BOLD, 18));
        JPanel panelTimeout = new JPanel();
        panelTimeout.setLayout(new BorderLayout());
        panelTimeout.add(lblTimeout, BorderLayout.WEST);
        panelTimeout.add(txtTimeout, BorderLayout.EAST);

        chkFortuna = new JCheckBox(O_FORTUNA);
        chkFortuna.setFont(new Font("Normal", Font.BOLD, 18));
        JPanel panelFortuna = new JPanel();
        panelFortuna.add(chkFortuna);

        JButton btnStart = new JButton(START);
        btnStart.setSize(100, 100);
        btnStart.setFont(new Font("Normal", Font.BOLD, 18));
        JPanel panelStart = new JPanel();
        panelStart.add(btnStart);

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (areParametersValid()) {
                    //Empieza
                    if (chkFortuna.isSelected())
                        ReproduceAudio.successSong();

                    numberOfSimulations = Integer.parseInt(txtNumberOfSimulations.getText());
                    double maxTimePerSimulation = Double.parseDouble(txtMaxTimePerSimulation.getText());
                    double delay = 0;
                    if (chkSlowMode.isSelected())
                        delay = Double.parseDouble(txtDelay.getText());

                    final double toUseDelay = delay;
                    int kConnections = Integer.parseInt(txtKConnections.getText());
                    int availableSystemCalls = Integer.parseInt(txtSystemCalls.getText());
                    int nAvailableProcesses = Integer.parseInt(txtNAvailableProcesses.getText());
                    int pAvailableProcesses = Integer.parseInt(txtPAvailableProcesses.getText());
                    int mAvailableProcesses = Integer.parseInt(txtMAvailableProcesses.getText());
                    double timeout = Double.parseDouble(txtTimeout.getText());
                    Runnable toRun = new Runnable() {
                        @Override
                        public void run() {
                            system = new System(numberOfSimulations, toUseDelay, kConnections, availableSystemCalls,
                                    nAvailableProcesses, pAvailableProcesses, mAvailableProcesses, timeout, maxTimePerSimulation);
                            displayLiveStatistics();
                            system.startSimulations(txtDataDisplay);
                        }
                    };
                    new Thread(toRun).start();
                } else {
                    JDialog nonValidParametersDialog = new JDialog();
                    JLabel message = new JLabel("One or more parameters are either missing or are not valid");
                    message.setFont(new Font("Normal", Font.BOLD, 20));
                    nonValidParametersDialog.add(message);
                    nonValidParametersDialog.pack();
                    nonValidParametersDialog.setVisible(true);
                }

            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1, 0, 20));

        mainPanel.add(panelNumberOfSimulations);
        mainPanel.add(panelMaxTimePerSimulation);
        mainPanel.add(panelSlowMode);
        mainPanel.add(panelDelay);
        mainPanel.add(panelKConnections);
        mainPanel.add(panelSystemCalls);
        mainPanel.add(panelNAvailableProcesses);
        mainPanel.add(panelPAvailableProcesses);
        mainPanel.add(panelMAvailableProcesses);
        mainPanel.add(panelTimeout);
        mainPanel.add(panelFortuna);
        mainPanel.add(panelStart);
        Border padding = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        mainPanel.setBorder(padding);

        super.add(mainPanel);
        super.setSize(900, 900);
        //super.setResizable(false);
        super.setVisible(true);
    }

    private void changeLayout(JComponent component) {
        Border padding = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        component.setBorder(padding);
        super.getContentPane().removeAll();
        super.add(component);
        super.revalidate();
        super.repaint();
    }

    private void displayLiveStatistics() {
        txtDataDisplay = new JTextArea();
        txtDataDisplay.setFont(new Font("Normal", Font.BOLD, 12));
        txtDataDisplay.setEditable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JButton btnNext = new JButton("Next");
        btnNext.setFont(new Font("Normal", Font.BOLD, 18));
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAllFinalSimulationResults();
            }
        });

        JScrollPane jsp = new JScrollPane(txtDataDisplay, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(jsp);
        panel.add(btnNext);
        changeLayout(panel);
    }

    public void displayAllFinalSimulationResults(){
        JTabbedPane tabbedPane = new JTabbedPane();
        Iterator<Statistics> iterator = system.getAllStatistics().iterator();
        int counter = 0;
        while(iterator.hasNext()){
            Statistics statistic = iterator.next();
            tabbedPane.addTab("Simulation " + (counter + 1), displayFinalSimulationResult(counter + 1,statistic));
            counter++;
        }
        Statistics generalStatistics = new Statistics(system.getAllStatistics());
        Writer.writeIndex(counter, generalStatistics.getTimePerTrial(), generalStatistics.getkConnections(),
                generalStatistics.getSystemCalls(), generalStatistics.getnAvailableProcesses(), generalStatistics.getpQueries(),
                generalStatistics.getmSentences(),generalStatistics.getTimeout());

        JPanel lastAveragePanel = displayFinalSimulationResult(0, generalStatistics);
        tabbedPane.addTab("Final Average", lastAveragePanel);
        changeLayout(tabbedPane);
    }

    public JPanel displayFinalSimulationResult(int simulationNumber, Statistics statistics) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel panelSimulation = new JPanel();
        panelSimulation.setLayout(new FlowLayout());
        JLabel lblSimulation = new JLabel();
        panelSimulation.add(lblSimulation);
        if(simulationNumber != 0) {
            lblSimulation.setText("Simulation number: ");
            JTextField txtSimulationNumber = new JTextField("" + simulationNumber);
            txtSimulationNumber.setFont(new Font("Normal", Font.BOLD, 20));
            txtSimulationNumber.setEditable(false);
            panelSimulation.add(txtSimulationNumber);
            Writer.writeStatistics(statistics, simulationNumber);
        }else{
            lblSimulation.setText("Final result");
        }
        lblSimulation.setFont(new Font("Normal", Font.BOLD, 20));
        JPanel panelConnectionLife = new JPanel(new FlowLayout());
        JLabel lblConnectionLife = new JLabel("Average connection lifetime: ");
        lblConnectionLife.setFont(new Font("Normal", Font.BOLD, 20));
        JTextField txtConnectionLifetime = new JTextField();
        txtConnectionLifetime.setFont(new Font("Normal", Font.BOLD, 20));
        txtConnectionLifetime.setEditable(false);
        txtConnectionLifetime.setText(statistics.getClientConnectionStatistics().getAverageQueryLifetime() + "");
        panelConnectionLife.add(lblConnectionLife);
        panelConnectionLife.add(txtConnectionLifetime);

        JPanel panelRejectedConnections = new JPanel();
        panelRejectedConnections.setLayout(new FlowLayout());
        JLabel lblRejectedConnections = new JLabel("Rejected connections: ");
        lblRejectedConnections.setFont(new Font("Normal", Font.BOLD, 20));
        JTextField txtRejectedConnections = new JTextField();
        txtRejectedConnections.setFont(new Font("Normal", Font.BOLD, 20));
        txtRejectedConnections.setEditable(false);
        txtRejectedConnections.setText(statistics.getRejectedConnections() + "");
        panelRejectedConnections.add(lblRejectedConnections);
        panelRejectedConnections.add(txtRejectedConnections);

        JPanel panelAveragePerModuleTitle = new JPanel();
        JLabel lblAveragePerModule = new JLabel("Average time per module (per type of query): ");
        lblAveragePerModule.setFont(new Font("Normal", Font.BOLD, 20));
        panelAveragePerModuleTitle.add(lblAveragePerModule);

        JPanel panelAveragePerModule = new JPanel();
        panelAveragePerModule.setLayout(new BoxLayout(panelAveragePerModule, BoxLayout.PAGE_AXIS));
        panelAveragePerModule.setLayout(new GridLayout(3, 3, 5, 5));
        JTextArea txtAverageInModule1 = new JTextArea("Client Connection Module (without a solved query): \nDDL: \t " + statistics.getClientConnectionStatistics().getAverageDdlTime() + "\nJoin: \t" +
                statistics.getClientConnectionStatistics().getAverageJoinTime() + "\nSelect: \t" + statistics.getClientConnectionStatistics().getAverageSelectTime() + "\nUpdate: \t" +
                statistics.getClientConnectionStatistics().getAverageUpdateTime() + "\nTotal idle time: \t" + statistics.getClientConnectionStatistics().getIdleTime() + "\nTotal average time in queue: \t"
                + statistics.getClientConnectionStatistics().getAverageQueueSize());

        JTextArea txtAverageInModule2 = new JTextArea("Process Manager Module: \nDDL: \t " + statistics.getProcessManagerStatistics().getAverageDdlTime() + "\nJoin: \t" +
                statistics.getProcessManagerStatistics().getAverageJoinTime() + "\nSelect: \t" + statistics.getProcessManagerStatistics().getAverageSelectTime() + "\nUpdate: \t" +
                statistics.getProcessManagerStatistics().getAverageUpdateTime() + "\nTotal idle time: \t" + statistics.getProcessManagerStatistics().getIdleTime() + "\nTotal average time in queue: \t"
                + statistics.getProcessManagerStatistics().getAverageQueueSize());

        JTextArea txtAverageInModule3 = new JTextArea("Query Processing Module: \nDDL: \t " + statistics.getQueryProcessingStatistics().getAverageDdlTime() + "\nJoin: \t" +
                statistics.getQueryProcessingStatistics().getAverageJoinTime() + "\nSelect: \t" + statistics.getQueryProcessingStatistics().getAverageSelectTime() + "\nUpdate: \t" +
                statistics.getQueryProcessingStatistics().getAverageUpdateTime() + "\nTotal idle time: \t" + statistics.getQueryProcessingStatistics().getIdleTime() + "\nTotal average time in queue: \t"
                + statistics.getQueryProcessingStatistics().getAverageQueueSize());

        JTextArea txtAverageInModule4 = new JTextArea("Transaction and Data Access Module: \nDDL: \t " + statistics.getTransactionAndDataStatistics().getAverageDdlTime() + "\nJoin: \t" +
                statistics.getTransactionAndDataStatistics().getAverageJoinTime() + "\nSelect: \t" + statistics.getTransactionAndDataStatistics().getAverageSelectTime() + "\nUpdate: \t" +
                statistics.getTransactionAndDataStatistics().getAverageUpdateTime() + "\nTotal idle time: \t" + statistics.getTransactionAndDataStatistics().getIdleTime() + "\nTotal average time in queue: \t"
                + statistics.getTransactionAndDataStatistics().getAverageQueueSize());

        JTextArea txtAverageInModule5 = new JTextArea("Execution Module: \nDDL: \t " + statistics.getExecutionStatistics().getAverageDdlTime() + "\nJoin: \t" +
                statistics.getExecutionStatistics().getAverageJoinTime() + "\nSelect: \t" + statistics.getExecutionStatistics().getAverageSelectTime() + "\nUpdate: \t" +
                statistics.getExecutionStatistics().getAverageUpdateTime() + "\nTotal idle time: \t" + statistics.getExecutionStatistics().getIdleTime() + "\nTotal average time in queue: \t"
                + statistics.getExecutionStatistics().getAverageQueueSize());

        JTextArea txtAverageInModule6 = new JTextArea("Client Connection Module (with a solved query): \nDDL: \t " + statistics.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageDdlTime() + "\nJoin: \t" +
                statistics.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageJoinTime() + "\nSelect: \t" + statistics.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageSelectTime() + "\nUpdate: \t" +
                statistics.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageUpdateTime() + "\nTotal idle time: \t" + statistics.getClientConnectionStatisticsWithASolvedQueryStatistics().getIdleTime() + "\nTotal average time in queue: \t"
                + statistics.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageQueueSize());

        txtAverageInModule1.setFont(new Font("Normal", Font.BOLD, 15));
        txtAverageInModule2.setFont(new Font("Normal", Font.BOLD, 15));
        txtAverageInModule3.setFont(new Font("Normal", Font.BOLD, 15));
        txtAverageInModule4.setFont(new Font("Normal", Font.BOLD, 15));
        txtAverageInModule5.setFont(new Font("Normal", Font.BOLD, 15));
        txtAverageInModule6.setFont(new Font("Normal", Font.BOLD, 15));

        txtAverageInModule1.setEditable(false);
        txtAverageInModule2.setEditable(false);
        txtAverageInModule3.setEditable(false);
        txtAverageInModule4.setEditable(false);
        txtAverageInModule5.setEditable(false);
        txtAverageInModule6.setEditable(false);

        panelAveragePerModule.add(txtAverageInModule1);
        panelAveragePerModule.add(txtAverageInModule2);
        panelAveragePerModule.add(txtAverageInModule3);
        panelAveragePerModule.add(txtAverageInModule4);
        panelAveragePerModule.add(txtAverageInModule5);
        panelAveragePerModule.add(txtAverageInModule6);


        mainPanel.add(panelSimulation);
        mainPanel.add(panelConnectionLife);
        mainPanel.add(panelRejectedConnections);
        mainPanel.add(panelAveragePerModuleTitle);
        mainPanel.add(panelAveragePerModule);

        if(simulationNumber == 0){
            JButton htmlButton = new JButton("Display statistics by simulation");
            htmlButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    File f = new File("statistics/index.html");
                    try{
                        Desktop d = Desktop.getDesktop();
                        d.open(f);
                    }catch(IOException exc){
                        exc.printStackTrace();
                    }
                }
            });
            mainPanel.add(htmlButton);

        }

        return mainPanel;
    }

    private boolean areParametersValid() {

        boolean validParameters = true;

        if (!digitValidation(txtNumberOfSimulations.getText(), false))
            validParameters = false;

        if (!digitValidation(txtMaxTimePerSimulation.getText(), false))
            validParameters = false;

        if (chkSlowMode.isSelected() && !digitValidation(txtDelay.getText(), true))
            validParameters = false;

        if (!digitValidation(txtKConnections.getText(), false))
            validParameters = false;

        if (!digitValidation(txtSystemCalls.getText(), false))
            validParameters = false;

        if (!digitValidation(txtNAvailableProcesses.getText(), false))
            validParameters = false;

        if (!digitValidation(txtPAvailableProcesses.getText(), false))
            validParameters = false;

        if (!digitValidation(txtMAvailableProcesses.getText(), false))
            validParameters = false;

        if (!digitValidation(txtTimeout.getText(), true))
            validParameters = false;

        if (!digitValidation(txtMaxTimePerSimulation.getText(), true))
            validParameters = false;

        return validParameters;
    }

    private boolean digitValidation(String number, boolean isDouble) {
        boolean isDigit = true;
        if (number.equals(""))
            isDigit = false;

        for (int i = 0; i < number.length() && isDigit; i++) {
            char currentCharacter = number.charAt(i);
            if (!Character.isDigit(currentCharacter)) {
                if (!isDouble) {
                    isDigit = false;
                } else {
                    if (currentCharacter != '.')
                        isDigit = false;
                }
            }
        }
        return isDigit;
    }

    public static void main(String... args) {
        GUI gui = new GUI();
    }
}