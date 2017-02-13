import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Paola Ortega S on 2/8/2017.
 */
public class GUI extends JFrame{
    private final String TITLE = "Simulation";
    private final String NUMBER_OF_SIMULATIONS = "Number of simulations";
    private final String MAX_TIME_PER_SIMULATION = "Maximum time to run the simulation";
    private final String SLOW_MODE = "Slow Mode";
    private final String K_CONNECTIONS = "Number of connections to be handled (k)";
    private final String N_AVAILABLE_PROCESSES = "Number of available processes for query processing (n)";
    private final String P_AVAILABLE_PROCESSES = "Number of available processes for query transactions (p)";
    private final String M_AVAILABLE_PROCESSES = "Number of available processes for query executions (m)";
    private final String T_TIMEOUT = "Connection timeout (t)";
    private final String DELAY = "Delay";
    private final String START = "Start";
    private final String O_FORTUNA = "O Fortuna!";

    private JPanel panelNumberOfSimulations;
    private JLabel lblNumberOfSimulations;
    private JTextField txtNumberOfSimulations;

    private JPanel panelMaxTimePerSimulation;
    private JLabel lblMaxTimePerSimulation;
    private JTextField txtMaxTimePerSimulation;

    private JPanel panelSlowMode;
    private JLabel lblSlowMode;
    private JCheckBox chkSlowMode;

    private JPanel panelDelay;
    private JLabel lblDelay;
    private JTextField txtDelay;

    private JPanel panelKConnections;
    private JLabel lblKConnections;
    private JTextField txtKConnections;

    private JPanel panelNAvailableProcesses;
    private JLabel lblNAvailableProcesses;
    private JTextField txtNAvailableProcesses;

    private JPanel panelPAvailableProcesses;
    private JLabel lblPAvailableProcesses;
    private JTextField txtPAvailableProcesses;

    private JPanel panelMAvailableProcesses;
    private JLabel lblMAvailableProcesses;
    private JTextField txtMAvailableProcesses;

    private JPanel panelTimeout;
    private JLabel lblTimeout;
    private JTextField txtTimeout;

    private JPanel panelFortuna;
    private JCheckBox chkFortuna;

    private JPanel panelStart;
    private JButton btnStart;

    private JTextArea dataDisplay;

