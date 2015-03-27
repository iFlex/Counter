// Process the data to put into the counter
package engine.Processing;
import java.io.IOException;
import java.util.LinkedList;

import engine.audio.AudioIn;
import engine.audio.FileIn;
import engine.util.Counter;
import engine.util.Data;
import engine.util.RingBuffer;

public abstract class Recogniser
{
	protected Counter counter;
	protected long position;
	protected double[] rawModel;
	protected LinkedList<Long> positions;
	
	//common configuration for recogniser
	//resets position in sound input
	//sets the counter
	//and creates a new list of positions where matches have happened
	private void _init(Counter c){
		counter = c;
		position = 0;
		positions = new LinkedList<Long>();
	}
	//default recogniser constructor
	protected Recogniser(Counter c){
		_init(c);
	}
	//recogniser with audio model already loaded
	public Recogniser(Counter c, Data thesample){
		_init(c);
		rawModel = thesample.get();
	}
	//set the audio model
	//look for the file at the path specified by the parameter
	//load the file into one large Data object
	//put the raw data values in rawModel
	//reset position in audio data and retrieved indexes for the matches because recognition will restert
	//since new model was set
	public void setModel(String name){
		positions.clear();
		position = 0;
		
		//Sample data
		Data sample;
		//it reads its own data
		AudioIn SampleIn;
		sample = new Data();
		
		SampleIn = new FileIn(name);
		SampleIn.blockingStart();
		
		//1. get the data
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
	//load the audio model from a data object, this function needs to be called before Processing starts
	//because it does not perform any resets
	public synchronized void setRawModel(Data m){
		rawModel = m.get();
	}
	//add a match position
	protected void pushFramePos(long pos){
		positions.add(pos);
	}
	//get positions of matches
	public LinkedList<Long> getPositions(){
		return positions;
	}
	//stub function for the main entry point to the algorithm
	public abstract void process(Data data);
}

