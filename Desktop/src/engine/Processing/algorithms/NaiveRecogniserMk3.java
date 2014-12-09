// Made by Pedro HC Avelar
// Last edit on 09/12/2014 ~ 20:50

package engine.Processing.algorithms;
import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.util.*;

public class NaiveRecogniserMk3 implements Recogniser
{
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//	ATTRIBUTES	//	ATTRIBUTES	//	ATTRIBUTES	//	ATTRIBUTES	//
	//////////////////////////////////////////////////////////////////////////////////////////////////

	// The threshold that the data needs to pass to be considered a uphill count
	private double threshold;
	public double getThreshold()
	{
		return this.threshold;
	}
	public void setThreshold(double Threshold)
	{
		this.threshold = Threshold;
	}


	// How many downhill doubles it has counted
	private int downhillCount;
	public int getDownhillCount()
	{
		return this.downhillCount;
	}


	// The window to ignore hills after a downhill to see if it is another count
	private int window;
	public int getWindow()
	{
		return this.window;
	}
	public void setWindow(int Window)
	{
		this.window = Window;
	}

	// The counter object to increment to
	private Counter counter;
	public void passCounter(Counter counter)
	{
		this.counter = counter;
	}
	public Counter getCounter()
	{
		return this.counter;
	}

	// Starndard initalization
	private void standardSettings()
	{
		this.threshold = 0.8;
		this.window = 64;
		this.downhillCount = window;
		this.counter = null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//	CONSTRUCTORS	//	CONSTRUCTORS	//	CONSTRUCTORS	//	CONSTRUCTORS	//
 	//////////////////////////////////////////////////////////////////////////////////////////////////

	public NaiveRecogniserMk3()
	{
		this.standardSettings();
	}

	public NaiveRecogniserMk3(double Threshold)
	{
		this.standardSettings();
		this.threshold = Threshold;
	}
	
	public NaiveRecogniserMk3(double Threshold, int Window)
	{
		this.threshold = Threshold;
		this.window = Window;
		this.downhillCount = window;
	}

	public NaiveRecogniserMk3(Counter c)
	{
		this.standardSettings();
		this.counter = counter;
	}

	public NaiveRecogniserMk3(double Threshold,Counter c)
	{
		this.standardSettings();
		this.threshold = Threshold;
		this.counter = counter;
	}
	
	public NaiveRecogniserMk3(double Threshold, int Window, Counter counter)
	{
		this.threshold = Threshold;
		this.window = Window;
		this.downhillCount = window;
		this.counter = counter;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//	METHODS 	//	METHODS 	//	METHODS 	//	METHODS 	//
 	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void process(Data data)
	{
		// Get the data to process
		double[] dataToProcess = data.get();
		// For all the data inside the array
		for(int i = 0; i<dataToProcess.length; i++)
		{
			// If it is above the modulus of the threshold
			if( ( dataToProcess[i] * dataToProcess[i] ) > ( this.threshold * this.threshold ) )
			{
				// If it has counted more than the allowance window
				if(this.downhillCount >= this.window)
				{
					// Then Increment the counter
					try
					{
						counter.increment(1.0);
						System.out.println("Count:"+counter.getCount());
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				// It is not in a downhill anymore
				this.downhillCount = 0;
			}
			// If it isn't above the modulus of the threshold
			else
			{
				// Increment the number of downhill frames it has counted without an uphill
				this.downhillCount++;
			}
		};
	}

}
