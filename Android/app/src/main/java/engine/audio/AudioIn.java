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
import android.util.Log;
// Code with explanatory comments in ./Desktop/src/engine
public class AudioIn implements Runnable
{
	private ConcurrentLinkedQueue<Data> inQueue;
	protected Thread thread;
	protected boolean canRun;

	public AudioIn()
	{
		inQueue = new ConcurrentLinkedQueue<Data>();
		canRun = false;
	}

    public void run(){
        //default run does nothing to support mock data input
    }
	public void blockingStart(){
		canRun = true;
		run();
	}

    public void start()
	{
		canRun = true;
        thread = new Thread(this);
        thread.start();
	}
    //not used anymore
    public void drainStop(){
        long now  = System.currentTimeMillis();
        int spins =  0;
        Log.d("Drain Stop","starting.");
        while( inQueue.isEmpty() == false )
            spins++;//busy waiting
        Log.d("Drain Stop","stopping. Delta:"+(System.currentTimeMillis() - now )+" ms count:"+spins);
        stop();
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

