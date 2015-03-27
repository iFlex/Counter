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
//this algorithm is derived from RawRidgeRecogniser
public class FastRidgeRecogniser extends Recogniser {
	
	//debug file handlers
	FileOutputStream dbg;
	FileOutputStream rto;
	FileOutputStream sampl;
	FileOutputStream mic;
	FileOutputStream dd;
	FileOutputStream smp;
	//global state retaining data used by algorithm
	private RingBuffer buff;
	private int toSkip = 0;
	private double skipRate = 0.05;
	private double runnerAvg = 0,theAvg=0,accumulator=0;
	private double maxDrop = 65535,minDiff = 0; int maxDropPos=0, startTrack=0;
	//default constructor calls constructor from Recogniser class to do setup
	public FastRidgeRecogniser(Counter c){
		super(c);
		System.out.println("Fast Ridge Recogniser");
		//debug
		dbg = null;
		try {
			dbg   = new FileOutputStream(new File("tests/graphs/average.txt"));
			rto   = new FileOutputStream(new File("tests/graphs/accumulator.txt"));
			sampl = new FileOutputStream(new File("tests/graphs/mindiff.txt"));
			mic   = new FileOutputStream(new File("tests/graphs/rawmicinput.txt"));
			dd    = new FileOutputStream(new File("tests/graphs/zerocrosser.txt"));
			smp   = new FileOutputStream(new File("tests/graphs/thesamplesn.txt"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	//set a new model, all function from parent class to do the reset and then reprocess the model sample
	//calculate how much to skip for every full buffer
	//reset all time minimum
	public synchronized void setModel(String name){
		super.setModel(name);
		buff = new RingBuffer(rawModel.length);
		
		for( int i = 1 ; i < rawModel.length; ++i )
		{
			buff.push(0);
			try {
				smp.write((rawModel[i-1]+"\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		toSkip = (int)(rawModel.length*skipRate);
		minDiff = 65535;
	}
	//process the next audio sample in line
	//push it to the ring buffer, increment position in audio stream
	//if rung buffer is full then calculate distance and check if it needs to be considered
	//for match making
	//this function has the same general outline as the one from the RawRidgeRecogniser with the exception of
	//calculating the all time minimum and using a different threshold for the minimum of the sector under the mean value
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
				lhs =  Math.abs(buff.get(i));
				rhs =  Math.abs(rawModel[i]);
				lhs =  Math.abs( lhs - rhs );
				accumulator += lhs;
				
				if( max < lhs )
					max = lhs;
			}
			//get average distance
			accumulator /= i;
			//normalise
			accumulator /= max;
			
			//calculate all time minimum
			if( minDiff > accumulator )
				minDiff = accumulator;
			
			runnerAvg += accumulator;
			theAvg = (runnerAvg/position);
			//track when graph goes under the mean level
			if( accumulator < theAvg ){
				//record the position where the graph has descended under the mean level
				if(startTrack == 0)
					startTrack = (int) position;
				
				//track minimum value and its position
				if(accumulator < maxDrop ){
					maxDrop = accumulator;
					maxDropPos = (int) position;
				}
			}
			else
			{
				//calculate the value of (20% of the distance between the mean and all time minimum) above the all time minimum 
				double lim = (minDiff + (theAvg-minDiff)*0.3);//20% above min diff
				//if graph has been under the mean for at least a sample and its minimum value (maxDrop) is lower than the above threshold
				//and the section of the graph is no shorter than 50 samples then accept minimum as potential match
				if( startTrack != 0 && (maxDrop <= lim && (position - startTrack > 50))){
					//calculate how fast the maximum was reached
					int len = (int)(position - startTrack);
					//if( len >= buff.getCapacity()*0.25 ){
						int dist = ( len - ( maxDropPos - startTrack )); 
						if(dist < 0)
							dist = 1;
						//calculate certainty in relation to the length of the sector under the mean level
						certain = ((double)dist / len)*1.0;
						System.out.println("crt:"+certain+" maxDrop:"+maxDrop+" avg:"+theAvg+" dist:"+( maxDropPos - startTrack )+" max:"+buff.getCapacity()+" time:"+((double)position/44100));
					//}
				}
				//reset
				maxDrop = theAvg;
				maxDropPos = 0;
				startTrack = 0;
			}
			//write debug values to files to enable plotting
			try {
				rto.write((accumulator+"\n").getBytes());
				dbg.write((theAvg+"\n").getBytes());
				sampl.write((minDiff+"\n").getBytes() );
				mic.write((a+"\n").getBytes());
				dd.write((runnerAvg/position+"\n").getBytes());//(chk.get()+"\n").getBytes());//( Math.abs(ddlt.getFirst() + ddlt.getLast()) + "\n" ).getBytes() );
			} catch (IOException e) {
				e.printStackTrace();
			}
			//if certainty is high enough attempt a count
			if(certain > 0.5)
			{
				counter.increment(certain);
				System.out.println(certain+" Count:"+counter.getCount()+" time:"+((double)position/44100));
			}
			//skip calculated number of frames to speed up algorithm
			for(int idx = 0; idx < toSkip; ++idx){
				try {
					buff.pop();
				} catch (Exception e) {
					break;
				}
			}
		}
		else{
			//maintain the average for samples that come in but don't trigger a distance calculation
			runnerAvg += accumulator;
			theAvg = (runnerAvg/position);		
		}
	}
	//process each sample in line from the Data object received form the processor
	@Override
	public void process(Data data) {
		double[] d = data.get();
		for( int i = 0; i < d.length; ++i)
			_processNext(d[i]);
		if( data.getLength() == 0 )
			System.out.println("End of data!");
	}
}
