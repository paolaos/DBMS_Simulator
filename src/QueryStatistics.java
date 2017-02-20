/**
 * Created by Paola Ortega S on 2/8/2017.
 */
public class QueryStatistics {
    private QueryModuleStatistics clientConnectionStatisticsWithoutResolvedQuery;
    private QueryModuleStatistics processManagerStatistics;
    private QueryModuleStatistics queryProcessingStatistics;
    private QueryModuleStatistics transactionAndDataAccessStatistics;
    private QueryModuleStatistics executionStatistics;
    private QueryModuleStatistics clientConnectionStatisticsWithResolvedQuery;

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


    public class QueryModuleStatistics {
        private double timeOfEntryToModule=0;
        private double timeOfEntryToQueue=0;
        private double timeOfEntryToServer=0;
        //TODO no hay exit from server porque es lo mismo que exit from Module, en este caso en específico.
        private double timeOfExitFromQueue=0;
        private double timeOfExitFromModule=0;

        public double getTimeOfEntryToModule() {
            return timeOfEntryToModule;
        }

        public void setTimeOfEntryToModule(double timeOfEntryToModule) {
            this.timeOfEntryToModule = timeOfEntryToModule;
        }

        public double getTimeOfExitFromQueue() {
            return timeOfExitFromQueue;
        }

        public void setTimeOfExitFromQueue(double timeOfExitFromQueue) {
            this.timeOfExitFromQueue = timeOfExitFromQueue;
        }

        public double getTimeOfEntryToQueue() {
            return timeOfEntryToQueue;
        }

        public void setTimeOfEntryToQueue(double timeOfEntryToQueue) {
            this.timeOfEntryToQueue = timeOfEntryToQueue;
        }

        public double getTimeOfEntryToServer() {
            return timeOfEntryToServer;
        }

        public void setTimeOfEntryToServer(double timeOfEntryToServer) {
            this.timeOfEntryToServer = timeOfEntryToServer;
        }

        public double getTimeOfExitFromModule() {
            return timeOfExitFromModule;
        }

        public void setTimeOfExitFromModule(double timeOfExitFromModule) {
            this.timeOfExitFromModule = timeOfExitFromModule;
        }

    }


}
