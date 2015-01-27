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

import android.util.Log;
import java.io.*;
import android.content.Context;
import rory.bain.counter.app.MainActivity;

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
		running = new AtomicBoolean(false);
		canRun = false;
		n = new NaiveRecogniserMk2((double)15000, 512, count);
        //n = new FFTrecogniser(count);
        audioIn = null;
    }

    public synchronized void setModel(String path){
        n.setModel(path);
    }
    public synchronized void setInput(String nameOrPath){
        canRun = false;
        if( audioIn != null )
        {
            audioIn.stop();
            audioIn = null;
        }

        if( nameOrPath.equals(".../microphone"))
            audioIn = new MicrophoneIn();
        else
            audioIn = new FileIn(nameOrPath);
        //TODO: if audioIn is no initilised properly canRun = false;
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
        if( audioIn == null )
            audioIn = new MicrophoneIn();

        t = new Thread(this);
        t.start();
		audioIn.start();
	}

	public void stop(){
		canRun = false;
		audioIn.stop();
        audioIn = null;
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
