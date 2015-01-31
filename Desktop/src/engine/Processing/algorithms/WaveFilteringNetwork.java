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
import engine.util.RingBuffer;
//
public class WaveFilteringNetwork implements Recogniser {
	
	//it reads its own data
	private AudioIn SampleIn;
	//the counter
	private Counter counter;
	double[] thedta;
    double[] sample;
    //debg
  	FileOutputStream dbg;
  	FileOutputStream rto;
  	FileOutputStream sampl;
  	FileOutputStream mic;
  	FileOutputStream fftmic;
    public int co = 0;
	//alternative 
	private RingBuffer buff;
	
	private void Normalise(double[] buffer,int length){
		// f(x) = (2*x)/MAX - 1/2;
		// 75% = 1 25% = 0
		double m = buffer[0];
		for( int i=1; i < length ; ++i)
			if(m < buffer[i])
				m = buffer[i];
		
		for( int i = 0 ; i < length; ++i )
			buffer[i]=buffer[i]/m; //2*buffer[i]/m - 1/2;
	}
	public synchronized void setModel(String name){
		Data _sample = new Data();
		//for now load the sample here
		SampleIn = new FileIn(name);
		SampleIn.blockingStart();
		//1. get the data
		Data d = SampleIn.getNext();
		while( d != null ){
			_sample.extend(d);
			d = SampleIn.getNext();
		}
		sample = _sample.get();
		if( sample != null )
		{
			Normalise(sample,sample.length);
		    for( int i = 0 ; i < sample.length; ++i)
		    {
		    	try {
		    		sampl.write((sample[i]+"\n").getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    thedta = new double[sample.length];
		    buff = new RingBuffer(sample.length);
		    System.out.println("## Sample size:"+sample.length);
		}
		else
			System.out.println("Error: could not initialise correctly! Sample is empty");
	}
	
	public WaveFilteringNetwork(Counter c){
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
		double segmentDensity = 100;//skipAmount*2;
		int len = buff.getCapacity();
		for(int i = 0; i < len ; i++ )
		{
			if( thedta[i] > noise )
			{
				areaUnderSample += thedta[i];
				areaUnderMicrph += sample[i];
			}
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
		return ( maxSimilarity + avgSimilarity ) / 2;
	}
	
	private double mcompare( double noise ){
		if(noise > 1)
			noise = 1;
		if( noise < 0 )
			noise = 0;
		
		double sSample = 0;
		double sMicrph = 0;
		double largestDiff = 0;
		double avgDiff = 0;
		int segmentDensity = 20;
		int len = buff.getCapacity();
		double[] diffs = new double[(len+1)/segmentDensity];
		for(int i = 0; i < len ; i++ )
		{
			//if( fftdta[i] > noise )
			//{
				sSample += thedta[i];
				sMicrph += sample[i];
			//}
			if( (i % segmentDensity) == 0 && i != 0)
			{
				sSample /= segmentDensity;
				sMicrph /= segmentDensity;
				
				double diff = Math.abs(sSample - sMicrph);
				//diffs[i/segmentDensity] = diff;
				avgDiff += diff;
				if( largestDiff < diff || largestDiff == 0 )
					largestDiff = diff;
				
				sSample = 0;
				sMicrph = 0;
			}
		}
		avgDiff /= (len/segmentDensity);
		double r = (1 -(largestDiff+avgDiff)/2); 
		/*if( r > 0.7 )
		{
			System.out.println("avgDiff:"+avgDiff+" largest:"+largestDiff+" r:"+r);
			for( int i = 0 ; i < len/segmentDensity; ++i )
				System.out.print(" "+diffs[i]);
			System.out.println("");
		}*/
		return r;
	}
	
	private void _processNext(double a){
		co++;
		buff.push(a);
		//if buffer has reached proper size for comparison then perform fft
		if( buff.length() == buff.getCapacity() )
		{
			int len = buff.length(); 
			int iter = 0;
			int i = 0;
			//1. copy buffer data in fft buffer
			double max = buff.getFirst(),nxt;
			while( len > 0)
			{
				len--;
				//nxt = buff.getNext();
				//if( max < nxt)
					//max = nxt;
			}
			len = buff.length();
			//2. Normalise
			//Normalise(thedta,len);
			//3. Compare
			double ratio = 0;
			//double ratio = mcompare(0.0);
			if( ratio > 0.75)
			{
				//4. Increment
				System.out.println(ratio+" Count:"+counter.getCount()+" @ "+((co-buff.getCapacity())*(1.0/44100))+"s");
				counter.increment(ratio);
				writeCMPdta(co-buff.getCapacity());
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
	
	private void writeCMPdta(int index){
	try {
		fftmic = new FileOutputStream(new File("./debug/_raw/"+(index)+"raw.txt"));
	} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	int len = buff.getCapacity();
	for( int i = 0 ; i < len; ++ i )
	{
		try {
			fftmic.write((thedta[i]+"\n").getBytes());
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
