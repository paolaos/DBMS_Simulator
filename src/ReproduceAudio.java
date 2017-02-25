/**
 * Created by jesus on 10/02/2017.
 */
import java.io.File;
import java.lang.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class ReproduceAudio {
    public static void successSong(){
        try {
            Clip song = AudioSystem.getClip();
            File a = new  File("audio/Carl_Orff_-_O_Fortuna_-_Carmina_Burana.wav");
            song.open(AudioSystem.getAudioInputStream(a));
            song.start();
            //Thread.sleep(30000); // 10000 milisegundos (10 segundos)
            //song.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

