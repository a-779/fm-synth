package javasynth;

import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author a
 */
public class JavaSynth {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, LineUnavailableException  {
        Synth synth = new Synth();
        
        synth.play(440); // A4
        
        //synth.savePatch(true);
        
        Thread.sleep(5000);
        
        
        synth.noteOff(440);
//        synth.play(523); // C5
//        Thread.sleep(1000);
//        synth.play(659); // E5
    }
    
}
