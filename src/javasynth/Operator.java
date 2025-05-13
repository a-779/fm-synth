package javasynth;

/**
 *
 * @author a
 */
public class Operator {
    public boolean carrier = false;
    
    public int ratio = 0;
    
    public int modulators[];
    public double modulationSens[];
    
    public double feedbackSens;
    public double prevValue;
    
    final int MAX = 1;
    
    public double a;
    public double d;
    public double s;
    public double r;
    
    public double aSlope;
    public double dSlope;
    public double rSlope;
    
    public double volume;

    public Operator(boolean carrier , int ratio, 
        int modulators[], double modulationSens[],double feedbackSens,
        double a, double d,double s, double r, double voulume) {
        this.carrier = carrier;
        this.ratio = ratio;
        this.modulators = modulators;
        this.modulationSens = modulationSens;

        this.feedbackSens = feedbackSens;
        prevValue = 0;
        
        this.a = a;
        this.d = d;
        this.s = s;
        this.r = r;
        
        aSlope = MAX/a;
        dSlope = (s - MAX)/d;
        rSlope = (-s)/r;
        
        this.volume = voulume;
        
    }
}
