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
    FileOutputStream outputStream;


    public Processor(Counter c)
	{

        String filename = "myfile";
        String string = "Hello world!";
        try {
            outputStream = MainActivity.ctx.openFileOutput(filename, Context.MODE_MULTI_PROCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        count = c;
		audioIn = new MicrophoneIn();
		t = new Thread(this);
		running = new AtomicBoolean(false);
		canRun = false;
		n = new NaiveRecogniserMk2((double)0.2, 512, count);
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
		t.start();
		audioIn.start();
	}

	public void stop(){
		canRun = false;
		audioIn.stop();

        try {
            outputStream.close();
        }catch(Exception e){
            Log.i("BITCH","CLOSE:"+e);
        }
	}

	public boolean isRunning(){
		return running.get() == true;
	}
}
