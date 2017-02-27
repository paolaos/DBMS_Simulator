import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by jesus on 06/02/2017.
 */
public class Main {

    public  static  void  main (String[]args){

        TextArea data = new TextArea();

        for(int i =0; i< 10; i++){

            data.setText(Integer.toString(i));
        data.update(data.getGraphics());
        data.setCaretPosition(data.getText().length());
        data.isVisible();


            try{
                Thread.sleep(1000);
            }catch(Exception e){
                e.printStackTrace();
            }
           java.lang.System.out.println(i);
        }
    }


}
