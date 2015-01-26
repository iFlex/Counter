/*
* Author: Milorad Liviu Felix & Pedro Avelar
* Sat 6 December 2014 17:49GMT
* Interface for the sound recognition algorithm
*/
package engine.Processing;

import java.util.concurrent.atomic.AtomicBoolean;

import engine.audio.*;
import engine.util.*;
import engine.Processing.algorithms.*;
import engine.Processing.debug.*;

public class Processor implements Runnable
{
	private AudioIn audioIn;
	private Recogniser n;
	private Recogniser debug;
	private Thread t;
	private AtomicBoolean running;
	private boolean canRun;
	private Counter count;
	
	public Processor(Counter c)
	{
		count = c;
		audioIn = new FileIn("res/noisySnap.wav");
		//audioIn= new PcMicrophoneIn();
		n = new FftRidgeRecogniser(count);
		//debug = new micFFTout();
		
		running = new AtomicBoolean(false);
		canRun = false;
		t = new Thread(this);
	
	}

	public void run(){
		running.set(true);
		while(canRun)
		{
			Data d = audioIn.getNext();
			//System.out.println("Got data from audio:"+d);
			if( d != null )
			{
				n.process(d);
				//debug.process(d);
			}
			//System.out.println("Done processing");
			//else
				//break;
		}
		//running.set(false);
	}

	public void start(){
		canRun = true;
		running.set(true);
		t.start();
		audioIn.start();
	}

	public void stop(){
		canRun = false;
		audioIn.stop();
	}
	public boolean isRunning(){
		return running.get();
	}
}
