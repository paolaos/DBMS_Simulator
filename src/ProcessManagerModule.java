import java.util.concurrent.LinkedBlockingQueue;

public class ProcessManagerModule extends Module{
    public ProcessManagerModule(Simulation simulation, Module nextModule){
        this.simulation = simulation;
        this.nextModule = nextModule;
        queue = new LinkedBlockingQueue<>();
        timeQueue = new LinkedBlockingQueue<>();
        busy = false;
        hasBeenInQueue = 0;
    }

    @Override // procesamientode arribo
    public void processArrival(Query query) {
        if(this.isBusy()){
            queue.offer(query);
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToQueue(simulation.getClock());
        }else{
            busy = true;
            double normalValue = DistributionGenerator.getNextRandomValueByNormal(1.5, Math.sqrt(0.1));
            simulation.addEvent(new Event(simulation.getClock() + normalValue,
                        query, EventType.EXIT, ModuleType.PROCESS_MANAGER_MODULE));
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToServer(simulation.getClock()); //TODO revisar esto
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromModule(simulation.getClock() + normalValue);
        }

    }

    @Override //procesamiento de salida
    //por Brayan
    public void processDeparture(Query query) {
        if(queue.size() > 0){
            busy = true;
            // 0.316227766 sqrt of 0.1
            double normalValue = DistributionGenerator.getNextRandomValueByNormal(1.5, Math.sqrt(0.1));
            simulation.addEvent(new Event(simulation.getClock() + normalValue,
                   queue.poll(), EventType.EXIT, ModuleType.PROCESS_MANAGER_MODULE));
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToServer(simulation.getClock()); //TODO revisar esto
            query.getQueryStatistics().getProcessManagerStatistics().setTimeOfExitFromModule(simulation.getClock() + normalValue);
        }else {
            busy = false;
            //TODO ya lo procesé?
        }
        nextModule.generateServiceEvent(query);
    }

    @Override
    public void processKill(Query query) {

    }

    public boolean isBusy() {
        return busy;
    }


    @Override
    public void generateServiceEvent(Query query) {
        query.setCurrentModule(ModuleType.PROCESS_MANAGER_MODULE);
        simulation.addEvent(new Event(simulation.getClock(), query, EventType.ARRIVAL, ModuleType.PROCESS_MANAGER_MODULE));
        query.getQueryStatistics().getProcessManagerStatistics().setTimeOfEntryToModule(simulation.getClock());
    }



    @Override
    public double getNextExitTime() {
        return 0;
    }

}
