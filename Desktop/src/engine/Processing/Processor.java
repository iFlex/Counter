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
	protected Counter count;
	protected Recogniser consumer;
	protected AudioIn audioIn;
	////////////////////////////////
	protected Thread t;
	protected AtomicBoolean running;
	protected boolean canRun;
	protected String source;
	////////////////////////////////
	//Initialisation common to all Processor configurations
	void _init(Counter c) {
		count = c;
        running = new AtomicBoolean(false);
        canRun = false;
        audioIn = null;
    }
	//Default Processor configuration: common + FastRidgeRecogniser
	public Processor(Counter c)
	{
		_init(c);
		//consumer = new NaiveRecogniserMk3(count);
		//consumer = new RawRidgeRecogniser(count);
		consumer = new FastRidgeRecogniser(count);
		//consumer = new FFTFastRidgeR(count);
	}
	//Processor configuration with custom recogniser
	public Processor(Counter c, Recogniser r)
    {
        _init(c);
        consumer = r; 
    }
	//set the model to be recognised
	public synchronized void setModel(String path){
		consumer.setModel(path);
	}
	//set input source
	public synchronized void setInput(String nameOrPath){ 
		//cause the audio thread to stop in case it is already running
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
	}
	
	public void run(){
		running.set(true);
		while(canRun && audioIn != null)
		{
			Data d = audioIn.getNext();
			if( d != null )
			    consumer.process(d);
			else if( !source.equalsIgnoreCase(".../microphone") && audioIn.ready == true )
				break;
		}
		running.set(false);
	}
	//threading setup function
	//needs to be callsed before by the start function
	protected void _init(){
		if(running.get() == true)
			stop();
		
		canRun = true;
		if( audioIn == null )
			audioIn= new PcMicrophoneIn();
     }
	//start a thread for the Processor and start Audio sampler
	public void start(){
		_init();
			
		t = new Thread(this);
        t.start();
		audioIn.start();
	}
	//run Processor synchronously and start Audio sampler
    public void blockingRun(){
    	_init();
        audioIn.start();
        run();
        audioIn.stop();
        stop();
    }
    //stop the Processor and the Audio sampler
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
		return running.get();
	}
}
