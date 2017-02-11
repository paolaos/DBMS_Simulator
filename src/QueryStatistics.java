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
        private double timeOfEntryToModule;
        private double timeOfEntryToQueue;
        private double timeOfEntryToServer;
        //TODO no hay exit from server porque es lo mismo que exit from Module, en este caso en específico.
        private double timeOfExitFromModule;

        public double getTimeOfEntryToModule() {
            return timeOfEntryToModule;
        }

        public void setTimeOfEntryToModule(double timeOfEntryToModule) {
            this.timeOfEntryToModule = timeOfEntryToModule;
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
