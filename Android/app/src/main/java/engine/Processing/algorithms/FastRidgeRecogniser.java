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
		System.out.println("RAW ridge recogniser");
		//debug
		dbg = null;
		try {
			dbg   = new FileOutputStream(new File("average.txt"));
			rto   = new FileOutputStream(new File("accumulator.txt"));
			sampl = new FileOutputStream(new File("mindiff.txt"));
			mic   = new FileOutputStream(new File("rawmicinput.txt"));
			dd    = new FileOutputStream(new File("zerocrosser.txt"));
			smp   = new FileOutputStream(new File("thesamplesn.txt"));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void setModel(String name){
		super.setModel(name);
		buff = new RingBuffer(rawModel.length);
		//chk = new RingSum(rawModel.length);
		for( int i = 1 ; i < rawModel.length; ++i )
		{
			buff.push(0);
			try {
				smp.write((rawModel[i-1]+"\n").getBytes());
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		toSkip = (int)(rawModel.length*skipRate);
		lastCount = 0;
		minDiff = 65535;
	}
	private void adjustCount(){
		if( counter.getCount() - lastCount > 1 )
			counter.setCount( lastCount + 1 );
		lastCount = counter.getCount();
	}
	double runnerAvg = 0,theAvg=0,accumulator=0;
	double maxDrop = 65535,minDiff = 0; int maxDropPos=0, startTrack=0;
	private void _processNext(double a){
		buff.push(a);
		position++;
		
		double certain = 0;
		//if buffer has reached proper size for comparison then perform fft
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
				if(startTrack == 0){
					startTrack = (int) position;
					//System.out.println("Plunge:"+((double)position/44100));
				}
				//track
				if(accumulator < maxDrop ){
					maxDrop = accumulator;
					//System.out.println("MDDROP:"+maxDrop+" avg:"+theAvg+" @"+((double)position/44100));
					maxDropPos = (int) position;
				}
			}
			else
			{
				//problematic: detect if the max drop is low enough
				//System.out.println("FAIL? st:"+startTrack+" md:"+maxDrop+" lim:"+(minDiff + (theAvg-minDiff)*0.2)+" avg:"+theAvg);
				double lim = (minDiff + (theAvg-minDiff)*0.2);//20% above min diff
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
				//else
					//System.out.println("FAIL! st:"+startTrack+" md:"+maxDrop+" lim:"+(minDiff + (theAvg-minDiff)*0.2)+" avg:"+theAvg);
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
			// TODO Auto-generated catch block
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
		//correct the count
		//if( position % buff.getCapacity() == 0 )
			//adjustCount();
	}
	
	@Override
	public void process(Data data) {
		double[] d = data.get();
		for( int i = 0; i < d.length; ++i)
			_processNext(d[i]);
		if( data.getLength() == 0 ){
			//adjustCount();
			System.out.println("End of data!");
		}
	}
}
