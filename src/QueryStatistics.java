/**
 * Created by Paola Ortega S on 2/8/2017.
 */
public class QueryStatistics {
    private QueryModuleStatistics clientConnectionStatistics;
    private QueryModuleStatistics processManagerStatistics;
    private QueryModuleStatistics queryProcessingStatistics;
    private QueryModuleStatistics transactionAndDataAccessStatistics;
    private QueryModuleStatistics executionStatistics;


    private class QueryModuleStatistics {
        private double timeOfEntry;
        private double timeOfQueueEntry;
        private double timeOfServiceEntry;
        private double timeOfServiceExit;
        //TODO acordarnos del quinto atributo (preguntarle a Barty)

    }
}