    public GUI() {
        super.setTitle(TITLE);
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        lblNumberOfSimulations = new JLabel(NUMBER_OF_SIMULATIONS);
        lblNumberOfSimulations.setFont(new Font("Normal", Font.BOLD, 18));
        txtNumberOfSimulations = new JTextField();
        txtNumberOfSimulations.setFont(new Font("Normal", Font.BOLD, 18));
        txtNumberOfSimulations.setColumns(20);
        panelNumberOfSimulations = new JPanel();
        panelNumberOfSimulations.setLayout(new BorderLayout());
        panelNumberOfSimulations.add(lblNumberOfSimulations, BorderLayout.WEST);
        panelNumberOfSimulations.add(txtNumberOfSimulations, BorderLayout.EAST);

        lblMaxTimePerSimulation = new JLabel(MAX_TIME_PER_SIMULATION);
        lblMaxTimePerSimulation.setFont(new Font("Normal", Font.BOLD, 18));
        txtMaxTimePerSimulation = new JTextField();
        txtMaxTimePerSimulation.setColumns(20);
        txtMaxTimePerSimulation.setFont(new Font("Normal", Font.BOLD, 18));
        panelMaxTimePerSimulation = new JPanel();
        panelMaxTimePerSimulation.setLayout(new BorderLayout());
        panelMaxTimePerSimulation.add(lblMaxTimePerSimulation, BorderLayout.WEST);
        panelMaxTimePerSimulation.add(txtMaxTimePerSimulation, BorderLayout.EAST);

        lblSlowMode = new JLabel(SLOW_MODE);
        lblSlowMode.setFont(new Font("Normal", Font.BOLD, 18));
        chkSlowMode = new JCheckBox();
        chkSlowMode.setSelected(false);
        chkSlowMode.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(chkSlowMode.isSelected()){
                    txtDelay.setEnabled(true);
                }else{
                    txtDelay.setEnabled(false);
                }
            }
        });
        panelSlowMode = new JPanel();
        panelSlowMode.setLayout(new BorderLayout());
        panelSlowMode.add(lblSlowMode, BorderLayout.WEST);
        panelSlowMode.add(chkSlowMode, BorderLayout.EAST);

        lblDelay = new JLabel(DELAY);
        lblDelay.setFont(new Font("Normal", Font.BOLD, 18));
        txtDelay = new JTextField();
        txtDelay.setFont(new Font("Normal", Font.BOLD, 18));
        txtDelay.setColumns(20);
        txtDelay.setEnabled(false);
        panelDelay = new JPanel();
        panelDelay.setLayout(new BorderLayout());
        panelDelay.add(lblDelay, BorderLayout.WEST);
        panelDelay.add(txtDelay, BorderLayout.EAST);

        lblKConnections = new JLabel(K_CONNECTIONS);
        lblKConnections.setFont(new Font("Normal", Font.BOLD, 18));
        txtKConnections = new JTextField();
        txtKConnections.setColumns(20);
        txtKConnections.setFont(new Font("Normal", Font.BOLD, 18));
        panelKConnections = new JPanel();
        panelKConnections.setLayout(new BorderLayout());
        panelKConnections.add(lblKConnections, BorderLayout.WEST);
        panelKConnections.add(txtKConnections, BorderLayout.EAST);

        lblNAvailableProcesses = new JLabel(N_AVAILABLE_PROCESSES);
        lblNAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        txtNAvailableProcesses = new JTextField();
        txtNAvailableProcesses.setColumns(20);
        txtNAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        panelNAvailableProcesses = new JPanel();
        panelNAvailableProcesses.setLayout(new BorderLayout());
        panelNAvailableProcesses.add(lblNAvailableProcesses, BorderLayout.WEST);
        panelNAvailableProcesses.add(txtNAvailableProcesses, BorderLayout.EAST);

        lblPAvailableProcesses = new JLabel(P_AVAILABLE_PROCESSES);
        lblPAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        txtPAvailableProcesses = new JTextField();
        txtPAvailableProcesses.setColumns(20);
        txtPAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        panelPAvailableProcesses = new JPanel();
        panelPAvailableProcesses.setLayout(new BorderLayout());
        panelPAvailableProcesses.add(lblPAvailableProcesses, BorderLayout.WEST);
        panelPAvailableProcesses.add(txtPAvailableProcesses, BorderLayout.EAST);

        lblMAvailableProcesses = new JLabel(M_AVAILABLE_PROCESSES);
        lblMAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        txtMAvailableProcesses = new JTextField();
        txtMAvailableProcesses.setColumns(20);
        txtMAvailableProcesses.setFont(new Font("Normal", Font.BOLD, 18));
        panelMAvailableProcesses = new JPanel();
        panelMAvailableProcesses.setLayout(new BorderLayout());
        panelMAvailableProcesses.add(lblMAvailableProcesses, BorderLayout.WEST);
        panelMAvailableProcesses.add(txtMAvailableProcesses, BorderLayout.EAST);

        lblTimeout = new JLabel(T_TIMEOUT);
        lblTimeout.setFont(new Font("Normal", Font.BOLD, 18));
        txtTimeout = new JTextField();
        txtTimeout.setColumns(20);
        txtTimeout.setFont(new Font("Normal", Font.BOLD, 18));
        panelTimeout = new JPanel();
        panelTimeout.setLayout(new BorderLayout());
        panelTimeout.add(lblTimeout, BorderLayout.WEST);
        panelTimeout.add(txtTimeout, BorderLayout.EAST);

        chkFortuna = new JCheckBox(O_FORTUNA);
        chkFortuna.setFont(new Font("Normal", Font.BOLD, 18));
        panelFortuna = new JPanel();
        panelFortuna.add(chkFortuna);

        btnStart = new JButton(START);
        btnStart.setSize(100, 100);
        btnStart.setFont(new Font("Normal", Font.BOLD, 18));
        panelStart = new JPanel();
        panelStart.add(btnStart);

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayLiveStatistics();
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1, 0, 20));

        mainPanel.add(panelNumberOfSimulations);
        mainPanel.add(panelMaxTimePerSimulation);
        mainPanel.add(panelSlowMode);
        mainPanel.add(panelDelay);
        mainPanel.add(panelKConnections);
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
        super.setResizable(false);
        super.setVisible(true);
    }

    private void changeLayout(JComponent component){
        Border padding = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        component.setBorder(padding);
        super.getContentPane().removeAll();
        super.add(component);
        super.revalidate();
        super.repaint();
    }

    private void displayLiveStatistics(){
        JTextArea txtArea = new JTextArea();
        txtArea.setFont(new Font("Normal", Font.BOLD, 20));
        txtArea.setEditable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JButton btnNext = new JButton("Next");
        btnNext.setFont(new Font("Normal", Font.BOLD, 18));
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayFinalSimulationResult(0);
            }
        });
        JScrollPane jsp = new JScrollPane(txtArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(jsp);
        panel.add(btnNext);
        changeLayout(panel);
    }

    public void displayFinalSimulationResult(int simulationNumber){
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel panelSimulation = new JPanel();
        panelSimulation.setLayout(new FlowLayout());
        JLabel lblSimulation = new JLabel("Simulation number: ");
        lblSimulation.setFont(new Font("Normal", Font.BOLD, 20));
        JTextField txtSimulationNumber = new JTextField("" + simulationNumber);
        txtSimulationNumber.setFont(new Font("Normal", Font.BOLD, 20));
        txtSimulationNumber.setEditable(false);
        panelSimulation.add(lblSimulation);
        panelSimulation.add(txtSimulationNumber);

        JPanel panelConnectionLife = new JPanel(new FlowLayout());
        JLabel lblConnectionLife = new JLabel("Average connection lifetime: ");
        lblConnectionLife.setFont(new Font("Normal", Font.BOLD, 20));
        JTextField txtConnectionLifetime = new JTextField();
        txtConnectionLifetime.setFont(new Font("Normal", Font.BOLD, 20));
        txtConnectionLifetime.setEditable(false);
        txtConnectionLifetime.setText("0.1");
        panelConnectionLife.add(lblConnectionLife);
        panelConnectionLife.add(txtConnectionLifetime);

        JPanel panelRejectedConnections = new JPanel();
        panelRejectedConnections.setLayout(new FlowLayout());
        JLabel lblRejectedConnections = new JLabel("Rejected connections: ");
        lblRejectedConnections.setFont(new Font("Normal", Font.BOLD, 20));
        JTextField txtRejectedConnections = new JTextField();
        txtRejectedConnections.setFont(new Font("Normal", Font.BOLD, 20));
        txtRejectedConnections.setEditable(false);
        txtRejectedConnections.setText("54");
        panelRejectedConnections.add(lblRejectedConnections);
        panelRejectedConnections.add(txtRejectedConnections);

        JPanel panelAveragePerModuleTitle = new JPanel();
        JLabel lblAveragePerModule = new JLabel("Average time per module (per type of query): ");
        lblAveragePerModule.setFont(new Font("Normal", Font.BOLD, 20));
        panelAveragePerModuleTitle.add(lblAveragePerModule);

        JPanel panelAveragePerModule = new JPanel();
        panelAveragePerModule.setLayout(new BoxLayout(panelAveragePerModule, BoxLayout.PAGE_AXIS));
        panelAveragePerModule.setLayout(new GridLayout(3, 3, 5, 5));
        JTextArea txtAverageInModule1 = new JTextArea("Client Connection Module (without a solved query): \nDDL: \t " + "\nJoin: \t" + "\nSelect: \t" + "\nUpdate: \t" + "\nTotal idle time: \t" + "\nTotal average time in queue: \t"); //TODO pasar cada promedio
        JTextArea txtAverageInModule2 = new JTextArea("Query Processing Module: \nDDL: \t " + "\nJoin: \t" + "\nSelect: \t" + "\nUpdate: \t" + "\nTotal idle time: \t" + "\nTotal average time in queue: \t");
        JTextArea txtAverageInModule3 = new JTextArea("Process Manager Module: \nDDL: \t" + "\nJoin: \t" + "\nSelect: \t" + "\nUpdate: \t" + "\nTotal idle time: \t" + "\nTotal average time in queue: \t");
        JTextArea txtAverageInModule4 = new JTextArea("Transaction and Data Access Module: \nDDL: \t" + "\nJoin: \t" + "\nSelect: \t" + "\nUpdate: \t" + "\nTotal idle time: \t" + "\nTotal average time in queue: \t");
        JTextArea txtAverageInModule5 = new JTextArea("Execution Module: \nDDL: \t" + "\nJoin: \t" + "\nSelect: \t" + "\nUpdate: \t" + "\nTotal idle time: \t" + "\nTotal average time in queue: \t");
        JTextArea txtAverageInModule6 = new JTextArea("Client Connection Module (with a solved query): \nDDL: \t" + "\nJoin: \t" + "\nSelect: \t" + "\nUpdate: \t" + "\nTotal idle time: \t" + "\nTotal average time in queue: \t");

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
        changeLayout(mainPanel);
    }

    public static void main(String... args){
        GUI gui = new GUI();
    }
}
