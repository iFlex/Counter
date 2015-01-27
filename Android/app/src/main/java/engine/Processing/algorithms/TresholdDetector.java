package engine.Processing.algorithms;
import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.util.*;

public class TresholdDetector implements Recogniser {
	public double treshold = 100;
	private double min = 0;
	private double max = 0;
	private Counter counter;
    public synchronized void setModel(String name){
    }
	public TresholdDetector(Counter c){
		counter = c;
	}
	public void process(Data d){
		max = 0;
		min = 0;
		double[] D = d.get();
		for( int i=0; i < D.length; ++i )
		{
			if(D[i] > 0 && D[i] > treshold)
				counter.increment(1);
			if(min > D[i])
				min = D[i];
			if(max < D[i])
				max = D[i];
		}
		//System.out.println(max+" , "+min);
	}
}
