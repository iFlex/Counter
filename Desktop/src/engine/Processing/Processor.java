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
	protected AudioIn audioIn;
	protected Recogniser consumer;
	protected Recogniser debug;
	protected Thread t;
	protected AtomicBoolean running;
	protected boolean canRun;
	protected String source;
	protected Counter count;
	protected Recogniser n;
	
	void _init(Counter c){
        count = c;
        running = new AtomicBoolean(false);
        canRun = false;
        audioIn = null;
    }
	
	public Processor(Counter c)
	{
		count = c;
		//consumer = new NaiveRecogniserMk3(count);
		//consumer = new RawRidgeRecogniser(count);
		//consumer = new FastRidgeRecogniser(count);
		consumer = new FFTFastRidgeR(count);
		
		running = new AtomicBoolean(false);
		canRun = false;
		audioIn = null;
	}
	
	public Processor(Counter c, Recogniser r)
    {
        _init(c);
        n = r;
    }
	
	public synchronized void setModel(String path){
		consumer.setModel(path);
	}
	
	public synchronized void setInput(String nameOrPath){ 
		canRun = false;
		if( audioIn != null )
		{
			audioIn.stop();
			audioIn = null;
		}
		System.out.println("Processor: Setting input "+nameOrPath);
		source = nameOrPath;
		if( nameOrPath.equals(".../microphone"))
			audioIn = new PcMicrophoneIn();
		else
			audioIn = new FileIn(nameOrPath);
		//TODO: if audioIn is no initilised properly canRun = false;
	}
	
	public void run(){
		running.set(true);
		while(canRun && audioIn != null)
		{
			Data d = audioIn.getNext();
			//System.out.println("Got data from audio:"+d);
			if( d != null )
			    consumer.process(d);
			else if( !source.equalsIgnoreCase(".../microphone") && audioIn.ready == true )
				break;
		}
		running.set(false);
	}

	protected void _init(){
		if(running.get() == true)
			stop();
		
		canRun = true;
		if( audioIn == null )
			audioIn= new PcMicrophoneIn();
     }

	public void start(){
		_init();
			
		t = new Thread(this);
        t.start();
		audioIn.start();
	}

     public void blockingRun(){
         _init();
         audioIn.start();
         run();
         audioIn.stop();
         stop();
     }


	public void stop(){
		canRun = false;
		if(audioIn != null)
		{
			audioIn.stop();
			audioIn = null;
		}
		if( t != null)
		{
	        try {
	            t.join();
	        } catch ( Exception e){
	            t = null;
	        }
		}
	}

	public boolean isRunning(){
		return running.get() == true;
	}
}
