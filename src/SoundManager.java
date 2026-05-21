import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {

    public static void playSound(String fileName) {
        // Запускаем звук в отдельном потоке, чтобы игра не фризила в момент воспроизведения
        new Thread(() -> {
            try {
                File soundFile = new File("resources/" + fileName);
                if (!soundFile.exists()) {
                    System.err.println("Sounddatei nicht gefunden: " + fileName);
                    return;
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();

            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }
}