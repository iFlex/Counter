import java.util.concurrent.*;

// Gets data from any audio input possible, the Microphone, for example
public abstract class AudioIn implements Runnable
{
	ConcurrentLinkedQueue<Data> inQueue;
	Thread thread;
	

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

