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
	public synchronized void setModel(String name){
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
	
	public NaiveRecogniserMk3GEMk1(double Threshold, double[] MaxFreqAmp, int Window, Counter counter)
	{
		this.standardSettings();
		this.threshold = Threshold;
		this.window = Window;
		this.downhillCount = window;
		this.counter = counter;
		this.maxFreqAmp = MaxFreqAmp.clone();
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
				this.uphillData.add(new Double(dataToProcess[i]));

				// If it has counted more than the allowance window
				if(this.downhillCount >= this.window)
				{
					// It isn't an uphill anymore
					this.isUphill = false;
					// Process the data inside the ArrayList
					if(this.uphillData.size()>0)
					{
						counter.increment(this.CheckFrequencies());
					}
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
		Object[] listAsArray = this.uphillData.toArray();
		this.uphillData.clear();
		double[] fftResults = new double[2*listAsArray.length];
		for(int i = 0; i<listAsArray.length; i++)
		{
			fftResults[i*2] = (double) ( (Double) listAsArray[i]);
			fftResults[i*2+1] = 0;
		}
		// Fast Fourier the shit out of the data and see if any of the things
		DoubleFFT_1D fft = new DoubleFFT_1D(listAsArray.length);
		fft.complexForward(fftResults);
		// Check if the amplitude results of the FFT surpasses the limit values passed on the constructor
		int[] meanCount = new int[this.maxFreqAmp.length];
		double[] freqSpecResult = new double[this.maxFreqAmp.length];
		for(int j = 0; j<meanCount.length; j++)
		{
			meanCount[j] = 0;
			freqSpecResult[j] = 0;
		}
		for(int i = 0, j = 0; i<listAsArray.length && j<meanCount.length; i++)
		{
			if(i>((listAsArray.length)/(j+1)))
			{
				j++;
			}
			freqSpecResult[j] += (fftResults[i*2]*fftResults[i*2])+(fftResults[(i*2)+1]*fftResults[(i*2)+1]);
			meanCount[j]++;
		}
		double difference = 0;
		for(int j = 0; j<meanCount.length; j++)
		{ // FIXME The difference is becoming not a number in a somehow twisted way of java's interpreting
			freqSpecResult[j] = freqSpecResult[j]/meanCount[j];
			System.out.println("FreqSpecResult: " + fftResults[j]);
			System.out.println("Difference: " + difference);
			difference += Math.sqrt(freqSpecResult[j]); //Make random calculations to define what is the
			//difference += freqSpecResult[j]; //Make random calculations to define what is the
		}
		 difference = difference<0 ? 0 : (difference > 1 ? 1 : difference);
		// FIXME Dummy dummy dummy
		System.err.print("Difference: ");
		System.out.println(difference);
		return (1 - difference);
	}

}
