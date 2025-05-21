package javasynth;

import java.util.ArrayList;
import javax.sound.sampled.*;

/**
 *
 * @author a
 */
public class Synth {
    public static final float SAMPLE_RATE = 44100f;

    public static Operator algorithm[];
    
    public static int carriers[];
    
    public static ArrayList<Voice> voices;

    private final Mixer mixer;
    
    private Patch patch;
    
    public Synth() throws LineUnavailableException, InterruptedException {    
        voices = new ArrayList<>();
        mixer = new Mixer();
        patch = new Patch();
        
        initPatch();
    }
        
    public void initPatch() {
        mixer.end();
        
        patch.currentPatch = 0;
        
        algorithm = patch.get();
        
        getCarriers();
        
        mixer.start();
    }

    public void nextPatch() {
        mixer.end();
        
        patch.currentPatch++;
        
        algorithm = patch.get();
        
        getCarriers();
        
        mixer.start();
    }
    
    public void lastPatch() {
        mixer.end();
        
        patch.currentPatch--;
        
        algorithm = patch.get();
        
        getCarriers();
        
        mixer.start();
    }
    
    private void getCarriers()  {
        ArrayList<Integer> carriers = new ArrayList<>();
        
        for (int i = 0; i < algorithm.length; i++) {
            if (algorithm[i].carrier) {
                carriers.add(i);
            }
        }
        
        this.carriers = new int[carriers.size()];
        
        for (int i = 0; i < carriers.size(); i++) {
            this.carriers[i] = carriers.get(i);
        }
    }
    
    public static void play(int freq) {
        voices.add(new Voice(freq));
    }
    
    public static void noteOff(int freq)  {
        for (Voice v : voices) {
            if (v.freq  == freq) {
                v.gate = false;
                
                v.gateT = v.t;
            }
        }
    }
}
