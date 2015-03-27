/*
 * Author: Milorad Liviu Felix
 * 14 Jan 2014  22:00 GMT
 */

package engine.Processing.algorithms;


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
/// Code with explanatory comments in ./Desktop/src/engine
public class RawRidgeRecogniser extends Recogniser {
	private RingBuffer buff,lagger,ddlt;
    double runnerAvg = 0,theAvg=0;
    double maxDrop = 65535; int maxDropPos=0, startTrack=0;

    public RawRidgeRecogniser(Counter c){
		super(c);
	}
	public synchronized void setModel(String name){
		super.setModel(name);
		buff = new RingBuffer(rawModel.length);
		for( int i = 1 ; i < rawModel.length; ++i )
			buff.push(0);
	}
	
	private void _processNext(double a){
		buff.push(a);
		position++;
		double certain = 0;
		//if buffer has reached proper size for comparison then perform fft
		if( buff.length() == buff.getCapacity() )
		{
			double accumulator = 0, max = 0,lhs,rhs;
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
			runnerAvg += accumulator;
			theAvg = (runnerAvg/position);
			if( accumulator < theAvg ){
				if(startTrack == 0)
					startTrack = (int) position;

				if(accumulator < maxDrop ){
					maxDrop = accumulator;
					maxDropPos = (int) position;
				}
			}
			else
			{
				if( startTrack != 0 && maxDrop <= theAvg * 0.75){ //only consider counting if the drop was low enough
					//calculate how fast the maximum was reached
					int dist =( buff.getCapacity() - ( maxDropPos - startTrack )); 
					if(dist < 0)
						dist = 1;
					certain = ((double)dist / buff.getCapacity())*1.5;
					//if(certain > 0.5)
						System.out.println("crt:"+certain+" maxDrop:"+maxDrop+" avg:"+theAvg+" dist:"+( maxDropPos - startTrack )+" max:"+buff.getCapacity()+" time:"+((double)position/44100));
				}
				//evaluate
				maxDrop = theAvg;
				maxDropPos = (int) position;
				startTrack = 0;
			}

			if(certain > 0.5)
			{
				counter.increment(certain);
				System.out.println(certain+" Count:"+counter.getCount()+" time:"+((double)position/44100));
			}
		}	
	}
	
	@Override
	public void process(Data data) {
		double[] d = data.get();
		for( int i = 0; i < d.length; ++i)
			_processNext(d[i]);
		if( data.getLength() == 0 )
			System.out.println("End of data!");
	}
}
