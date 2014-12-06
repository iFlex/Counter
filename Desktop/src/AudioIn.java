/*
* Author: JamesBrown
* Sat 6 December 2014 16:30GMT
* 
*/
import java.util.concurrent.*;
import java.lang.Thread;
// Gets data from any audio input possible, the Microphone, for example
public abstract class AudioIn implements Runnable
{
	private ConcurrentLinkedQueue<Data> inQueue;
	private Thread thread;
	
	public AudioIn()
	{
		inQueue = new ConcurrentLinkedQueue<Data>();
		thread = new Thread(this);
	}

	public void start()
	{
		thread.start();
	}
	
	public void stop()
	{
		thread.stop();
	}
	
	public Data getNext()
	{
		return inQueue.poll();
	}
	
	//Stub!
	public void run()
	{
		//To be overwritten in MicIn
	}
	
	public void push(Data s) 
	{
		inQueue.add(s);
	}
}

