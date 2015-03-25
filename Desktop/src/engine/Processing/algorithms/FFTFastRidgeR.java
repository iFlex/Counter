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
public class FFTFastRidgeR extends Recogniser {
	
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
	private int toSkip = 0;
	DoubleFFT_1D fft;
	private double[] modelDump,sampleDump;
	private boolean ultradebug = false;
	//private RingSum chk;
	
	public FFTFastRidgeR(Counter c){
		super(c);
		System.out.println("RAW ridge recogniser");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void setModel(String name){
		super.setModel(name);
		buff = new RingBuffer(rawModel.length);
		//chk = new RingSum(rawModel.length);
		modelDump = new double[rawModel.length*2];
		sampleDump = new double[modelDump.length];
		for( int i = 0 ; i < rawModel.length; ++i )
		{
			buff.push(0);
			modelDump[i] = rawModel[i];
		}
		
		fft = new DoubleFFT_1D(rawModel.length);
		fft.realForwardFull(modelDump);
		for(int i=0;i < modelDump.length; i+=2 ){
			modelDump[i] = Math.sqrt(modelDump[i]*modelDump[i]+modelDump[i+1]*modelDump[i+1]);
			try {
				smp.write((modelDump[i]+"\n").getBytes());
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		toSkip = (int)(rawModel.length*skipRate);
		minDiff = 65535;
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
			//copy sample
			for( int i = 0; i < rawModel.length; ++i )
				sampleDump[i] = buff.get(i);
			//do fft
			fft.realForwardFull(sampleDump);
			//convert to abs val
			for( int i = 0 ; i < sampleDump.length; i += 2 )
				sampleDump[i] = Math.sqrt(sampleDump[i]*sampleDump[i]+sampleDump[i+1]*sampleDump[i+1]);
			FileOutputStream udbg = null;
			if(ultradebug == true)
			try {
				  udbg = new FileOutputStream(new File("tests/graphs/udbg/"+((double)position/44100)+".txt"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//perform distance calculation
			for(int i = 0; i < sampleDump.length ; i+=2 ){
				lhs =  Math.abs(sampleDump[i]);//*buff.b[i];
				rhs =  Math.abs(modelDump[i]);//*rawModel[iter];
				lhs =  Math.abs( lhs - rhs ); //accidental mistake yelded interesting result -= in stead of =
				accumulator += lhs;
				if(ultradebug == true)
					try {
						udbg.write((sampleDump[i]+"\n").getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if( max < lhs )
					max = lhs;
			}
			if(ultradebug == true)
			try {
				udbg.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			accumulator /= rawModel.length;
			accumulator /= max;//normalise
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
						certain = ((double)dist / len)*1.55;
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
