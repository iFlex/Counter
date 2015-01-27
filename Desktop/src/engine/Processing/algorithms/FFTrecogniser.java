/*
 * Author: Milorad Liviu Felix
 * 14 Jan 2014  22:00 GMT
 */
package engine.Processing.algorithms;
import engine.Processing.Processor;

import org.jtransforms.fft.DoubleFFT_1D;

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
public class FFTrecogniser implements Recogniser {
	
	//it reads its own data
	private AudioIn SampleIn;
	//Sample data
	private Data sample;
	//the counter
	private Counter counter;
	DoubleFFT_1D fftDo;
	double[] fftdta;
    double[] samplefft;
    public int skipAmount = 32;
    public int skipIndex = 0;
	//debug
	FileOutputStream dbg;
	FileOutputStream rto;
	FileOutputStream sampl;
	FileOutputStream mic;
	FileOutputStream fftmic;
	public int co = 0;
	//alternative 
	private RingBuffer buff;
	
	/*private void FFT(){
	}*/
	
	private void beautifyFFT(double[] data){
		int index = 1;
		for( int i = 2; i < data.length; i+=2 )
			data[index++] = Math.abs(data[i]);
	}
	
	private void Normalise(double[] buffer,int length){
		//f(x) = (2*x)/MAX - 1/2;
		// 75% = 1 25% = 0
		double m = buffer[0];
		for( int i=1; i < length ; ++i)
			if(m < buffer[i])
				m = buffer[i];
		
		for( int i = 0 ; i < length; ++i )
			buffer[i]=buffer[i]/m; //2*buffer[i]/m - 1/2;
	}
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
		//2. FFT the data
		double[] dta = sample.get();
		if( dta != null )
		{
			fftDo = new DoubleFFT_1D(dta.length);
			buff = new RingBuffer(dta.length);
			samplefft = new double[dta.length * 2];
		    fftdta = new double[ samplefft.length ];
		    System.out.println("Sample length:"+dta.length);
		    System.arraycopy(dta, 0, samplefft, 0, dta.length);
		    fftDo.realForwardFull(samplefft);
		    
		    beautifyFFT(samplefft);
		    Normalise(samplefft,dta.length);
		    for( int i = 0 ; i < dta.length; ++i)
		    {
		    	try {
		    		dbg.write((dta[i]+"\n").getBytes());
					sampl.write((samplefft[i]+"\n").getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		else
			System.out.println("Error: could not initialise correctly! Sample is empty");
	}
	public FFTrecogniser(Counter c){
		System.out.println("FFT ridge recogniser");
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
	}
	
	private double compare( double noise ){
		if(noise > 1)
			noise = 1;
		if( noise < 0 )
			noise = 0;
		
		double areaUnderSample = 0;
		double areaUnderMicrph = 0;
		double maxSimilarity = 0;
		double avgSimilarity = 0;
		double similarity = 0;
		double segmentDensity = skipAmount*2;
		int len = buff.getCapacity();
		for(int i = 0; i < len ; i++ )
		{
			if( fftdta[i] > noise )
			{
				areaUnderSample += fftdta[i];
				areaUnderMicrph += samplefft[i];
				if( i % segmentDensity == 0 && i != 0)
				{
					similarity = areaUnderSample / areaUnderMicrph;
					if(similarity > 1)
						similarity = 1/similarity;
					
					if( similarity > maxSimilarity )
						maxSimilarity = similarity;
					
					if(avgSimilarity == 0 )
						avgSimilarity = similarity;
					else
					{
						avgSimilarity += similarity;
						avgSimilarity /= 2;
					}
					
					areaUnderSample = 0;
					areaUnderMicrph = 0;
				}
			}
		}
		return ( maxSimilarity + avgSimilarity ) / 2;
	}
	
	private void _processNext(double a){
		co++;
		buff.push(a);
		//if buffer has reached proper size for comparison then perform fft
		if( buff.length() == buff.getCapacity() && skipIndex < 1 )
		{
			skipIndex = skipAmount;
			int len = buff.length(); 
			int iter = 0;
			int i = 0;
			//1. copy buffer data in fft buffer
			for( i = buff.start; len > 0 ; i++,len--,iter++ ) {
				i %= buff.getCapacity();
				fftdta[iter] = buff.b[i];
			}
			len = buff.length();
			//2. FFT - this is 2 slow therefore needs to be improved somehow
			fftDo.realForwardFull(fftdta);
			beautifyFFT(fftdta);
			//2. Normalise
			Normalise(fftdta,len);
			//3. Compare
			double ratio = compare(0.0);
			if( ratio > 0.75)
			{
				//4. Increment
				System.out.println(ratio+" Count:"+counter.getCount()+" frame:"+(co));
				counter.increment(ratio);
				writeFFT(co-buff.getCapacity());
				int jump = (int)(buff.getCapacity()*0.95);
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
		skipIndex--;
	}
	
	@Override
	public void process(Data data) {
		double[] d = data.get();
		//long startTime = System.nanoTime();
		for( int i = 0; i < d.length; ++i)
			_processNext(d[i]);
		//long endTime = System.nanoTime();
		//long duration = (endTime - startTime);
		if( co % 1000 == 0)
			System.out.println("Frame:"+co);
		//if(d.length != 0)
			//System.out.println("dT:"+(double)duration/1000000/d.length+"ms - sampling period:"+((1.0/44100)*1000)+" ms processed:"+d.length+" frames");
		if( d.length == 0)
			System.out.println("END or data");
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
	
	private void writeFFT(int index){
	try {
		fftmic = new FileOutputStream(new File("./debug/_fft/"+(index)+"fft.txt"));
	} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	int len = buff.getCapacity();
	for( int i = 0 ; i < len; ++ i )
	{
		try {
			fftmic.write((fftdta[i]+"\n").getBytes());
		} catch (IOException e) {
			// TODO Auto-gesnerated catch block
			e.printStackTrace();
		}
	}
	try {
		fftmic.close();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	}
}
