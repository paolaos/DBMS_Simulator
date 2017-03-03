/**
 * Created by jesus on 10/02/2017.
 */

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class ReproduceAudio {
    public static void successSong() {
        try {
            Clip song = AudioSystem.getClip();
            File a = new File("audio/Carl_Orff_-_O_Fortuna_-_Carmina_Burana.wav");
            song.open(AudioSystem.getAudioInputStream(a));
            song.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

