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

	public Processor()
	{
		audioIn = new FileIn("./res/BOOK_25.wav");
		t = new Thread(this);
		running = new AtomicBoolean(false);
		
		n = new NaiveRecogniser(0.0001);
	}	

	public void run(){
		running.set(true);
		while(true)
		{
			Data d = audioIn.getNext();
			if( d != null )
			{	
				System.out.println("Data:"+d.toString());
				n.process(d);
			}
			//else
				//break;
		}
		//running.set(false);
	}

	public void start(){
		running.set(true);
		t.start();
		audioIn.start();
	}

	public void stop(){
		t.stop();
		audioIn.stop();
	}
	public boolean isRunning(){
		return running.get() == true;
	}
}
