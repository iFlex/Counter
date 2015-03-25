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
	
	//debug
	FileOutputStream dbg;
	FileOutputStream rto;
	FileOutputStream sampl;
	FileOutputStream mic;
	FileOutputStream dd;
	FileOutputStream smp;
	//alternative 
	private RingBuffer buff,lagger,ddlt;
	//private RingSum chk;
	
	public RawRidgeRecogniser(Counter c){
		super(c);
		System.out.println("RAW ridge recogniser");
		//debug
		dbg = null;
		try {
			dbg   = new FileOutputStream(new File("tests/graphs/lagbehinder.txt"));
			rto   = new FileOutputStream(new File("tests/graphs/accumulator.txt"));
			sampl = new FileOutputStream(new File("tests/graphs/lagbehdelta.txt"));
			mic   = new FileOutputStream(new File("tests/graphs/rawmicinput.txt"));
			dd    = new FileOutputStream(new File("tests/graphs/zerocrosser.txt"));
			smp   = new FileOutputStream(new File("tests/graphs/thesamplesn.txt"));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void setModel(String name){
		super.setModel(name);
		buff = new RingBuffer(rawModel.length);
		lagger = new RingBuffer(rawModel.length);
		ddlt = new RingBuffer(rawModel.length);
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
		
	}
	
	double runnerAvg = 0,theAvg=0;
	double maxDrop = 65535; int maxDropPos=0, startTrack=0;
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
			lagger.push(accumulator);
			theAvg = (runnerAvg/position);
			if( accumulator < theAvg ){
				if(startTrack == 0)
				{
					startTrack = (int) position;
					
				}
				//track
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
				dbg.write((lagger.b[lagger.start]+"\n").getBytes());
				//number of zero crossings in this graph should give away the count
				sampl.write((accumulator - lagger.b[lagger.start]+"\n").getBytes() );
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
		if( data.getLength() == 0 )
			System.out.println("End of data!");
	}
}
