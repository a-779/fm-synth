package javasynth;

import javax.sound.sampled.LineUnavailableException;


/**
 *
 * @author a
 */
public class JavaSynth {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Synth synth = new Synth();
//        synth.play(440); // A4
//        synth.play(523); // C5
//        synth.play(659); // E5
//
//        Thread.sleep(5000);
//
//        
//        synth.noteOff(440);
//        synth.noteOff(523);
//        synth.noteOff(659);
        } catch (LineUnavailableException ex) {
            System.getLogger(JavaSynth.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (InterruptedException ex) {
            System.getLogger(JavaSynth.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
    }
    
}
