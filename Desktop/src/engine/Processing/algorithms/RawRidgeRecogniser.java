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
//
public class RawRidgeRecogniser extends Recogniser {
	
	//debug files
	FileOutputStream dbg;
	FileOutputStream rto;
	FileOutputStream sampl;
	FileOutputStream mic;
	FileOutputStream dd;
	FileOutputStream smp;
	//ring buffer for sliding window 
	private RingBuffer buff;
	
	public RawRidgeRecogniser(Counter c){
		super(c);
		System.out.println("RAW ridge recogniser");
		//opent he files to write debug information in
		try {
			dbg   = new FileOutputStream(new File("tests/graphs/lagbehinder.txt"));
			rto   = new FileOutputStream(new File("tests/graphs/accumulator.txt"));
			sampl = new FileOutputStream(new File("tests/graphs/lagbehdelta.txt"));
			mic   = new FileOutputStream(new File("tests/graphs/rawmicinput.txt"));
			dd    = new FileOutputStream(new File("tests/graphs/zerocrosser.txt"));
			smp   = new FileOutputStream(new File("tests/graphs/thesamplesn.txt"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public synchronized void setModel(String name){
		super.setModel(name);
		//create a new ring buffer with the same length as the new model
		buff = new RingBuffer(rawModel.length);
		//write the sample values to
		for( int i = 1 ; i < rawModel.length; ++i )
		{
			buff.push(0);
			try {
				smp.write((rawModel[i-1]+"\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	double runnerAvg = 0,theAvg=0;
	double maxDrop = 65535; int maxDropPos=0, startTrack=0;
	private void _processNext(double a){
		buff.push(a);
		position++;
		double certain = 0;
		//only perform difference if the buffer contains the same amount of data as the model
		if( buff.length() == buff.getCapacity() )
		{
			double accumulator = 0, max = 0,lhs,rhs;
			int i;
			for( i = 0; i < rawModel.length ; i++ ){
				//sum up the differences between each pair of amplitudes
				lhs =  Math.abs(buff.get(i));
				rhs =  Math.abs(rawModel[i]);
				lhs =  Math.abs( lhs - rhs );
				accumulator += lhs;
				//get maximum difference to use for normalisation
				if( max < lhs )
					max = lhs;
			}
			//get the average difference
			accumulator /= i;
			//normalise value to account for volume differences
			accumulator /= max;
			//calculate the average level
			runnerAvg += accumulator;
			theAvg = (runnerAvg/position);
			//if the difference descends under the average level
			if( accumulator < theAvg ){
				//record position where the descent started
				if(startTrack == 0) 
					startTrack = (int) position;
					
				//find local minimum and save its position
				if(accumulator < maxDrop ){
					maxDrop = accumulator;
					maxDropPos = (int) position;
				}
			}
			else
			{
				//problematic: detect if the max drop is low enough
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
			try {
				rto.write((accumulator+"\n").getBytes());
				//dbg.write((lagger.b[lagger.start]+"\n").getBytes());
				//number of zero crossings in this graph should give away the count
				//sampl.write((accumulator - lagger.b[lagger.start]+"\n").getBytes() );
				mic.write((a+"\n").getBytes());
				dd.write((runnerAvg/position+"\n").getBytes());//(chk.get()+"\n").getBytes());//( Math.abs(ddlt.getFirst() + ddlt.getLast()) + "\n" ).getBytes() );
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(certain > 0.5) {
				counter.increment(certain);
				System.out.println(certain+" Count:"+counter.getCount()+" time:"+((double)position/44100));
			}
		}	
	}
	
	@Override
	public void process(Data data) {
		//get the raw array of amplitude values
		double[] d = data.get();
		for( int i = 0; i < d.length; ++i) //process each amplitude in the order it arrived
			_processNext(d[i]);
		
		//if there is not data in buffer, end of input file must have been reached
		if( data.getLength() == 0 )
			System.out.println("End of data!");
	}
}
