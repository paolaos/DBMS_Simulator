import java.util.Random;

/**
 * Created by jesus on 03/02/2017.
 */

public class DistributionGenerator {

    private final float DDL_RESTRUCTRATION_TIME = (float) 0.5;
    private final float UPDATE_RESTRUCTURATION_TIME = 1;
    private Random rnd;


    /**
     * @return
     */
    public static QueryType generateType() {
        Random rnd = new Random();
        float randomNumber = rnd.nextFloat();
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
    public static float getNextArrivalTime(float lambda) {
        Random rnd = new Random();
        float aleatoryNumber = rnd.nextFloat();
        return (float) -Math.log(aleatoryNumber) / lambda;
    }

    /**
     * @param a
     * @param b
     * @return
     */
    public float getNextRandomValueByUniform(float a, float b) {
        float r = this.rnd.nextFloat();
        return (float) (r * (b - a)) + a;
    }


    /**
     * @param lambda
     * @return
     */
    public float getNextRandomValueByExponential(float lambda) {
        float r = this.rnd.nextFloat();

        return (float) (-1 / (lambda * Math.log(r)));

    }

    public static float getNextRandomValueByNormal(float average, float estandarDeviation) {
        float z = 0;
        float x;
        Random rnd = new Random();

        for (int i = 0; i < 12; i++) {
            z += rnd.nextFloat();
        }
        z = z - 6;
        x = average + estandarDeviation * z;
        return x;
    }


    public float timeInQueryProcessingModule(QueryType query) {
        float totalTime = 0;
        float lexicalValidationTime;
        float syntacticalValidationTime;
        float semanticValidationTime;
        float permitVerificationTime;
        float queryOptimizationTime;
        float aleatoryNumber = this.rnd.nextFloat();

        if (aleatoryNumber < 0.7) {
            lexicalValidationTime = (float) 0.1;
        } else {
            lexicalValidationTime = (float) 0.4;
        }
        syntacticalValidationTime = this.getNextRandomValueByUniform(0, (float) 0.8);
        semanticValidationTime = this.getNextRandomValueByNormal(1, (float) 0.5);
        permitVerificationTime = this.getNextRandomValueByExponential((float) (1 / 0.7));

        if (query.equals(QueryType.SELECT) || query.equals(QueryType.JOIN)) {
            queryOptimizationTime = (float) 0.1;
        } else {
            queryOptimizationTime = (float) 0.5;
        }
        totalTime = lexicalValidationTime + syntacticalValidationTime + semanticValidationTime + permitVerificationTime + queryOptimizationTime;
        return totalTime;
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
                int x = (int) Math.nextUp(getNextRandomValueByUniform((float) 1, (float) 16));
                int y = (int) Math.nextUp(getNextRandomValueByUniform((float) 1, (float) 12));

                numberOfBlocks = x + y;
                break;

            case SELECT:
                numberOfBlocks = (int) Math.nextUp(getNextRandomValueByUniform((float) 1, (float) 64));
                break;
        }
        return numberOfBlocks;
    }


    public float getLoadingTime(int numberOfBlocks) {
        return numberOfBlocks * (float) 0.1;
    }


    public float getBlockExecutingTime(int numberOfBlocks) {
        return (float) Math.pow(numberOfBlocks, 2) / 1000;

    }

    public float getRestructurationTime(QueryType query) {
        float time;
        if (query == QueryType.DDL) {
            time = DDL_RESTRUCTRATION_TIME;
        } else {
            time = UPDATE_RESTRUCTURATION_TIME;
        }
        return time;
    }

    public float getResultantTime(int numberOfBlocks) {
        float average = (float) numberOfBlocks / 3;
        return average + numberOfBlocks / 2;
    }


}
