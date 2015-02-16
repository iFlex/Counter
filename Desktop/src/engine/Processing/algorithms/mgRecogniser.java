/*
 * Author: Milorad Liviu Felix
 * 14 Jan 2014  22:00 GMT
 */

package engine.Processing.algorithms;


import java.io.ByteArrayInputStream;
import java.io.File;

import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.audio.*;
import engine.util.*;

import com.musicg.wave.*;
import com.musicg.fingerprint.*;

public class mgRecogniser extends Recogniser {

	private Counter counter;
	Wave model,frag;
	FingerprintSimilarity s;
	Data toprocess;
	
	
	public synchronized void setModel(String name){
		model = new Wave(name);
	}
	
	public mgRecogniser(Counter c){
		super(c);
		System.out.println("MusicG recogniser");
		counter = c;
		this.toprocess = new Data();
		//c.enableIncremental();
	}
	
	@Override
	public void process(Data data) {
		this.toprocess.extend(data);
		
		if(this.toprocess.getLength() < this.model.getNormalizedAmplitudes().length)
		{
			return;
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		WavFile copy;
		try
		{
			copy = WavFile.newWavFile(new File("./mgrecprocess.wav"), 1, this.toprocess.getLength(), 16, 44100);
		} catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		System.out.println("Stopped audio in");
		System.out.println("Copying the file to the new file");
		try
		{
			copy.writeFrames(this.toprocess.get(), this.toprocess.getLength());
			copy.close();
		} catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		frag = new Wave("./mgrecprocess.wav");
		s = model.getFingerprintSimilarity(frag);
		System.out.println("Similarity:"+s.getSimilarity());
		counter.increment(s.getSimilarity());
	}
}
