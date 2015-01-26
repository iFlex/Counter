/*
 * Author: Milorad Liviu Felix
 * 14 Jan 2014  22:00 GMT
 */

package engine.Processing.algorithms;
import engine.Processing.Processor;

import org.jtransforms.fft.DoubleFFT_1D;

import engine.Processing.Recogniser;
import engine.Processing.algorithms.FftRidgeRecogniser.RingBuffer;
import engine.util.Counter;
import engine.util.Data;
import engine.audio.*;

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
	//alternative 
	private RingBuffer buff;

	public RawRidgeRecogniser(Counter c){
		System.out.println("RAW ridge recogniser");
		//debug
		dbg = null;
		try {
			dbg = new FileOutputStream(new File("output.txt"));
			rto = new FileOutputStream(new File("ratio.txt"));
			sampl = new FileOutputStream(new File("sample.txt"));
			mic = new FileOutputStream(new File("mic.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		counter = c;
		sample = new Data();
		//for now load the sample here
		SampleIn = new FileIn("res/snap.wav");
		SampleIn.blockingStart();
		SampleIn.run();
		//1. get the data
		Data d = SampleIn.getNext();
		while( d != null ){
			sample.extend(d);
			d = SampleIn.getNext();
		}
		rawSample = sample.get();
		if( rawSample == null )
		{
			System.out.println("Error: could not initialise correctly! Sample is empty");
			return;
		}
		buff = new RingBuffer(rawSample.length);
	}

	public int co = 0;
	private void _processNext(double a){
		buff.push(a);
		co++;
		//if buffer has reached proper size for comparison then perform fft
		if( buff.length() == buff.getCapacity() )
		{
			int len = buff.length(); 
			int iter = 0;
			double ratio = 1;
			double _rat = 0;
			double lastRatio = 0;
			
			for( int i = buff.start; len > 0 ; i++,len--,iter++ ){
				i%=buff.getCapacity();
				
				double lhs = Math.abs(buff.b[i]);//*buff.b[i];
				double rhs = Math.abs(rawSample[iter]);//*rawSample[iter];
				if( lhs == 0 && rhs != 0)
				{
					_rat += rhs;
					System.out.println("Mic Silence..");
				}
				if( rhs == 0 )
				{
					_rat += lhs;
					if( lhs == 0 )
						System.out.println("Total Silene...");
					else
						System.out.println("Sample Silence..");
				}
				if( lhs != 0 && rhs != 0 )
				{
					if( lhs < rhs )
					{
						double aux = lhs;
						lhs = rhs;
						rhs = lhs;
					}
					//issue: when the microphone input is 2 silent it fits well anything
					double q = Math.floor(lhs/rhs);
					_rat += (lhs - q * rhs);
				}
			}
			
			lastRatio = _rat;
			_rat = Math.abs(_rat/buff.length());
			if( _rat == 0 )
				ratio = 1;
			else
				ratio = 1 / _rat; //closeness of _rat to 0
			
			try {
				dbg.write((co+" rat:"+ratio+" r:"+_rat+" lr:"+lastRatio+"\n").getBytes());
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(ratio > 0.9)
			{
				counter.increment(ratio);
				System.out.println(ratio+" Count:"+counter.getCount());
				int jump = (int)(buff.getCapacity());
				while(jump-- > 0)
					try {
						buff.pop();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						break;
					}
			}
		}
		else
		{
			/*try {
				dbg.write((co+" raw:"+a+"\n").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
	}
	
	@Override
	public void process(Data data) {
		double[] d = data.get();
		for( int i = 0; i < d.length; ++i)
			_processNext(d[i]);
		//System.out.println("CO:"+co);
	}
	
	class RingBuffer{
		
		public double[] b;
		public int start,stop,_length,capacity;
		
		RingBuffer(int length){
			start=stop=_length=0;
			capacity = length;
			b = new double[capacity];
		}
		
		private void normaliseIndexes(){
			if(_length!=0)
			{
				if(start >= 0)
					start %= capacity;
				else
					start = capacity + start;
				
				if( stop >= 0 )
					stop %= capacity;
				else
					stop = capacity + stop;
			}
			
		}
		
		public void push(double d){
			b[stop++] = d;
			
			if( capacity == _length )
				start++;
			else
				_length++;
			
			normaliseIndexes();
		}
		
		public double pop() throws Exception{
			if( _length > 0 )
			{
				double r = b[start];
				start++;
				_length--;
				normaliseIndexes();
				return r;
			}
			throw new Exception("Empty ring buffer");
		}
		
		public double popEnd() throws Exception{
			if( _length > 0 )
			{
				double r = b[stop];
				stop--;
				_length--;
				normaliseIndexes();
				return r;
		
			}
			throw new Exception("Empty ring buffer");
		}
		
		public int getCapacity(){
			return capacity;
		}
		
		public int length(){
			return _length;
		}
		@Override
		public String toString(){
			String str = "";
			int len = _length;
			for( int i = start ; len > 0 ; ++i,len-- )
			{
				i%=capacity;
				str+= " "+b[i];
			}
			return str;
		}
	}
}
