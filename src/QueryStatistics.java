/**
 * Created by Paola Ortega S on 2/8/2017.
 */

/**
 * Store All the time information from some query in all modules.
 */
public class QueryStatistics {
    private QueryModuleStatistics clientConnectionStatisticsWithoutResolvedQuery;
    private QueryModuleStatistics processManagerStatistics;
    private QueryModuleStatistics queryProcessingStatistics;
    private QueryModuleStatistics transactionAndDataAccessStatistics;
    private QueryModuleStatistics executionStatistics;
    private QueryModuleStatistics clientConnectionStatisticsWithResolvedQuery;

    /**
     * Constructor of class QueryStatistics
     */
    public QueryStatistics(){
        clientConnectionStatisticsWithoutResolvedQuery = new QueryModuleStatistics();
        processManagerStatistics = new QueryModuleStatistics();
        queryProcessingStatistics = new QueryModuleStatistics();
        transactionAndDataAccessStatistics = new QueryModuleStatistics();
        executionStatistics = new QueryModuleStatistics();
        clientConnectionStatisticsWithResolvedQuery = new QueryModuleStatistics();
    }

    public QueryModuleStatistics getClientConnectionStatisticsWithResolvedQuery() {
        return clientConnectionStatisticsWithResolvedQuery;
    }

    public QueryModuleStatistics getClientConnectionStatisticsWithoutResolvedQuery() {
        return clientConnectionStatisticsWithoutResolvedQuery;
    }

    public QueryModuleStatistics getProcessManagerStatistics() {
        return processManagerStatistics;
    }


    public QueryModuleStatistics getQueryProcessingStatistics() {
        return queryProcessingStatistics;
    }


    public QueryModuleStatistics getTransactionAndDataAccessStatistics() {
        return transactionAndDataAccessStatistics;
    }


    public QueryModuleStatistics getExecutionStatistics() {
        return executionStatistics;
    }

}
