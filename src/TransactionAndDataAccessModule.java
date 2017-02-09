import java.util.PriorityQueue;

public class TransactionAndDataAccessModule extends Module{
    private final double DDL_RESTRUCTRATION_TIME = (double) 0.5;
    private final double UPDATE_RESTRUCTURATION_TIME = 1;
    private int pQueries;
    private int currentQueries;
    private boolean blocked;
    private Query pendingQuery;


    public TransactionAndDataAccessModule(Simulation simulation, Module nextModule, int pQueries){
        this.simulation = simulation;
        this.nextModule = nextModule;
        queue = new PriorityQueue<>();
        this.pQueries = pQueries;
        currentQueries = 0;
        pendingQuery =null;
        blocked =false; //booleano para caso DDL
    }
    
    @Override
    public void processArrival(Query query) {
        //si está ocupado o cloqueado
        if (isBusy() || blocked) {
            //encole
            queue.offer(query);
        } else {
            //en caso de que pueda atender

            //Si la consulta es DDL
            if(query.getQueryType()== QueryType.DDL) {
                //Bloquee el Sistema y almacene cual es la consulta por hacer
                blocked =true;
                pendingQuery=query;

            }else {
                // si la consulta no es DDL Atienda

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
        if(query.getQueryType() == QueryType.DDL ){
            blocked =false;
        }

        if(queue.size()>0 && !blocked ){
            //agregar tiempo que suma al clock
            if( queue.peek().getQueryType() == QueryType.DDL){
                blocked=true;
            }
            simulation.addEvent(new Event(simulation.getClock() ,
                    queue.poll(), EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
        }else {
            currentQueries--;
            if (currentQueries ==0){
                //Ejecuta consulta pendinte
                simulation.addEvent(new Event(simulation.getClock() ,
                        pendingQuery, EventType.EXIT, ModuleType.TRANSACTION_AND_DATA_ACCESS_MODULE));
                pendingQuery=null;
            }
        }
        nextModule.generateServiceEvent(query);
    }

    @Override
    public boolean isBusy() {
        return pQueries==currentQueries;
    }

    @Override
    public double getNextExitTime() {
        return 0;
    }

    public int getBlockNumber(QueryType query) {
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

    public double getBlockLoadingTime(int numberOfBlocks) {
        return numberOfBlocks * (double) 0.1;
    }

    public double getRestructurationTime(QueryType query) {
        double time;
        if (query == QueryType.DDL) {
            time = DDL_RESTRUCTRATION_TIME;
        } else {
            time = UPDATE_RESTRUCTURATION_TIME;
        }
        return time;
    }
}
