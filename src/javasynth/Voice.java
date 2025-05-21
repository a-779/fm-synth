package javasynth;

import static java.lang.Math.PI;
import static java.lang.Math.sin;

import static javasynth.Synth.algorithm;
import static javasynth.Synth.carriers;
import static javasynth.Synth.SAMPLE_RATE;

/**
 *
 * @author a
 */
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

    public double out() {
        double sum = 0;                   

        for (int i = 0; i < carriers.length; i++) {
            sum += value(algorithm[i]);
        }

        t += 1/SAMPLE_RATE;

        return sum;
    }

    private double value(Operator operator) {
        double volume = operator.volume;
        double amp = envelope(operator);

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
        if (t < operator.a)  {
            return operator.aSlope*t;
        } else if (t < (operator.a + operator.d))  {
            return operator.dSlope*(t- operator.a) + operator.MAX;
        } else if (gate) {
            return operator.s;
        }  else if ((t - gateT) < operator.r) {
            return operator.rSlope*(t - gateT) + operator.s;
        } else {
            dead = true;
            return 0;
        }
    }
}