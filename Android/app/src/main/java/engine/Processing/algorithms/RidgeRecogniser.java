/*
 * Author: Milorad Liviu Felix
 * 14 Jan 2014  22:00 GMT
 * Works relatively well, recognizes other similar sounds as well not just the exact sample
 */

package engine.Processing.algorithms;
import engine.Processing.Processor;
import engine.Processing.Recogniser;
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
public class RidgeRecogniser extends Recogniser {

	//it reads its own data
	private AudioIn SampleIn;
	//Sample data
	private Data sample;
	private double[] angles;
	private double sampleDistance = 1/44100; //
	//recognition internals
	private int sIndex = 0;
	private int totalAngles = 0;
	//the counter
	private Counter counter;
	//debug
	FileOutputStream dbg;
	FileOutputStream rto;
	FileOutputStream sampl;
	FileOutputStream mic;
	//alternative 
	private RingBuffer buff;
	public synchronized void setModel(String name){
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
		
		sample = new Data();
		//for now load the sample here
		SampleIn = new FileIn("res/snap.wav");
		SampleIn.blockingStart();
		SampleIn.run();
		Data d = SampleIn.getNext();
		while( d != null ){
			sample.extend(d);
			d = SampleIn.getNext();
		}
		//now get the angles from the sample
		double[] dta = sample.get();
		if( dta != null )
		{
			angles = new double[dta.length];
			totalAngles = dta.length-1;
			System.out.println("Sample size:"+dta.length);
			//System.out.print("RAW:");
			//normalise negative side;
			int i=0;
			for(i=0;i<dta.length;++i)
			{
				try {
					sampl.write((dta[i]+"\n").getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				angles[i] = dta[i]*dta[i];
				//System.out.print(" "+angles[i]);
			}
			int sp = totalAngles;
			totalAngles = 0;
			for(i = 0 ; i < sp; ++i )
				angles[totalAngles++] = getAngle(angles[i],angles[i+1]);
			
			buff = new RingBuffer(totalAngles);
		}
		else
			System.out.println("Error: could not initialise correctly! Sample is empty");
	}
	
	private double getAngle(double a, double b){
		return 1+Math.atan2(b-a,sampleDistance);
	}
	
	public RidgeRecogniser(Counter c){
		super(null); counter = c;
	}

	public int co = 0;
	double lastVal = -1;
	private void _processNext(double a){
		try {
			mic.write((a+"\n").getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		co++;
		a = a*a;
		if( lastVal >= 0 )
			buff.push(getAngle(lastVal,a));
		lastVal = a;
		//iteration
		if(buff.length() == buff.getCapacity())
		{
			double ratio = 1;
			double _rat = 0;
			int len = buff.length(); 
			int iter = 0;
			for( int i = buff.start; len > 0 ; i++,len-- ){
				i%=buff.getCapacity();
				/*
				try {
					dbg.write((buff.b[i] + " vs "+angles[iter]).getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				_rat = Math.abs( buff.b[i] / angles[iter++] );
				if( _rat > 1 )
					_rat = 1/_rat;
				/*
				try {
					dbg.write((" "+_rat+"\n").getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				ratio += _rat;
				ratio/=2;
			}
			
			int jump = 0;
			if(ratio > 0.72)
			{
				jump = (int)(buff.getCapacity()*ratio);
				while(jump-- > 0)
					try {
						buff.pop();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				//System.out.println(co+" Event:"+ratio);
			
			}
			if( ratio > 0.8 )
				counter.increment(ratio);
			
			try {
				dbg.write((co+" ratio:"+ratio+" jump:"+(int)(buff.getCapacity()*ratio)+"\n").getBytes());
				rto.write((ratio+"\n").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
