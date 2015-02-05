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
	public synchronized void setModel(String name){
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
	}

	public int co = 0;
	private double lastVal = 0;
	private boolean lastUp = false;
	private int lastPos = 0;
	int dpos = 0;
	double rs = 0;
	double parabolicDif = 0;
	private void _processNext(double a){
		buff.push(a);
		co++;
		double certain = 0;
		//if buffer has reached proper size for comparison then perform fft
		if( buff.length() == buff.getCapacity() )
		{
			int len = buff.length(),iter = 0;
			double accumulator = 0, max = 0,lhs,rhs;
			for( int i = buff.start; len > 0 ; i++,len--,iter++ ){
				i%=buff.getCapacity();
				lhs =  Math.abs(buff.b[i]);//*buff.b[i];
				rhs =  Math.abs(rawSample[iter]);//*rawSample[iter];
				lhs =  Math.abs( lhs - rhs ); //accidental mistake yelded interesting result -= in stead of =
				accumulator += lhs;
				
				if( max < lhs )
					max = lhs;
			}
			accumulator /= iter;
			accumulator /= max;
			lagger.push(accumulator);
			double val = accumulator - lagger.b[lagger.start]; 
			//parabolic difference
			parabolicDif = 0;iter--;
			for( int i = 0; i < iter ; i++,iter-- )
				parabolicDif += lagger.get(iter) - lagger.get(i);
			
			//
			int zc = 0;
			
			if((lastVal < 0 && parabolicDif > 0))// || (lastVal > 0 && parabolicDif < 0))
			{
				zc = 1;
				if( co - lastPos >= buff.getCapacity() )
					certain = 1;
				dpos = co - lastPos;
				//zero crossing
				lastVal = parabolicDif;
				lastPos = co;
			}
			lastVal = parabolicDif;
				
			try {
				rto.write((accumulator+"\n").getBytes());
				dbg.write((lagger.b[lagger.start]+"\n").getBytes());
				//number of zero crossings in this graph should give away the count
				sampl.write((val+"\n").getBytes() );
				mic.write((a+"\n").getBytes());
				dd.write((zc+"\n").getBytes());//(chk.get()+"\n").getBytes());//( Math.abs(ddlt.getFirst() + ddlt.getLast()) + "\n" ).getBytes() );
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(certain > 0.9)
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
