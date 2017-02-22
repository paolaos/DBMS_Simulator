/**
 * Created by jesus on 10/02/2017.
 */
import java.io.File;
import java.lang.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Ejemplo de reproducción de ficheros de sonido.
 * @author chuidiang
 * http://www.chuidiang.com
 */
public class ReproduceAudio {
    public static  void cancionDelExito(){
        try {
            Clip sonido = AudioSystem.getClip();

            File a = new  File("C:\\Users\\jesus\\Music\\Queen_-_We_Are_The_Champions_Official_Video_.wav");
            sonido.open(AudioSystem.getAudioInputStream(a));
            sonido.start();

            Thread.sleep(15000); // 10000 milisegundos (10 segundos)
            sonido.close();
        }
        catch (Exception tipoerror) {
            java.lang.System.out.print("" + tipoerror);
        }
    }
    public static void main(String[] args) {
        /*
        try {

            // Se obtiene un Clip de sonido
            Clip sonido = AudioSystem.getClip();

            // Se carga con un fichero wav
            sonido.open(AudioSystem.getAudioInputStream(new File("C:\\Users\\jesus\\Music\\alegre\\edy herrera - devorame otra vez.wav")));

            // Comienza la reproducción
            sonido.start();

            // Espera mientras se esté reproduciendo.
            while (sonido.isRunning())
                Thread.sleep(1000);

            // Se cierra el clip.
            sonido.close();
        } catch (Exception e) {
            java.lang.System.out.println("" + e);
        }
            */
    }

}
