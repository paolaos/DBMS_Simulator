import javax.swing.*;
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

    public GUI() {
        super.setTitle(TITLE);
        super.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        super.setLayout(new GridLayout(0, 1, 0, 20));

        lblNumberOfSimulations = new JLabel(NUMBER_OF_SIMULATIONS);
        txtNumberOfSimulations = new JTextField();
        txtNumberOfSimulations.setColumns(20);
        panelNumberOfSimulations = new JPanel();
        panelNumberOfSimulations.setLayout(new BorderLayout());
        panelNumberOfSimulations.add(lblNumberOfSimulations, BorderLayout.WEST);
        panelNumberOfSimulations.add(txtNumberOfSimulations, BorderLayout.EAST);

        lblMaxTimePerSimulation = new JLabel(MAX_TIME_PER_SIMULATION);
        txtMaxTimePerSimulation = new JTextField();
        txtMaxTimePerSimulation.setColumns(20);
        panelMaxTimePerSimulation = new JPanel();
        panelMaxTimePerSimulation.setLayout(new BorderLayout());
        panelMaxTimePerSimulation.add(lblMaxTimePerSimulation, BorderLayout.WEST);
        panelMaxTimePerSimulation.add(txtMaxTimePerSimulation, BorderLayout.EAST);

        lblSlowMode = new JLabel(SLOW_MODE);
        chkSlowMode = new JCheckBox();
        panelSlowMode = new JPanel();
        panelSlowMode.setLayout(new BorderLayout());
        panelSlowMode.add(lblSlowMode, BorderLayout.WEST);
        panelSlowMode.add(chkSlowMode, BorderLayout.EAST);

        lblKConnections = new JLabel(K_CONNECTIONS);
        txtKConnections = new JTextField();
        txtKConnections.setColumns(20);
        panelKConnections = new JPanel();
        panelKConnections.setLayout(new BorderLayout());
        panelKConnections.add(lblKConnections, BorderLayout.WEST);
        panelKConnections.add(txtKConnections, BorderLayout.EAST);

        lblNAvailableProcesses = new JLabel(N_AVAILABLE_PROCESSES);
        txtNAvailableProcesses = new JTextField();
        txtNAvailableProcesses.setColumns(20);
        panelNAvailableProcesses = new JPanel();
        panelNAvailableProcesses.setLayout(new BorderLayout());
        panelNAvailableProcesses.add(lblNAvailableProcesses, BorderLayout.WEST);
        panelNAvailableProcesses.add(txtNAvailableProcesses, BorderLayout.EAST);

        lblPAvailableProcesses = new JLabel(P_AVAILABLE_PROCESSES);
        txtPAvailableProcesses = new JTextField();
        txtPAvailableProcesses.setColumns(20);
        panelPAvailableProcesses = new JPanel();
        panelPAvailableProcesses.setLayout(new BorderLayout());
        panelPAvailableProcesses.add(lblPAvailableProcesses, BorderLayout.WEST);
        panelPAvailableProcesses.add(txtPAvailableProcesses, BorderLayout.EAST);

        lblMAvailableProcesses = new JLabel(M_AVAILABLE_PROCESSES);
        txtMAvailableProcesses = new JTextField();
        txtMAvailableProcesses.setColumns(20);
        panelMAvailableProcesses = new JPanel();
        panelMAvailableProcesses.setLayout(new BorderLayout());
        panelMAvailableProcesses.add(lblMAvailableProcesses, BorderLayout.WEST);
        panelMAvailableProcesses.add(txtMAvailableProcesses, BorderLayout.EAST);

        lblTimeout = new JLabel(T_TIMEOUT);
        txtTimeout = new JTextField();
        txtTimeout.setColumns(20);
        panelTimeout = new JPanel();
        panelTimeout.setLayout(new BorderLayout());
        panelTimeout.add(lblTimeout, BorderLayout.WEST);
        panelTimeout.add(txtTimeout, BorderLayout.EAST);

        chkFortuna = new JCheckBox(O_FORTUNA);
        panelFortuna = new JPanel();
        panelFortuna.add(chkFortuna);

        btnStart = new JButton(START);
        btnStart.setSize(100, 100);
        panelStart = new JPanel();
        panelStart.add(btnStart);

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog jd = new JDialog();
                jd.add(new JLabel("Puto quien lo lea"));
                jd.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                jd.pack();
                jd.setVisible(true);
            }
        });

        super.add(panelNumberOfSimulations);
        super.add(panelMaxTimePerSimulation);
        super.add(panelSlowMode);
        super.add(panelKConnections);
        super.add(panelNAvailableProcesses);
        super.add(panelPAvailableProcesses);
        super.add(panelMAvailableProcesses);
        super.add(panelTimeout);
        super.add(panelFortuna);
        super.add(panelStart);
        super.setSize(700, 700);
        super.setVisible(true);
    }

    public static void main(String... args){
        GUI gui = new GUI();
    }
}
