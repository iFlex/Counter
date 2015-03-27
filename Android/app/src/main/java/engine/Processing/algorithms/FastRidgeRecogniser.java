/*
 * Author: Milorad Liviu Felix
 * 14 Jan 2014  22:00 GMT
 */
package engine.Processing.algorithms;
// Code with explanatory comments in ./Desktop/src/engine
import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.audio.*;
import engine.util.*;

import org.jtransforms.fft.DoubleFFT_1D;

import java.lang.Math.*;
import java.io.FileNotFoundException;

//debug
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
public class FastRidgeRecogniser extends Recogniser {

	private RingBuffer buff;
	private double skipRate = 0.05;
    double runnerAvg = 0,theAvg=0,accumulator=0;
    double maxDrop = 65535,minDiff = 0; int maxDropPos=0, startTrack=0;

    private int toSkip = 0,lastCount = 0;

    public FastRidgeRecogniser(Counter c){
		super(c);
	}

	public synchronized void setModel(String name){
		super.setModel(name);
		buff = new RingBuffer(rawModel.length);
		for( int i = 1 ; i < rawModel.length; ++i )
    		buff.push(0);
		toSkip = (int)(rawModel.length*skipRate);
		lastCount = 0;
		minDiff = 65535;
	}

	private void _processNext(double a){
		buff.push(a);
		position++;
		
		double certain = 0;
		if( buff.length() == buff.getCapacity() )
		{
			double max = 0,lhs,rhs;
			accumulator = 0;
			int i;
			for( i = 0; i < rawModel.length ; i++ ){
				lhs =  Math.abs(buff.get(i));//*buff.b[i];
				rhs =  Math.abs(rawModel[i]);//*rawModel[iter];
				lhs =  Math.abs( lhs - rhs ); //accidental mistake yelded interesting result -= in stead of =
				accumulator += lhs;
				
				if( max < lhs )
					max = lhs;
			}
			accumulator /= i;
			accumulator /= max;
			if( minDiff > accumulator )
				minDiff = accumulator;
			
			runnerAvg += accumulator;
			theAvg = (runnerAvg/position);
			if( accumulator < theAvg ){
				if(startTrack == 0)
					startTrack = (int) position;
				//track
				if(accumulator < maxDrop ){
					maxDrop = accumulator;
					maxDropPos = (int) position;
				}
			}
			else
			{
				double lim = (minDiff + (theAvg-minDiff)*0.2);//20% above min diff
				if( startTrack != 0 && (maxDrop <= lim && (position - startTrack > 50))){ //only consider counting if the drop was low enough
					int len = (int)(position - startTrack);
					int dist = ( len - ( maxDropPos - startTrack ));
					if(dist < 0)
						dist = 1;
					certain = ((double)dist / len)*1.0;
					System.out.println("crt:"+certain+" maxDrop:"+maxDrop+" avg:"+theAvg+" dist:"+( maxDropPos - startTrack )+" max:"+buff.getCapacity()+" time:"+((double)position/44100));
				}

                maxDrop = theAvg;
				maxDropPos = 0;
				startTrack = 0;
			}
			
			if(certain > 0.5)
			{
				counter.increment(certain);
				System.out.println(certain+" Count:"+counter.getCount()+" time:"+((double)position/44100));
			}

			//skipper
			for(int idx = 0; idx < toSkip; ++idx){
				try {
					buff.pop();
				} catch (Exception e) {
					break;
				}
			}
		}
		else{
			runnerAvg += accumulator;
			theAvg = (runnerAvg/position);
		}
	}
	
	@Override
	public void process(Data data) {
        if( data != null ) {
            double[] d = data.get();
            for (int i = 0; i < d.length; ++i)
                _processNext(d[i]);

            if (data.getLength() == 0)
                System.out.println("End of data!");
        }
        else
            System.out.println("Recogniser got null object");
	}
}
