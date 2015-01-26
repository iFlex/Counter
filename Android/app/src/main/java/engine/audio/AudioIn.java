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
// Gets data from any audio input possible, the Microphone, for example
public abstract class AudioIn implements Runnable
{
	private ConcurrentLinkedQueue<Data> inQueue;
	protected Thread thread;
	protected boolean canRun;
	
	public AudioIn()
	{
		inQueue = new ConcurrentLinkedQueue<Data>();
		canRun = false;
	}

	public void start()
	{
		canRun = true;
        thread = new Thread(this);
        thread.start();
	}
	
	public void stop()
	{
        canRun = false;
        try {
            thread.join();
        } catch ( Exception e){
            thread = null;
        }
	}
	
	public Data getNext()
	{
		return inQueue.poll();
	}

	public void push( Data s ) { inQueue.add(s); }
}

