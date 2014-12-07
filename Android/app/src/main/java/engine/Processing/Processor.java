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

public class Processor implements Runnable
{
	private AudioIn audioIn;
	private Recogniser n;
	private Thread t;
	private AtomicBoolean running;
	private boolean canRun;
	private Counter count;
	
	public Processor(Counter c)
	{
		count = c;
		audioIn = new MicrophoneIn();
		t = new Thread(this);
		running = new AtomicBoolean(false);
		canRun = false;
		n = new NaiveRecogniser((double)4,count);
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
			}
		}
		running.set(false);
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
		return running.get() == true;
	}
}
