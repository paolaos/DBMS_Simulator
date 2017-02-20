import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by Ian on 18/2/2017.
 */
public class Writer {
    public static void writeIndex(int numberOfSimulations, double maxTimePerSimulation, int kConnections,
                                  int nAvailableProcesses, int pAvailableProcesses, int mAvailableProcesses, double timeout){

        VelocityEngine ve = new VelocityEngine();
        ve.init();

        Template t = ve.getTemplate("templates/IndexTemplate.html");
        VelocityContext vc = new VelocityContext();

        vc.put("numberOfSimulations", "" + numberOfSimulations);
        vc.put("maxTimePerSimulation", "" + maxTimePerSimulation);
        vc.put("kConnections", "" + kConnections);
        vc.put("nAvailableProcesses", "" + nAvailableProcesses);
        vc.put("pAvailableProcesses", "" + pAvailableProcesses);
        vc.put("mAvailableProcesses", "" + mAvailableProcesses);
        vc.put("timeout", "" + timeout);
        StringWriter sw = new StringWriter();

        t.merge(vc, sw);
        try{
            FileWriter fw = new FileWriter("statistics/index.html");
            fw.write(sw.toString());
            fw.flush();
            fw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String... args){
        Writer.writeIndex(5, 2.345, 3, 6, 1, 5, 3.444);
    }
}
