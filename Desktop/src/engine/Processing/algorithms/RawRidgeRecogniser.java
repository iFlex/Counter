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
public class RawRidgeRecogniser implements Recogniser {

	//it reads its own data
	private AudioIn SampleIn;
	//Sample data
	private Data sample;
	//the counter
	private Counter counter;
	double[] rawSample;
	//debug
	FileOutputStream dbg;
	FileOutputStream rto;
	FileOutputStream sampl;
	FileOutputStream mic;
	FileOutputStream dd;
	FileOutputStream smp;
	//alternative 
	private RingBuffer buff,lagger,ddlt;
	private RingSum chk;
	private int co = 0;
	public synchronized void setModel(String name){
		co = 0;
		sample = new Data();
		//for now load the sample here
		SampleIn = new FileIn(name);
		SampleIn.blockingStart();
		//1. get the data
		Data d = SampleIn.getNext();
		while( d != null ){
			sample.extend(d);
			d = SampleIn.getNext();
		}
		rawSample = sample.get();
		buff = new RingBuffer(rawSample.length);
		lagger = new RingBuffer(rawSample.length);
		ddlt = new RingBuffer(rawSample.length);
		chk = new RingSum(rawSample.length);
		
		for( int i = 1 ; i < rawSample.length; ++i )
		{
			buff.push(0);
			try {
				smp.write((rawSample[i-1]+"\n").getBytes());
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if( rawSample == null )
		{
			System.out.println("Error: could not initialise correctly! Sample is empty");
			return;
		}
		
	}
	
	public RawRidgeRecogniser(Counter c){
		System.out.println("RAW ridge recogniser");
		//debug
		dbg = null;
		try {
			dbg   = new FileOutputStream(new File("lagbehinder.txt"));
			rto   = new FileOutputStream(new File("accumulator.txt"));
			sampl = new FileOutputStream(new File("lagbehdelta.txt"));
			mic   = new FileOutputStream(new File("rawmicinput.txt"));
			dd    = new FileOutputStream(new File("zerocrosser.txt"));
			smp   = new FileOutputStream(new File("thesamplesn.txt"));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		counter = c;
		co = 0;
	}

	double runnerAvg = 0,theAvg=0;
	double maxDrop = 65535; int maxDropPos=0, startTrack=0;
	private void _processNext(double a){
		buff.push(a);
		co++;
		double certain = 0;
		//if buffer has reached proper size for comparison then perform fft
		if( buff.length() == buff.getCapacity() )
		{
			double accumulator = 0, max = 0,lhs,rhs;
			int i;
			for( i = 0; i < rawSample.length ; i++ ){
				lhs =  Math.abs(buff.get(i));//*buff.b[i];
				rhs =  Math.abs(rawSample[i]);//*rawSample[iter];
				lhs =  Math.abs( lhs - rhs ); //accidental mistake yelded interesting result -= in stead of =
				accumulator += lhs;
				
				if( max < lhs )
					max = lhs;
			}
			accumulator /= i;
			accumulator /= max;
			runnerAvg += accumulator;
			lagger.push(accumulator);
			theAvg = (runnerAvg/co);
			if( accumulator < theAvg ){
				if(startTrack == 0)
				{
					startTrack = co;
					
				}
				//track
				if(accumulator < maxDrop ){
					maxDrop = accumulator;
					maxDropPos = co;
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
						System.out.println("crt:"+certain+" maxDrop:"+maxDrop+" avg:"+theAvg+" dist:"+( maxDropPos - startTrack )+" max:"+buff.getCapacity());
				}
				//evaluate
				maxDrop = theAvg;
				maxDropPos = co;
				startTrack = 0;
			}
			try {
				rto.write((accumulator+"\n").getBytes());
				dbg.write((lagger.b[lagger.start]+"\n").getBytes());
				//number of zero crossings in this graph should give away the count
				sampl.write((accumulator - lagger.b[lagger.start]+"\n").getBytes() );
				mic.write((a+"\n").getBytes());
				dd.write((runnerAvg/co+"\n").getBytes());//(chk.get()+"\n").getBytes());//( Math.abs(ddlt.getFirst() + ddlt.getLast()) + "\n" ).getBytes() );
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(certain > 0.5)
			{
				counter.increment(certain);
				System.out.println(certain+" Count:"+counter.getCount());
				/*int jump = (int)(buff.getCapacity());
				while(jump-- > 0)
					try {
						buff.pop();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						break;
					}*/
			}
		}	
	}
	
	@Override
	public void process(Data data) {
		double[] d = data.get();
		for( int i = 0; i < d.length; ++i)
			_processNext(d[i]);
		System.out.println("CO:"+co);
	}
}
