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
//this algorithm is derived from FastRidgeRecogniser, more detailed explanations for common functions can be found thare
public class FFTFastRidgeR extends Recogniser {
	
	//Debug file output streams used to store algorithm outputs
	FileOutputStream dbg;
	FileOutputStream rto;
	FileOutputStream sampl;
	FileOutputStream mic;
	FileOutputStream dd;
	FileOutputStream smp;
	////////////////////////////////////////////////////////////
	private RingBuffer buff;
	private double skipRate = 0.05;
	private int toSkip = 0;
	DoubleFFT_1D fft;
	private double[] modelDump,sampleDump;
	private boolean ultradebug = false;
	private double runnerAvg = 0,theAvg=0,accumulator=0;
	private double maxDrop = 65535,minDiff = 0; int maxDropPos=0, startTrack=0;
	
	public FFTFastRidgeR(Counter c){
		super(c);
		System.out.println("RAW ridge recogniser");
		//debug file handlers
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
		//create a new uni dimensional FFT calculator
		fft = new DoubleFFT_1D(rawModel.length);
	}

	public synchronized void setModel(String name){
		super.setModel(name);
		buff = new RingBuffer(rawModel.length);
		modelDump = new double[rawModel.length*2];
		sampleDump = new double[modelDump.length];
		//copy model to buffer in order to perform FFT on it
		for( int i = 0 ; i < rawModel.length; ++i )
		{
			buff.push(0);
			modelDump[i] = rawModel[i];
		}
		//perform FFT on model data
		fft.realForwardFull(modelDump);
		//calculate absolute value of the complex numbers resulted form FFT
		for(int i=0;i < modelDump.length; i+=2 ){
			modelDump[i] = Math.sqrt(modelDump[i]*modelDump[i]+modelDump[i+1]*modelDump[i+1]);
			try {
				smp.write((modelDump[i]+"\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//calculate how many samples to skip for each full buffer
		toSkip = (int)(rawModel.length*skipRate);
		minDiff = 65535;
	}
	//process one sample
	private void _processNext(double a){
		buff.push(a);
		position++;
		
		double certain = 0;
		//if buffer has reached proper size for comparison then perform evaluation
		if( buff.length() == buff.getCapacity() )
		{
			double max = 0,lhs,rhs;
			accumulator = 0;
			//copy sample to perform FFT on its data
			for( int i = 0; i < rawModel.length; ++i )
				sampleDump[i] = buff.get(i);
			//perform FFT
			fft.realForwardFull(sampleDump);
			//obtain absolute value from the complex numbers returned by the FFT
			for( int i = 0 ; i < sampleDump.length; i += 2 )
				sampleDump[i] = Math.sqrt(sampleDump[i]*sampleDump[i]+sampleDump[i+1]*sampleDump[i+1]);
			
			FileOutputStream udbg = null;
			if(ultradebug == true)
			try {
				  udbg = new FileOutputStream(new File("tests/graphs/udbg/"+((double)position/44100)+".txt"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			//perform distance calculation between the two FFT results
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
				e1.printStackTrace();
			}
			//get the average
			accumulator /= rawModel.length;
			//normalise
			accumulator /= max;
			if( minDiff > accumulator )
				minDiff = accumulator;
			
			runnerAvg += accumulator;
			theAvg = (runnerAvg/position);
			if( accumulator < theAvg ){
				if(startTrack == 0)
					startTrack = (int) position;
				
				//track minimum value and where it occured
				if(accumulator < maxDrop ){
					maxDrop = accumulator;
					maxDropPos = (int) position;
				}
			}
			else
			{
				//calculations explained in RawRidgeRecogniser
				double lim = (minDiff + (theAvg-minDiff)*0.2);//20% above all time minimum
				if( startTrack != 0 && (maxDrop <= lim && (position - startTrack > 50))){ //only consider counting if the drop was low enough
					int len = (int)(position - startTrack);
					//if( len >= buff.getCapacity()*0.25 ){
						int dist = ( len - ( maxDropPos - startTrack )); 
						if(dist < 0)
							dist = 1;
						certain = ((double)dist / len)*1.55;
						System.out.println("crt:"+certain+" maxDrop:"+maxDrop+" avg:"+theAvg+" dist:"+( maxDropPos - startTrack )+" max:"+buff.getCapacity()+" time:"+((double)position/44100));
					//}
				}
				//reset
				maxDrop = theAvg;
				maxDropPos = 0;
				startTrack = 0;
			}
			//print debug values for plotting
			try {
				rto.write((accumulator+"\n").getBytes());
				dbg.write((theAvg+"\n").getBytes());
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
			//skip calculated number of frames for speedup
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
	//process each sample in turn from the Data object the Processor passed
	@Override
	public void process(Data data) {
		double[] d = data.get();
		for( int i = 0; i < d.length; ++i)
			_processNext(d[i]);
		if( data.getLength() == 0 )
			System.out.println("End of data!");
	}
}
