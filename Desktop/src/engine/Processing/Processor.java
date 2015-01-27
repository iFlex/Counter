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
		//n = new FftRidgeRecogniser(count);
		n = new FFTrecogniser(count);
		//debug = new micFFTout();
		
		running = new AtomicBoolean(false);
		canRun = false;
	}

	public void run(){
		running.set(true);
		while(canRun)
		{
			Data d = audioIn.getNext();
			//System.out.println("Got data from audio:"+d);
			if( d != null )
			    n.process(d);
		}
		running.set(false);
	}

	public void start(){
		canRun = true;
		running.set(true);
		//
		//audioIn = new FileIn("res/7clap.wav");
		audioIn= new PcMicrophoneIn();
		//
        t = new Thread(this);
        t.start();
		audioIn.start();
	}

	public void stop(){
		canRun = false;
		audioIn.stop();

        try {
            t.join();
        } catch ( Exception e){
            t = null;
        }
	}

	public boolean isRunning(){
		return running.get() == true;
	}
}
