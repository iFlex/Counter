/*
* Author: JamesBrown
* Sat 6 December 2014 16:30GMT
* 
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
		thread = new Thread(this);
		canRun = false;
	}
	
	/*Added by Milorad Liviu Felix*/
	public void blockingStart(){
		canRun = true;
		this.run();
	}
	/**/
	
	public void start()
	{
		canRun = true;
		thread.start();
	}
	
	public void stop()
	{
		canRun = false;
		System.out.println("Stopping AudioIn");
	}
	
	public Data getNext()
	{
		return inQueue.poll();
	}

	public void push(Data s) 
	{
		inQueue.add(s);
	}
}

