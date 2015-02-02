/*
* Author: Milorad Liviu Felix & Pedro Avelar
* Sat 6 December 2014 17:49GMT
* Interface for the sound recognition algorithm
*/
package engine.Processing;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import engine.audio.*;
import engine.util.*;
import engine.Processing.algorithms.*;
import engine.Processing.debug.*;

import com.musicg.fingerprint.*;
import com.musicg.wave.*;

public class mgProcessor implements Runnable
{
	protected Wave inWave;
	protected Wave mdWave;
	protected String source;
	protected long startTime = 0;
	protected long endTime = 0;
	
	public mgProcessor(Counter c)
	{
	}
	
	public synchronized void setModel(String path){
		mdWave = new Wave(path);
	}
	public synchronized void setInput(String nameOrPath){ 
		inWave = new Wave(nameOrPath);
	}
	
	public void run(){
		FingerprintManager fpm = new FingerprintManager();
		byte[] mdw = mdWave.getFingerprint();
		byte[] inw = inWave.getFingerprint();
		byte[] rawDist = new byte[inw.length];
		FingerprintSimilarityComputer c = new FingerprintSimilarityComputer(mdw,inw);
		FingerprintSimilarity s = c.getFingerprintsSimilarity();
		//FingerprintSimilarity s = inWave.getFingerprintSimilarity(mdWave);
		System.out.println("Similarity:"+s.getSimilarity()+" score:"+s.getScore()+" timepos:"+s.getsetMostSimilarTimePosition());
		fpm.saveFingerprintAsFile(mdw, "model.fp");
		fpm.saveFingerprintAsFile(inw, "sampl.fp");
		
		FileOutputStream dbg = null;
		try {
			dbg = new FileOutputStream("distance.fp");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i = 0; i < inw.length ; ++i )
			try {
				dbg.write((Math.abs(inw[i] - mdw[i])+"\n").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		try {
			dbg.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	protected void _init(){
		startTime = System.currentTimeMillis();
     }

	public void start(){
		blockingRun();
	}

     public void blockingRun(){
         _init();
         run();
         stop();
     }


	public void stop(){
		endTime = System.currentTimeMillis();
		System.out.println("Duration:"+(endTime-startTime)+"ms");
	}

	public boolean isRunning(){
		return false;
	}
}
