public class NaiveRecogniser implements Recogniser
{
	private double threshold; // The threshold that the data needs to pass to be considered a uphill count
	private boolean actualState; // If it is true, means it is actually in a hill.
	// Is actualState actually needed?
	private double[] dataToProcess; // As name
	private int atIndex; // Index where the next double is going to be inserted in
	private int downhillCount; // How many downhill doubles it has counted
	private int window; // The window to ignore hills after a downhill to see if it is another count
	
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

	public NaiveRecogniser()
	{
		this.standardSettings();
	}

	public NaiveRecogniser(double Threshold)
	{
		this.standardSettings();
		this.threshold = Threshold;
	}

	public void process(Data data)
	{
		double[] allData = data.get();
		int allDataIndex = 0;
		// If the length of the input fills the rest of the data array
		while( allData.length >= (this.dataToProcess.length - (this.atIndex + 1)) )
		{
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
			// If it is above the threshold
			if(this.dataToProcess[i] > this.threshold)
			{
				// If it has counted more than the allowance window
				if(this.downhillCount >= this.window)
				{
					c++;
					// TODO FIXME
					try
					{
						main.counter.increment(1.0);
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
				this.downhillCount++;
			}
		}
		this.atIndex = 0;
		return c;
	}
}
