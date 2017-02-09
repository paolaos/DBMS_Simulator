import java.util.concurrent.LinkedBlockingQueue;

public class ProcessManagerModule extends Module{
    private boolean isBusy;


    public ProcessManagerModule(Simulation simulation, Module nextModule){
        this.simulation = simulation;
        this.nextModule = nextModule;
        queue = new LinkedBlockingQueue<>();
        timeQueue = new LinkedBlockingQueue<>();
        isBusy = false;
        hasBeenInQueue = 0;
    }

    @Override // procesamientode arribo
    public void processArrival(Query query) {
        if(this.isBusy()){
            queue.offer(query);
        }else{
            isBusy = true;
            simulation.addEvent(new Event(simulation.getClock() + DistributionGenerator.getNextRandomValueByNormal(1.5, Math.sqrt(0.1)),
                        query, EventType.EXIT, ModuleType.PROCESS_MANAGER_MODULE));
        }

    }

    @Override //procesamiento de salida
    //por Brayan
    public void processDeparture(Query query) {
        if(queue.size()>0){
            busy=true;
            simulation.addEvent(new Event(simulation.getClock() + DistributionGenerator.getNextRandomValueByNormal(1.5, Math.sqrt(0.1)),
                   queue.poll(), EventType.EXIT, ModuleType.PROCESS_MANAGER_MODULE));
        }else {
            busy= false;
        }

        nextModule.generateServiceEvent(query);
    }


    public boolean isBusy() {
        return isBusy;
    }


    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.PROCESS_MANAGER_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.PROCESS_MANAGER_MODULE));
    }



    @Override
    public double getNextExitTime() {
        return 0;
    }

}
