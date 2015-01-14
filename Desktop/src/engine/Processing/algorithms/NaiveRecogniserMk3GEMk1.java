// Made by Pedro HC Avelar
// Last edit on 12/01/2015 ~ 20:20

package engine.Processing.algorithms;
import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.util.*;
import java.util.ArrayList;
import org.jtransforms.fft.DoubleFFT_1D;

// Naive Recognizer Mk 3 - Gold Edition Mk 1
public class NaiveRecogniserMk3GEMk1 implements Recogniser
{
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//	ATTRIBUTES	//	ATTRIBUTES	//	ATTRIBUTES	//	ATTRIBUTES	//
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	// The list which will contain the data from the uphill
	private ArrayList<Double> uphillData;
	public Double[] getUpHillData()
	{
		return (Double[]) this.uphillData.toArray();
	}

	// Boolean indicating if the data is currently in an uphill
	private boolean isUphill;
	public boolean getIsUphill()
	{
		return this.isUphill;
	}

	// The maximum amplitude for a frequency in an
	// uphill so that it is considered a 100% count
	private double[] maxFreqAmp;
	public double[] getMaxFreqAmp()
	{
		// TODO return a copy?
		return this.maxFreqAmp;
	}
	public void setMaxFreqAmp(double[] MaxFreqAmp)
	{
		// TODO do an arraycopy?
		this.maxFreqAmp = MaxFreqAmp;
	}


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
		this.isUphill = false;
		this.uphillData = new ArrayList<Double>(64);
		this.maxFreqAmp = new double[4];
		this.maxFreqAmp[0] = 1.0;
		this.maxFreqAmp[1] = 1.0;
		this.maxFreqAmp[2] = 1.0;
		this.maxFreqAmp[3] = 1.0;
		this.threshold = 0.8;
		this.window = 64;
		this.downhillCount = window;
		this.counter = null;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//	CONSTRUCTORS	//	CONSTRUCTORS	//	CONSTRUCTORS	//	CONSTRUCTORS	//
 	//////////////////////////////////////////////////////////////////////////////////////////////////
	// TODO constructors with the rest of the variables

	public NaiveRecogniserMk3GEMk1()
	{
		this.standardSettings();
	}

	public NaiveRecogniserMk3GEMk1(double Threshold)
	{
		this.standardSettings();
		this.threshold = Threshold;
	}
	
	public NaiveRecogniserMk3GEMk1(double Threshold, int Window)
	{
		this.threshold = Threshold;
		this.window = Window;
		this.downhillCount = window;
	}

	public NaiveRecogniserMk3GEMk1(Counter c)
	{
		this.standardSettings();
		this.counter = counter;
	}

	public NaiveRecogniserMk3GEMk1(double Threshold,Counter c)
	{
		this.standardSettings();
		this.threshold = Threshold;
		this.counter = counter;
	}
	
	public NaiveRecogniserMk3GEMk1(double Threshold, int Window, Counter counter)
	{
		this.standardSettings();
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
//				if(this.downhillCount >= this.window)
//				{
//					// Then Increment the counter
//					try
//					{
//						counter.increment(1.0);
//						System.out.println("Count:"+counter.getCount());
//					}
//					catch(Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
				// Start adding the data as if you are uphill, then;
				this.isUphill = true;				
				// It is not in a downhill anymore
				this.downhillCount = 0;
			}
			// If it isn't above the modulus of the threshold
			else
			{
				// Increment the number of downhill frames it has counted without an uphill
				this.downhillCount++;
			}

			// TODO: Invert the ifs and try to make without the boolean variable.

			// If the data is currently in a uphill, add the data into the uphill count
			if(this.isUphill)
			{
				this.uphillData.add(dataToProcess[i]);
			}
			else
			{
				// If it has counted more than the allowance window
				if(this.downhillCount >= this.window)
				{
					// It isn't an uphill anymore
					this.isUphill = false;
					// Process the data inside the ArrayList
					counter.increment(this.CheckFrequencies());
					// Clear the uphillData
					this.uphillData.clear();
				}
			}
		};
	}

	private double CheckFrequencies()
	{
		// Get the list as an array and pass it to a double array double its size so that it
		// can be FFT'ed
		Double[] listAsArray = (Double[]) this.uphillData.toArray();
		double[] fftResults = new double[listAsArray.length];
		for(int i = 0; i<listAsArray.length; i++)
		{
			fftResults[i*2] = (double) listAsArray[i];
			fftResults[i*2+1] = 0;
		}
		// Fast Fourier the shit out of the data and see if any of the things
		DoubleFFT_1D fft = new DoubleFFT_1D(listAsArray.length);
		fft.complexForward(fftResults);
		// TODO Check if the amplitude results of the FFT surpasses the limit values passed on the constructor
		// And make random calculations to define what is the 

		// FIXME Dummy dummy dummy
		return 1.0;
	}

}
