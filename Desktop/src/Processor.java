/*
* Author: Milorad Liviu Felix & Pedro Avelar
* Sat 6 December 2014 17:49GMT
* Interface for the sound recognition algorithm
*/

import java.util.concurrent.atomic.AtomicBoolean;

public class Processor implements Runnable
{
	private AudioIn audioIn;
	private Recogniser n;
	private Thread t;
	private AtomicBoolean running;
	private boolean canRun;
	
	public Processor()
	{
		audioIn = new PcMicrophoneIn();//FileIn("./res/GUITAR_MUTED_10.wav");
		t = new Thread(this);
		running = new AtomicBoolean(false);
		canRun = false;
		n = new NaiveRecogniser((double)100);//new TresholdDetector(100);
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
		return running.get() == true;
	}
}
