// Process the data to put into the counter
package engine.Processing;
import java.io.IOException;
import java.util.LinkedList;

import engine.audio.AudioIn;
import engine.audio.FileIn;
import engine.util.Counter;
import engine.util.Data;
import engine.util.RingBuffer;
// Code with explanatory comments in ./Desktop/src/engine
public abstract class Recogniser
{
	//the counter
	protected Counter counter;
	protected long position;
	protected double[] rawModel;
	protected LinkedList<Long> positions;
	
	private void _init(Counter c){
		counter = c;
		position = 0;
		positions = new LinkedList<Long>();
	}
	
	protected Recogniser(Counter c){
		_init(c);
	}
	
	public Recogniser(Counter c, Data thesample){
		_init(c);
		rawModel = thesample.get();
	}
	
	public void setModel(String name){
		positions.clear();
		position = 0;
		
		Data sample;
		AudioIn SampleIn;
		sample = new Data();
		
		SampleIn = new FileIn(name);
		SampleIn.blockingStart();
		
		Data d = SampleIn.getNext();
		while( d != null ){
			sample.extend(d);
			d = SampleIn.getNext();
		}
		rawModel = sample.get();
		if( rawModel == null )
		{
			System.out.println("Error: could not initialise correctly! Sample is empty");
			return;
		}
	};

	public synchronized void setRawModel(Data m){
		rawModel = m.get();
	}
	
	protected void pushFramePos(long pos){
		positions.add(pos);
	}
	
	public LinkedList<Long> getPositions(){
		return positions;
	}
	
	public abstract void process(Data data);//reset 
}

