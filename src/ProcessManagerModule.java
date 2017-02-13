import java.lang.*;
import java.lang.System;
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

        //System.out.println("\n \n Llegada a modulo 2 \n \n ");
        if(this.isBusy()){
            queue.offer(query);
           // System.out.print(" Y encoló");


        }else{
            busy = true;
            double time = DistributionGenerator.getNextRandomValueByNormal(1.5, Math.sqrt(0.1));
          //  System.out.println("Tiempo de siguiente salida "+ (time + simulation.getClock()) );

            simulation.addEvent(new Event(simulation.getClock() + time,
                        query, EventType.EXIT, ModuleType.PROCESS_MANAGER_MODULE));
        }

    }

    @Override //procesamiento de salida
    //por Brayan
    public void processDeparture(Query query) {
        //System.out.println("\n \n Salidad de modulo 2 \n \n ");
        if(queue.size() > 0){
            busy=true;
            // 0.316227766 sqrt of 0.1
            double time =   DistributionGenerator.getNextRandomValueByNormal(1.5, 0.316227766);
           simulation.addEvent(new Event(simulation.getClock() + time ,
                   queue.poll(), EventType.EXIT, ModuleType.PROCESS_MANAGER_MODULE));
        }else {
            busy= false;
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
    }



    @Override
    public double getNextExitTime() {
        return 0;
    }

    public  static  void  main(String []args){



    }
}
