public class Bla {


    public static void main(String... args){
        DistributionGenerator DG = new DistributionGenerator();

        int x= Math.round((float) 0.1);
        int y = (int) 0.1;
        for(int i=0; i<20; i++) {
           // System.out.println(x + "  " + y + "   " + DG.getNextRandomValueByuniform(1, 64));
            DistributionGenerator.Type p= DistributionGenerator.Type.DDL;
            System.out.println( DG.timeInQueryProcessingModule(p));
        }

    }
}
