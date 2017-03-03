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
     *
     * @param numberOfSimulations  number of simulations ran
     * @param maxTimePerSimulation maximum time allowed for the simulations to run
     * @param kConnections         maximum number of connections
     * @param systemCalls          maximum amount of system calls for the simulations
     * @param nAvailableProcesses  available processes for query processing
     * @param pAvailableProcesses  available processes for query transactions
     * @param mAvailableProcesses  available processes for query executions
     * @param timeout              timeout of the queries.
     */
    public static void writeIndex(int numberOfSimulations, double maxTimePerSimulation, int kConnections, int systemCalls,
                                  int nAvailableProcesses, int pAvailableProcesses, int mAvailableProcesses, double timeout, Statistics finalAverage) {
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

        vc.put("clientConnectionStability", Boolean.toString(finalAverage.getClientConnectionStatistics().getAverageOccupiedTimeRho() < 1));
        vc.put("processManagerStability", Boolean.toString(finalAverage.getProcessManagerStatistics().getAverageOccupiedTimeRho() < 1));
        vc.put("queryProcessingStability", Boolean.toString(finalAverage.getQueryProcessingStatistics().getAverageOccupiedTimeRho() < 1));
        vc.put("transactionAndDataStability", Boolean.toString(finalAverage.getTransactionAndDataStatistics().getAverageOccupiedTimeRho() < 1));
        vc.put("executionStability", Boolean.toString(finalAverage.getExecutionStatistics().getAverageOccupiedTimeRho() < 1));
        vc.put("lastModuleStability", Boolean.toString(finalAverage.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageOccupiedTimeRho() < 1));

        //Lambdas
        vc.put("clientConnectionLambda", finalAverage.getClientConnectionStatistics().getTrueLambda());
        vc.put("processManagerLambda", finalAverage.getProcessManagerStatistics().getTrueLambda());
        vc.put("queryProcessingLambda", finalAverage.getQueryProcessingStatistics().getTrueLambda());
        vc.put("transactionAndDataLambda", finalAverage.getTransactionAndDataStatistics().getTrueLambda());
        vc.put("executionLambda", finalAverage.getExecutionStatistics().getTrueLambda());
        vc.put("lastModuleLambda", finalAverage.getClientConnectionStatisticsWithASolvedQueryStatistics().getTrueLambda());

        //Mus
        vc.put("clientConnectionMu", finalAverage.getClientConnectionStatistics().getAvgServiceTimeMu());
        vc.put("processManagerMu", finalAverage.getProcessManagerStatistics().getAvgServiceTimeMu());
        vc.put("queryProcessingMu", finalAverage.getQueryProcessingStatistics().getAvgServiceTimeMu());
        vc.put("transactionAndDataMu", finalAverage.getTransactionAndDataStatistics().getAvgServiceTimeMu());
        vc.put("executionMu", finalAverage.getExecutionStatistics().getAvgServiceTimeMu());
        vc.put("lastModuleMu", finalAverage.getClientConnectionStatisticsWithASolvedQueryStatistics().getAvgServiceTimeMu());

        //Rhos
        vc.put("clientConnectionRho", finalAverage.getClientConnectionStatistics().getAverageOccupiedTimeRho());
        vc.put("processManagerRho", finalAverage.getProcessManagerStatistics().getAverageOccupiedTimeRho());
        vc.put("queryProcessingRho", finalAverage.getQueryProcessingStatistics().getAverageOccupiedTimeRho());
        vc.put("transactionAndDataRho", finalAverage.getTransactionAndDataStatistics().getAverageOccupiedTimeRho());
        vc.put("executionRho", finalAverage.getExecutionStatistics().getAverageOccupiedTimeRho());
        vc.put("lastModuleRho", finalAverage.getExecutionStatistics().getAverageOccupiedTimeRho());

        //L's
        vc.put("clientConnectionL", finalAverage.getClientConnectionStatistics().getAverageQueriesL());
        vc.put("processManagerL", finalAverage.getProcessManagerStatistics().getAverageQueriesL());
        vc.put("queryProcessingL", finalAverage.getQueryProcessingStatistics().getAverageQueriesL());
        vc.put("transactionAndDataL", finalAverage.getTransactionAndDataStatistics().getAverageQueriesL());
        vc.put("executionL", finalAverage.getExecutionStatistics().getAverageQueriesL());
        vc.put("lastModuleL", finalAverage.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageQueriesL());

        //Lq's
        vc.put("clientConnectionLq", finalAverage.getClientConnectionStatistics().getAverageQueriesLQ());
        vc.put("processManagerLq", finalAverage.getProcessManagerStatistics().getAverageQueriesLQ());
        vc.put("queryProcessingLq", finalAverage.getQueryProcessingStatistics().getAverageQueriesLQ());
        vc.put("transactionAndDataLq", finalAverage.getTransactionAndDataStatistics().getAverageQueriesLQ());
        vc.put("executionLq", finalAverage.getExecutionStatistics().getAverageQueriesLQ());
        vc.put("lastModuleLq", finalAverage.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageQueriesLQ());

        //Ls's
        vc.put("clientConnectionLs", finalAverage.getClientConnectionStatistics().getAverageQueriesLS());
        vc.put("processManagerLs", finalAverage.getProcessManagerStatistics().getAverageQueriesLS());
        vc.put("queryProcessingLs", finalAverage.getQueryProcessingStatistics().getAverageQueriesLS());
        vc.put("transactionAndDataLs", finalAverage.getTransactionAndDataStatistics().getAverageQueriesLS());
        vc.put("executionLs", finalAverage.getExecutionStatistics().getAverageQueriesLS());
        vc.put("lastModuleLs", finalAverage.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageQueriesLS());

        //W's
        vc.put("clientConnectionW", finalAverage.getClientConnectionStatistics().getAverageTimeW());
        vc.put("processManagerW", finalAverage.getProcessManagerStatistics().getAverageTimeW());
        vc.put("queryProcessingW", finalAverage.getQueryProcessingStatistics().getAverageTimeW());
        vc.put("transactionAndDataW", finalAverage.getTransactionAndDataStatistics().getAverageTimeW());
        vc.put("executionW", finalAverage.getExecutionStatistics().getAverageTimeW());
        vc.put("lastModuleW", finalAverage.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageTimeW());

        //Wq's
        vc.put("clientConnectionWq", finalAverage.getClientConnectionStatistics().getAverageTimeWQ());
        vc.put("processManagerWq", finalAverage.getProcessManagerStatistics().getAverageTimeWQ());
        vc.put("queryProcessingWq", finalAverage.getQueryProcessingStatistics().getAverageTimeWQ());
        vc.put("transactionAndDataWq", finalAverage.getTransactionAndDataStatistics().getAverageTimeWQ());
        vc.put("executionWq", finalAverage.getExecutionStatistics().getAverageTimeWQ());
        vc.put("lastModuleWq", finalAverage.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageTimeWQ());

        //Ws's
        vc.put("clientConnectionWs", 1 / finalAverage.getClientConnectionStatistics().getAvgServiceTimeMu());
        vc.put("processManagerWs", 1 / finalAverage.getProcessManagerStatistics().getAvgServiceTimeMu());
        vc.put("queryProcessingWs", 1 / finalAverage.getQueryProcessingStatistics().getAvgServiceTimeMu());
        vc.put("transactionAndDataWs", 1 / finalAverage.getTransactionAndDataStatistics().getAvgServiceTimeMu());
        vc.put("executionWs", 1 / finalAverage.getExecutionStatistics().getAvgServiceTimeMu());
        vc.put("lastModuleWs", 1 / finalAverage.getClientConnectionStatisticsWithASolvedQueryStatistics().getAvgServiceTimeMu());

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

    public static void writeStatistics(Statistics statistic, int statisticNumber) {
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
        velocityContext.put("lastModuleStability", Boolean.toString(statistic.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageOccupiedTimeRho() < 1));

        //Lambdas
        velocityContext.put("clientConnectionLambda", statistic.getClientConnectionStatistics().getTrueLambda());
        velocityContext.put("processManagerLambda", statistic.getProcessManagerStatistics().getTrueLambda());
        velocityContext.put("queryProcessingLambda", statistic.getQueryProcessingStatistics().getTrueLambda());
        velocityContext.put("transactionAndDataLambda", statistic.getTransactionAndDataStatistics().getTrueLambda());
        velocityContext.put("executionLambda", statistic.getExecutionStatistics().getTrueLambda());
        velocityContext.put("lastModuleLambda", statistic.getClientConnectionStatisticsWithASolvedQueryStatistics().getTrueLambda());

        //Mus
        velocityContext.put("clientConnectionMu", statistic.getClientConnectionStatistics().getAvgServiceTimeMu());
        velocityContext.put("processManagerMu", statistic.getProcessManagerStatistics().getAvgServiceTimeMu());
        velocityContext.put("queryProcessingMu", statistic.getQueryProcessingStatistics().getAvgServiceTimeMu());
        velocityContext.put("transactionAndDataMu", statistic.getTransactionAndDataStatistics().getAvgServiceTimeMu());
        velocityContext.put("executionMu", statistic.getExecutionStatistics().getAvgServiceTimeMu());
        velocityContext.put("lastModuleMu", statistic.getClientConnectionStatisticsWithASolvedQueryStatistics().getAvgServiceTimeMu());

        //Rhos
        velocityContext.put("clientConnectionRho", statistic.getClientConnectionStatistics().getAverageOccupiedTimeRho());
        velocityContext.put("processManagerRho", statistic.getProcessManagerStatistics().getAverageOccupiedTimeRho());
        velocityContext.put("queryProcessingRho", statistic.getQueryProcessingStatistics().getAverageOccupiedTimeRho());
        velocityContext.put("transactionAndDataRho", statistic.getTransactionAndDataStatistics().getAverageOccupiedTimeRho());
        velocityContext.put("executionRho", statistic.getExecutionStatistics().getAverageOccupiedTimeRho());
        velocityContext.put("lastModuleRho", statistic.getExecutionStatistics().getAverageOccupiedTimeRho());

        //L's
        velocityContext.put("clientConnectionL", statistic.getClientConnectionStatistics().getAverageQueriesL());
        velocityContext.put("processManagerL", statistic.getProcessManagerStatistics().getAverageQueriesL());
        velocityContext.put("queryProcessingL", statistic.getQueryProcessingStatistics().getAverageQueriesL());
        velocityContext.put("transactionAndDataL", statistic.getTransactionAndDataStatistics().getAverageQueriesL());
        velocityContext.put("executionL", statistic.getExecutionStatistics().getAverageQueriesL());
        velocityContext.put("lastModuleL", statistic.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageQueriesL());

        //Lq's
        velocityContext.put("clientConnectionLq", statistic.getClientConnectionStatistics().getAverageQueriesLQ());
        velocityContext.put("processManagerLq", statistic.getProcessManagerStatistics().getAverageQueriesLQ());
        velocityContext.put("queryProcessingLq", statistic.getQueryProcessingStatistics().getAverageQueriesLQ());
        velocityContext.put("transactionAndDataLq", statistic.getTransactionAndDataStatistics().getAverageQueriesLQ());
        velocityContext.put("executionLq", statistic.getExecutionStatistics().getAverageQueriesLQ());
        velocityContext.put("lastModuleLq", statistic.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageQueriesLQ());

        //Ls's
        velocityContext.put("clientConnectionLs", statistic.getClientConnectionStatistics().getAverageQueriesLS());
        velocityContext.put("processManagerLs", statistic.getProcessManagerStatistics().getAverageQueriesLS());
        velocityContext.put("queryProcessingLs", statistic.getQueryProcessingStatistics().getAverageQueriesLS());
        velocityContext.put("transactionAndDataLs", statistic.getTransactionAndDataStatistics().getAverageQueriesLS());
        velocityContext.put("executionLs", statistic.getExecutionStatistics().getAverageQueriesLS());
        velocityContext.put("lastModuleLs", statistic.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageQueriesLS());

        //W's
        velocityContext.put("clientConnectionW", statistic.getClientConnectionStatistics().getAverageTimeW());
        velocityContext.put("processManagerW", statistic.getProcessManagerStatistics().getAverageTimeW());
        velocityContext.put("queryProcessingW", statistic.getQueryProcessingStatistics().getAverageTimeW());
        velocityContext.put("transactionAndDataW", statistic.getTransactionAndDataStatistics().getAverageTimeW());
        velocityContext.put("executionW", statistic.getExecutionStatistics().getAverageTimeW());
        velocityContext.put("lastModuleW", statistic.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageTimeW());

        //Wq's
        velocityContext.put("clientConnectionWq", statistic.getClientConnectionStatistics().getAverageTimeWQ());
        velocityContext.put("processManagerWq", statistic.getProcessManagerStatistics().getAverageTimeWQ());
        velocityContext.put("queryProcessingWq", statistic.getQueryProcessingStatistics().getAverageTimeWQ());
        velocityContext.put("transactionAndDataWq", statistic.getTransactionAndDataStatistics().getAverageTimeWQ());
        velocityContext.put("executionWq", statistic.getExecutionStatistics().getAverageTimeWQ());
        velocityContext.put("lastModuleWq", statistic.getClientConnectionStatisticsWithASolvedQueryStatistics().getAverageTimeWQ());
        //Ws's
        velocityContext.put("clientConnectionWs", 1 / statistic.getClientConnectionStatistics().getAvgServiceTimeMu());
        velocityContext.put("processManagerWs", 1 / statistic.getProcessManagerStatistics().getAvgServiceTimeMu());
        velocityContext.put("queryProcessingWs", 1 / statistic.getQueryProcessingStatistics().getAvgServiceTimeMu());
        velocityContext.put("transactionAndDataWs", 1 / statistic.getTransactionAndDataStatistics().getAvgServiceTimeMu());
        velocityContext.put("executionWs", 1 / statistic.getExecutionStatistics().getAvgServiceTimeMu());
        velocityContext.put("lastModuleWs", 1 / statistic.getClientConnectionStatisticsWithASolvedQueryStatistics().getAvgServiceTimeMu());

        StringWriter stringWriter = new StringWriter();
        template.merge(velocityContext, stringWriter);

        try {
            FileWriter fw = new FileWriter("statistics/simulation" + statisticNumber + ".html");
            fw.write(stringWriter.toString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}