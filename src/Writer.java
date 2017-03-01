import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by Ian on 18/2/2017.
 */
public class Writer {

    /**
     * Static method used to write the general results of the simulation into an html file using the Apache Velocity library
     * It takes the template for the index html from the templates folder, it then switches the values with the dollar sign
     * in the html for the parameters passed. After that, for every simulation ran, it creates a hyperlink to the specific simulation
     * and creates an html file for each one of them. Finally, the final result is written to a new html file in the statistics directory
     * @param numberOfSimulations number of simulations ran
     * @param maxTimePerSimulation maximum time allowed for the simulations to run
     * @param kConnections maximum number of connections
     * @param systemCalls maximum amount of system calls for the simulations
     * @param nAvailableProcesses available processes for query processing
     * @param pAvailableProcesses available processes for query transactions
     * @param mAvailableProcesses available processes for query executions
     * @param timeout timeout of the queries.
     */
    public static void writeIndex(int numberOfSimulations, double maxTimePerSimulation, int kConnections, int systemCalls,
                                  int nAvailableProcesses, int pAvailableProcesses, int mAvailableProcesses, double timeout) {
        VelocityEngine ve = new VelocityEngine();
        ve.init();

        Template t = ve.getTemplate("templates/IndexTemplate.html");
        VelocityContext vc = new VelocityContext();

        vc.put("numberOfSimulations", "" + numberOfSimulations);
        vc.put("maxTimePerSimulation", "" + maxTimePerSimulation);
        vc.put("kConnections", "" + kConnections);
        vc.put("systemCalls", "" + systemCalls);
        vc.put("nAvailableProcesses", "" + nAvailableProcesses);
        vc.put("pAvailableProcesses", "" + pAvailableProcesses);
        vc.put("mAvailableProcesses", "" + mAvailableProcesses);
        vc.put("timeout", "" + timeout);
        StringWriter sw = new StringWriter();

        t.merge(vc, sw);
        String code = sw.toString();
        String link = "";
        for (int i = 1; i <= numberOfSimulations; i++) {
            String path = "simulation" + i + ".html";
            link += "\t\t<a href=" + path + ">Simulation" + i + "</a><br>\n";
            File file = new File(path);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        code = code.replaceAll("</body>", link + "\n\t</body>");
        try {
            FileWriter fw = new FileWriter("statistics/index.html");
            fw.write(code);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeStatistics(Statistics statistic, int statisticNumber){
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();
        Template template = velocityEngine.getTemplate("templates/simulationTemplate.html");
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("simulationNumber", statisticNumber);
        velocityContext.put("maxTimePerSimulation", statistic.getTotalTimeSimulation());
        velocityContext.put("kConnections", statistic.getkConnections());
        velocityContext.put("nAvailableProcesses", statistic.getnAvailableProcesses());
        velocityContext.put("pAvailableProcesses", statistic.getpQueries());
        velocityContext.put("mAvailableProcesses", statistic.getmSentences());
        velocityContext.put("timeout", statistic.getTimeout());
        velocityContext.put("avgQueryLifetime", statistic.getAvgQueryLifetime());
        velocityContext.put("rejectedConnections", statistic.getRejectedConnections());

        //Stability
        velocityContext.put("clientConnectionStability", Boolean.toString(statistic.getClientConnectionStatistics().getAverageOccupiedTimeRho() < 1));
        velocityContext.put("processManagerStability", Boolean.toString(statistic.getProcessManagerStatistics().getAverageOccupiedTimeRho() < 1));
        velocityContext.put("queryProcessingStability", Boolean.toString(statistic.getQueryProcessingStatistics().getAverageOccupiedTimeRho() < 1));
        velocityContext.put("transactionAndDataStability", Boolean.toString(statistic.getTransactionAndDataStatistics().getAverageOccupiedTimeRho() < 1));
        velocityContext.put("executionStability", Boolean.toString(statistic.getExecutionStatistics().getAverageOccupiedTimeRho() < 1));

        //Lambdas
        velocityContext.put("clientConnectionLambda", statistic.getClientConnectionStatistics().getTrueLambda());
        velocityContext.put("processManagerLambda", statistic.getProcessManagerStatistics().getTrueLambda());
        velocityContext.put("queryProcessingLambda", statistic.getQueryProcessingStatistics().getTrueLambda());
        velocityContext.put("transactionAndDataLambda", statistic.getTransactionAndDataStatistics().getTrueLambda());
        velocityContext.put("executionLambda", statistic.getExecutionStatistics().getTrueLambda());

        //Mus
        velocityContext.put("clientConnectionMu", statistic.getClientConnectionStatistics().getAvgServiceTimeMu());
        velocityContext.put("processManagerMu", statistic.getProcessManagerStatistics().getAvgServiceTimeMu());
        velocityContext.put("queryProcessingMu", statistic.getQueryProcessingStatistics().getAvgServiceTimeMu());
        velocityContext.put("transactionAndDataMu", statistic.getTransactionAndDataStatistics().getAvgServiceTimeMu());
        velocityContext.put("executionMu", statistic.getExecutionStatistics().getAvgServiceTimeMu());

        //Rhos
        velocityContext.put("clientConnectionRho", statistic.getClientConnectionStatistics().getAverageOccupiedTimeRho());
        velocityContext.put("processManagerRho", statistic.getProcessManagerStatistics().getAverageOccupiedTimeRho());
        velocityContext.put("queryProcessingRho", statistic.getQueryProcessingStatistics().getAverageOccupiedTimeRho());
        velocityContext.put("transactionAndDataRho", statistic.getTransactionAndDataStatistics().getAverageOccupiedTimeRho());
        velocityContext.put("executionRho", statistic.getExecutionStatistics().getAverageOccupiedTimeRho());

        //L's
        velocityContext.put("clientConnectionL", statistic.getClientConnectionStatistics().getAverageQueriesL());
        velocityContext.put("processManagerL", statistic.getProcessManagerStatistics().getAverageQueriesL());
        velocityContext.put("queryProcessingL", statistic.getQueryProcessingStatistics().getAverageQueriesL());
        velocityContext.put("transactionAndDataL", statistic.getTransactionAndDataStatistics().getAverageQueriesL());
        velocityContext.put("executionL", statistic.getExecutionStatistics().getAverageQueriesL());

        //Lq's
        velocityContext.put("clientConnectionLq", statistic.getClientConnectionStatistics().getAverageQueriesLQ());
        velocityContext.put("processManagerLq", statistic.getProcessManagerStatistics().getAverageQueriesLQ());
        velocityContext.put("queryProcessingLq", statistic.getQueryProcessingStatistics().getAverageQueriesLQ());
        velocityContext.put("transactionAndDataLq", statistic.getTransactionAndDataStatistics().getAverageQueriesLQ());
        velocityContext.put("executionLq", statistic.getExecutionStatistics().getAverageQueriesLQ());

        //Ls's
        velocityContext.put("clientConnectionLs", statistic.getClientConnectionStatistics().getAverageQueriesLS());
        velocityContext.put("processManagerLs", statistic.getProcessManagerStatistics().getAverageQueriesLS());
        velocityContext.put("queryProcessingLs", statistic.getQueryProcessingStatistics().getAverageQueriesLS());
        velocityContext.put("transactionAndDataLs", statistic.getTransactionAndDataStatistics().getAverageQueriesLS());
        velocityContext.put("executionLs", statistic.getExecutionStatistics().getAverageQueriesLS());

        //W's
        velocityContext.put("clientConnectionW", statistic.getClientConnectionStatistics().getAverageTimeW());
        velocityContext.put("processManagerW", statistic.getProcessManagerStatistics().getAverageTimeW());
        velocityContext.put("queryProcessingW", statistic.getQueryProcessingStatistics().getAverageTimeW());
        velocityContext.put("transactionAndDataW", statistic.getTransactionAndDataStatistics().getAverageTimeW());
        velocityContext.put("executionW", statistic.getExecutionStatistics().getAverageTimeW());

        //Wq's
        velocityContext.put("clientConnectionWq", statistic.getClientConnectionStatistics().getAverageTimeWQ());
        velocityContext.put("processManagerWq", statistic.getProcessManagerStatistics().getAverageTimeWQ());
        velocityContext.put("queryProcessingWq", statistic.getQueryProcessingStatistics().getAverageTimeWQ());
        velocityContext.put("transactionAndDataWq", statistic.getTransactionAndDataStatistics().getAverageTimeWQ());
        velocityContext.put("executionWq", statistic.getExecutionStatistics().getAverageTimeWQ());

        //Ws's
        velocityContext.put("clientConnectionWs", 1 / statistic.getClientConnectionStatistics().getAvgServiceTimeMu());
        velocityContext.put("processManagerWs", 1 / statistic.getProcessManagerStatistics().getAvgServiceTimeMu());
        velocityContext.put("queryProcessingWs", 1 / statistic.getQueryProcessingStatistics().getAvgServiceTimeMu());
        velocityContext.put("transactionAndDataWs", 1 / statistic.getTransactionAndDataStatistics().getAvgServiceTimeMu());
        velocityContext.put("executionWs", 1 / statistic.getExecutionStatistics().getAvgServiceTimeMu());

        //Queue sizes
        velocityContext.put("clientConnectionQueueSize", statistic.getClientConnectionStatistics().getAverageQueueSize());
        velocityContext.put("processManagerQueueSize", statistic.getProcessManagerStatistics().getAverageQueueSize());
        velocityContext.put("queryProcessingQueueSize", statistic.getQueryProcessingStatistics().getAverageQueueSize());
        velocityContext.put("transactionAndDataQueueSize", statistic.getTransactionAndDataStatistics().getAverageQueueSize());
        velocityContext.put("executionQueueSize", statistic.getExecutionStatistics().getAverageQueueSize());

        StringWriter stringWriter = new StringWriter();
        template.merge(velocityContext, stringWriter);

        try{
            FileWriter fw = new FileWriter("statistics/simulation" + statisticNumber + ".html");
            fw.write(stringWriter.toString());
            fw.flush();
            fw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String... args) {
        Writer.writeIndex(5, 2.345, 3, 5, 6, 1, 5, 3.444);
    }
}