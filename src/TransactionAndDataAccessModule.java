import java.util.PriorityQueue;

public class TransactionAndDataAccessModule extends Module {
    private int pQueries;
    private int currentQueries;
    private boolean blocked;
    private Query pendingQuery;


    public TransactionAndDataAccessModule(Simulation simulation, Module nextModule, int pQueries) {
        this.simulation = simulation;
        this.nextModule = nextModule;
        queue = new PriorityQueue<>();
        this.pQueries = pQueries;
        currentQueries = 0;
        pendingQuery = null;
        blocked = false; //booleano para caso DDL
    }

    @Override
    public void processArrival(Query query) {
        //si está ocupado o bloqueado
        if (isBusy() || blocked) {
            //encole
            queue.offer(query);
        } else {
            //en caso de que pueda atender

            //Si la consulta es DDL
            if (query.getQueryType() == QueryType.DDL) {
                //Bloquee el Sistema y almacene cual es la consulta por hacer
                blocked = true;
                pendingQuery = query;
                if (currentQueries == 0) {


                    // si la consulta es DDL pero no hay queries
                    currentQueries++;
                    // Agregar el tiempo Respectivo que se debe sumar al clock
                    simulation.addEvent(new Event(simulation.getClock(),
                            pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));

                }


            } else {
                // si la consulta no es DDL => atienda


                currentQueries++;
                // Agregar el tiempo Respectivo que se debe sumar al clock
                simulation.addEvent(new Event(simulation.getClock(),
                        query, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
            }
        }
    }

    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
    }

    @Override
    //Si el que sale  es DDL y el que sigue no es DDL, entonces desbloquear,
    public void processDeparture(Query query) {
        if (query.getQueryType() == QueryType.DDL) {
            blocked = false;
        }

        if (queue.size() > 0) {
            if (!blocked) {
                //agregar tiempo que suma al clock
                //que se pueda, que hayan y que el siguiente no sea DDL
                while (currentQueries < pQueries && queue.size() > 0 && queue.peek().getQueryType() != QueryType.DDL) {
                    simulation.addEvent(new Event(simulation.getClock(),
                            queue.poll(), EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                    currentQueries++;
                }

                if (queue.peek().getQueryType() == QueryType.DDL) {
                    blocked = true;
                }
            }

        } else {
            currentQueries--;
            if (currentQueries == 0 && blocked) {
                //Ejecuta consulta pendinte
                simulation.addEvent(new Event(simulation.getClock(),
                        pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                pendingQuery = null;
            }
        }
        nextModule.generateServiceEvent(query);
    }

    @Override
    public void processKill(Query query) {

                if (queue.peek().getQueryType() == QueryType.DDL) {
                    blocked = true;
                }
            }

        } else {
            currentQueries--;
            if (currentQueries == 0 && blocked) {
                //Ejecuta consulta pendinte
                simulation.addEvent(new Event(simulation.getClock(),
                        pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                pendingQuery = null;
            }
        }
        nextModule.generateServiceEvent(query);
    }

    @Override
    public boolean isBusy() {
        return pQueries == currentQueries;
    }

    @Override
    public double getNextExitTime() {
        return 0;
    }

    //coordinacion
    private double getExecutionCoordinationTime() {
        return pQueries * 0.03;
    }

    private int getBlockNumber(QueryType query) {
        int numberOfBlocks = 0;
        switch (query) {
            case DDL:
                numberOfBlocks = 0;
                break;

            case UPDATE:
                numberOfBlocks = 0;
                break;

            case JOIN:
                int x = (int) Math.nextUp(DistributionGenerator.getNextRandomValueByUniform((double) 1, (double) 16));
                int y = (int) Math.nextUp(DistributionGenerator.getNextRandomValueByUniform((double) 1, (double) 12));
                numberOfBlocks = x + y;
                break;

            case SELECT:
                numberOfBlocks = (int) Math.nextUp(DistributionGenerator.getNextRandomValueByUniform((double) 1, (double) 64));
                break;
        }
        return numberOfBlocks;
    }


    public double getExecutionCoordinationTime() {
        return pQueries * 0.03;
    }

    public double getBlockLoadingTime(int numberOfBlocks) {
        return numberOfBlocks * 0.1;
    }

    public double getTotalTime(Query query){
        int blockNumber = getBlockNumber(query.getQueryType());
        query.setNumberOfBlocks(blockNumber);
        double totalTime= getExecutionCoordinationTime()+getBlockLoadingTime(blockNumber);
        return  totalTime;
    
    }
}