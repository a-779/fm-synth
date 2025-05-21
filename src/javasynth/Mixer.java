package javasynth;

import static javasynth.Synth.SAMPLE_RATE;
import static javasynth.Synth.voices;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author a
 */
public class Mixer extends Thread {
    private boolean end;

    AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false); // fix magoc variables
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
    SourceDataLine line;

    public Mixer() throws LineUnavailableException {            
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();
    }

    @Override
    public void run() {
        final int BUFFER_SIZE = 512;

        byte buffer[] = new byte[BUFFER_SIZE];

        while (!end) {
            for (int i = 0; i < BUFFER_SIZE/2; i++) {                          
                double mixed = 0;

                if (!voices.isEmpty()) {
                    for (Voice v : voices) {
                        if (v.dead) {
                            voices.remove(v);
                        }

                        mixed += v.out();
                    }
                }

                // find out issue with amplitude

                mixed /= voices.size(); 

                mixed *= 10000; // fix magic number

                short sample = (short) mixed;

                buffer[2*i] = (byte) (sample & 0xff);
                buffer[2*i+1] = (byte) ((sample >> 8) & 0xff);

            }

            line.write(buffer, 0, BUFFER_SIZE);
        }
    }

    public void end() {
        end = true;
    }

    @Override
    public void start() {
        end = false;
        super.start();
    }
}
