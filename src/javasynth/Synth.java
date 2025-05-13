package javasynth;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import java.util.ArrayList;
import javax.sound.sampled.*;

/**
 *
 * @author a
 */
public class Synth {
    final float SAMPLE_RATE = 44100f;

    Operator algorithm[];
    
    int carriers[];
    
    ArrayList<Voice> voices = new ArrayList<>();

    private Mixer mixer =  new Mixer();
    
    Patch patch = new Patch();
    private int currentPatch;
    
    public Synth() throws LineUnavailableException, InterruptedException {        
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
    
    public void play(int freq) {
        voices.add(new Voice(freq));
    }
    
    public void noteOff(int freq)  {
        for (Voice v : voices) {
            if (v.freq  == freq) {
                v.gate = false;
                
                v.gateT = v.t;
            }
        }
    }
    
    public void stop() {
        mixer.end();
    }
    
    public class Voice {
        public int freq;
        public boolean gate;
        public double t;
        public double gateT;
        public boolean dead;

        public Voice(int freq) {
            this.freq = freq;
            gate = true;
            t = 0;
            gateT = 0;
            dead = false;
        }
        
        private double out() {
            double sum = 0;                   
            
            for (int i = 0; i < carriers.length; i++) {
                sum += value(algorithm[i]);
            }
            
            //System.out.println(sum + " at " +  t);
            
            t += 1/SAMPLE_RATE;
            
            return sum;
        }
        
        private double value(Operator operator) {
            // add feedback
 
            double volume = operator.volume;
            double amp = envelope(operator); // make 1
            
            double x = 2*PI*operator.ratio*freq*t;
            
            double feedback = operator.feedbackSens*operator.prevValue;
            
            if (operator.modulators.length == 0) {
                double value = volume*amp*sin(x + feedback);
                
                operator.prevValue = value;
                
                return value;
            } else {     
                double sum = 0;
                
                for (int i = 0; i < operator.modulators.length; i++) {
                    sum += operator.modulationSens[i]*
                            value(algorithm[operator.modulators[i]]);
                }
                
                double value = volume*amp*sin(x + sum + feedback);
                
                operator.prevValue =  value;
                
                return value;
            }
        }
        
        private double envelope(Operator operator) {
            // has to between 1 and 0
            
            if (t < operator.a)  {
                return operator.aSlope*t;
            } else if (t < (operator.a + operator.d))  {
                return operator.dSlope*(t- operator.a) + operator.MAX;
            } else if (gate) {
                return operator.s;
            }  else if ((t - gateT) < operator.r) {
                return operator.rSlope*(t - gateT) + operator.s;
            } else {
                // this might not be nessecary
                
                return 0;
            }
        }
    }
    
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
                
                    for (Voice v : voices) {
                        if (v.dead) {
                            voices.remove(v);
                        }

                        mixed += v.out();
                    }

                    // find out issue with amplitude
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
}
