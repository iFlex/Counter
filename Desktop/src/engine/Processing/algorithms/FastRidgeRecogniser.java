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

public class FastRidgeRecogniser extends Recogniser {
	
	//debug
	FileOutputStream dbg;
	FileOutputStream rto;
	FileOutputStream sampl;
	FileOutputStream mic;
	FileOutputStream dd;
	FileOutputStream smp;
	//alternative 
	private RingBuffer buff;
	private double skipRate = 0.05;
	private int toSkip = 0,lastCount = 0;
	
	//private RingSum chk;
	
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
		lastCount = 0;
		minDiff = 65535;
	}
	
	double runnerAvg = 0,theAvg=0,accumulator=0;
	double maxDrop = 65535,minDiff = 0; int maxDropPos=0, startTrack=0;
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
				double lim = (minDiff + (theAvg-minDiff)*0.3);//20% above min diff
				if( startTrack != 0 && (maxDrop <= lim && (position - startTrack > 50))){ //only consider counting if the drop was low enough
					//calculate how fast the maximum was reached
					int len = (int)(position - startTrack);
					//if( len >= buff.getCapacity()*0.25 ){
						int dist = ( len - ( maxDropPos - startTrack )); 
						if(dist < 0)
							dist = 1;
						certain = ((double)dist / len)*1.0;
						System.out.println("crt:"+certain+" maxDrop:"+maxDrop+" avg:"+theAvg+" dist:"+( maxDropPos - startTrack )+" max:"+buff.getCapacity()+" time:"+((double)position/44100));
					//}
				}
				//evaluate
				maxDrop = theAvg;
				maxDropPos = 0;
				startTrack = 0;
			}
			try {
				rto.write((accumulator+"\n").getBytes());
				dbg.write((theAvg+"\n").getBytes());
				//number of zero crossings in this graph should give away the count
				sampl.write((minDiff+"\n").getBytes() );
				mic.write((a+"\n").getBytes());
				dd.write((runnerAvg/position+"\n").getBytes());//(chk.get()+"\n").getBytes());//( Math.abs(ddlt.getFirst() + ddlt.getLast()) + "\n" ).getBytes() );
			} catch (IOException e) {
				e.printStackTrace();
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
		double[] d = data.get();
		for( int i = 0; i < d.length; ++i)
			_processNext(d[i]);
		if( data.getLength() == 0 )
			System.out.println("End of data!");
	}
}
