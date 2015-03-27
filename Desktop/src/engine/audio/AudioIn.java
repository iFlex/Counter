/*
* Author: JamesBrown
* Sat 6 December 2014 16:30GMT
* Refined: Milorad Liviu Felix
*/
package engine.audio;
import engine.util.Data;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.Thread;
// Impelments framework for Audio Input to be integrated into the engine
// Requires a Thread to sample the audio data quickly and a queue for the Processor to poll
public abstract class AudioIn implements Runnable
{
	private ConcurrentLinkedQueue<Data> inQueue;
	protected Thread thread;
	protected boolean canRun;
	protected boolean noMoreInput;
	public boolean ready = false;
	public boolean valid = true;
	
	//default constructor, disallows running the thread and creates a new queue
	public AudioIn()
	{
		inQueue = new ConcurrentLinkedQueue<Data>();
		canRun = false;
	}
	//runs the audio sampler serially ( rather than in its own thread )
	public void blockingStart(){
		canRun = true;
		run();
	}
	//allow thread to run and start it
	public void start()
	{
		noMoreInput = false;
		if( thread != null)
			stop();
		
		canRun = true;
        thread = new Thread(this);
        thread.start();
	}
	//signal thread to stop and wait for it to exit ( join )
	public void stop()
	{
        canRun = false;
        if( thread != null)
        {
	        try {
	            thread.join();
	        } catch ( Exception e){
	
	        }
	        thread = null;
        }
	}
	//function used by Processor to poll queue
	public Data getNext()
	{
		return inQueue.poll();
	}
	//function used by inheriting class to push to queue
	public void push( Data s ) { inQueue.add(s); }
}

