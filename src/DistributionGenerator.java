import java.util.Random;

/**
 * Created by jesus on 03/02/2017.
 */

public class DistributionGenerator {

    public enum Type{
        SELECT, UPDATE, JOIN, DDL
    }

    private Random rnd;


        public DistributionGenerator(){
            rnd = new Random();

        }


    /**
     *
     * @return
     */
    public Type  generetaType(){

            float randomNumber= rnd.nextFloat();
            Type query;

            if(randomNumber<0.32){
                query= Type.SELECT;
            }else if(randomNumber>0.31 && randomNumber <0.60){
                query = Type.UPDATE;
            }else if (randomNumber > 0.59 && randomNumber <0.93 ){
                query = Type.JOIN;
            }else{
                query =Type.DDL;
            }

            return  query;
        }

    /**
     *
     * @param lambda
     * @return
     */
    public float getNextArrival(float lambda){
            float aleatoryNumber = this.rnd.nextFloat();
            return  (float) -Math.log(aleatoryNumber)/lambda;
         }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public float getNextRandomValueByuniform(float a, float b){
             float r = this.rnd.nextFloat();
             return (float)  (r*(b-a))+ a;
         }


    /**
     *
     * @param lambda
     * @return
     */
    public  float getNextRandomValueByExponential( float lambda ){
             float r = this.rnd.nextFloat();

             return  (float) (-1/(lambda*Math.log(r)));

         }

         public float getNextRandomValueByNormal( float average, float estandarDeviation){
            float z=0;
            float x;
            for (int i =0; i <12; i++){
                z+=this.rnd.nextFloat();
            }
            z = z -6;
            x = average + estandarDeviation* z;
            return  x;
         }



         public float timeInQueryProcessingModule(Type query){
             float totalTime=0;
             float lexicalValidationTime ;
             float syntacticalValidationTime;
             float semanticValidationTime;
             float permitVerificationTime;
             float queryOptimizationTime;
             float aleatoryNumber=this.rnd.nextFloat();

             if(aleatoryNumber <0.7){
                 lexicalValidationTime =(float)0.1;
             }else {
                 lexicalValidationTime=(float) 0.4;
             }

              syntacticalValidationTime = this.getNextRandomValueByuniform(0,(float) 0.8);
              semanticValidationTime    = this.getNextRandomValueByNormal(1, (float) 0.5 );
              permitVerificationTime    = this.getNextRandomValueByExponential((float) (1/0.7));

              if(query.equals(Type.SELECT)|| query.equals(Type.JOIN)){
                  queryOptimizationTime =(float)0.1;
              }else{
                  queryOptimizationTime =(float) 0.5;
              }

             //System.out.println("lexicalValidationTime "+ lexicalValidationTime);
             //System.out.println("syntacticalValidationTime "+  syntacticalValidationTime);
             //System.out.println("semanticValidationTime "+ semanticValidationTime);
             //System.out.println("permitVerificationTime "+ permitVerificationTime);
             //System.out.println("queryOptimizationTime "+ queryOptimizationTime+"\n");
             totalTime=lexicalValidationTime + syntacticalValidationTime + semanticValidationTime + permitVerificationTime +queryOptimizationTime;
             return totalTime;
         }




}
