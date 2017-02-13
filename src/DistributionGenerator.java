import java.lang.*;
import java.util.Random;

/**
 * Created by jesus on 03/02/2017.
 */

public class DistributionGenerator {

    /**
     * @return
     */
    public static QueryType generateType() {
        Random rnd = new Random();
        double randomNumber = rnd.nextDouble();
        QueryType query;

        if (randomNumber < 0.32) {
            query = QueryType.SELECT;
        } else if (randomNumber > 0.31 && randomNumber < 0.60) {
            query = QueryType.UPDATE;
        } else if (randomNumber > 0.59 && randomNumber < 0.93) {
            query = QueryType.JOIN;
        } else {
            query = QueryType.DDL;
        }

        return query;
    }

    /**
     * @param lambda
     * @return
     */
    public static double getNextArrivalTime(double lambda) {
        Random rnd = new Random();
        double aleatoryNumber = rnd.nextDouble();
        return -Math.log(aleatoryNumber) / lambda;
    }


    /**
     * @param a
     * @param b
     * @return
     */
    public static double getNextRandomValueByUniform(double a, double b) {
        Random rnd = new Random();
        double r = rnd.nextDouble();
        return (r * (b - a)) + a;
    }


    /**
     * @param lambda
     * @return
     */
    public static double getNextRandomValueByExponential(double lambda) {
        Random rnd = new Random();
        double r = rnd.nextDouble();
        return -1 / (lambda * Math.log(r));
    }

    public static double getNextRandomValueByNormal(double average, double standardDeviation) {
        double z = 0;
        double x;
        Random rnd = new Random();
        for (int i = 0; i < 12; i++) {
            z += rnd.nextDouble();
        }
        z -= 6;
        x = average + standardDeviation * z;
        return x;
    }

}