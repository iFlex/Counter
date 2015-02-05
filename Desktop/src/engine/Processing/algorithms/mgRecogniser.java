/*
 * Author: Milorad Liviu Felix
 * 14 Jan 2014  22:00 GMT
 */

package engine.Processing.algorithms;


import java.io.ByteArrayInputStream;

import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.audio.*;
import engine.util.*;

import com.musicg.wave.*;
import com.musicg.fingerprint.*;

public class mgRecogniser implements Recogniser {

	private Counter counter;
	Wave model,frag;
	FingerprintSimilarity s;
	
	public synchronized void setModel(String name){
		model = new Wave(name);
	}
	
	public mgRecogniser(Counter c){
		System.out.println("MusicG recogniser");
		counter = c;
		//c.enableIncremental();
	}
	
	@Override
	public void process(Data data) {
		WaveHeader hdr = new WaveHeader();
		hdr.setSampleRate(44100);
		System.out.println(hdr.toString()+" is valid:"+hdr.isValid());
		ByteArrayInputStream bais = new ByteArrayInputStream(data.getRaw());
		frag = new Wave(bais);
		s = model.getFingerprintSimilarity(frag);
		System.out.println("Similarity:"+s.getSimilarity());
		counter.increment(s.getSimilarity());
	}
}
