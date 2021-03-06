// Made by Pedro HC Avelar

package engine.Processing.algorithms;
import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.util.*;

public class NaiveRecogniserMk2 extends Recogniser
{
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

	// If it is true, means it is actually in a hill.
 	// Is actualState actually needed?
	private boolean actualState; 

	// As name
	private double[] dataToProcess;
	public double[] getdataToProcess()
	{
		return this.dataToProcess;
	}

	// Index where the next double is going to be inserted in
	private int atIndex;
	public int getAtIndex()
	{
		return this.atIndex;
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
	private Counter count;
    public synchronized void setModel(String name){
    }
	// Starndart initalization
	private void standardSettings()
	{
		this.threshold = 0.8;
		this.actualState = false;
		this.dataToProcess = new double[128];
		this.window = 64;
		this.downhillCount = window;
		this.atIndex = 0;
	}

	public NaiveRecogniserMk2(Counter c)
	{
        super(c);
		this.standardSettings();
	}

	public NaiveRecogniserMk2(double Threshold,Counter c)
	{
        super(c);
		this.standardSettings();
		this.threshold = Threshold;
	}
	
	public NaiveRecogniserMk2(double Threshold, int Window, Counter c)
	{
        super(c);
		this.threshold = Threshold;
		this.actualState = false;
		this.dataToProcess = new double[Window*2];
		this.window = Window;
		this.downhillCount = window;
		this.atIndex = 0;
	}
	
	public void process(Data data)
	{
		
		//System.out.println(":"+data.toString());
		double[] allData = data.get();
		int allDataIndex = 0;
		// If the length of the input fills the rest of the data array
		if( allData.length >= (this.dataToProcess.length - (this.atIndex + 1)) )
		{
			//System.out.println("Entered Condition");
			// While it has not read all the data it got
			while(allDataIndex < allData.length)
			{
				// While it has not read all the data it got and
				// its array is not full.
				while((this.atIndex < this.dataToProcess.length) && (allDataIndex < allData.length))
				{
					this.dataToProcess[this.atIndex] = allData[allDataIndex];
					this.atIndex++; allDataIndex++;
				}
				
				// If its array is full, count and reset the array
				if(this.atIndex == this.dataToProcess.length)
				{
					this.count();
					this.atIndex = 0;
				}
			}
		}
	}

	// This depends on a static variable counter inside the main class Cpp to acess and count.
	// Change it to the main class of the project
	public int count()
	{
		int c = 0;
		// For all the data inside the array
		for(int i = 0; i<this.dataToProcess.length; i++)
		{
            //System.out.print(this.dataToProcess[i] + " ");
			// If it is above the modulus of the threshold
			if( ( this.dataToProcess[i] * this.dataToProcess[i] ) > ( this.threshold * this.threshold ) )
			{
				// If it has counted more than the allowance window
				if(this.downhillCount >= this.window)
				{
					c++;
					// TODO FIXME
					try
					{
                        pushFramePos(position);
						counter.increment(1.0);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				// It is not in a downhill anymore
				this.downhillCount = 0;
			}
			else
			{
                if(this.downhillCount >= this.window)
                    pushFramePos(position);

				this.downhillCount++;
			}
		    position++;
        }
        System.out.println();
		this.atIndex = 0;
		return c;
	}
}
