//Made by Pedro HC Avelar
//Last edit on 09/12/2014 ~ 20:50
package engine.Processing.algorithms;
import engine.Processing.Recogniser;
import engine.util.*;
import android.util.Log;
//Made by Pedro HC Avelar
//Last edit on 09/12/2014 ~ 20:50

public class NaiveRecogniserMk3 extends Recogniser
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
    private void _config(){
        window = (int) ((int)rawModel.length*1.5);
        downhillCount = window;
        double max = rawModel[0];
        for( int i = 1 ; i < rawModel.length; ++i )
            if( max < rawModel[i])
                max = rawModel[i];
        threshold = max*0.8;
        System.out.println("Naive Recogniser MK3 -> Window:"+window+" Threshold:"+threshold);
    }
    public void setModel(String path){
        super.setModel(path);
        _config();
    }
    public void setRawModel(Data d){
        super.setRawModel(d);
        _config();
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

    // Starndard initalization
    private void standardSettings()
    {
        this.threshold = 0.8;
        this.window = 64;
        this.downhillCount = window;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    //	CONSTRUCTORS	//	CONSTRUCTORS	//	CONSTRUCTORS	//	CONSTRUCTORS	//
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public NaiveRecogniserMk3()
    {
        super(new Counter());
        this.standardSettings();
    }

    public NaiveRecogniserMk3(double Threshold)
    {
        super(new Counter());
        this.standardSettings();
        this.threshold = Threshold;
    }

    public NaiveRecogniserMk3(double Threshold, int Window)
    {
        super(new Counter());
        this.standardSettings();
        this.threshold = Threshold;
        this.window = Window;
        this.downhillCount = window;
    }

    public NaiveRecogniserMk3(Counter c)
    {
        super(c);
        this.standardSettings();
    }

    public NaiveRecogniserMk3(double Threshold,Counter c)
    {
        super(c);
        this.standardSettings();
        this.threshold = Threshold;
    }

    public NaiveRecogniserMk3(double Threshold, int Window, Counter counter)
    {
        super(counter);
        this.standardSettings();
        this.threshold = Threshold;
        this.window = Window;
        this.downhillCount = window;
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
                        this.pushFramePos(this.position);
                        System.out.println("Counter:"+counter);
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
                if(this.downhillCount >= this.window)
                {
                    this.pushFramePos(this.position);
                }
                // Increment the number of downhill frames it has counted without an uphill
                this.downhillCount++;
            }
            this.position++;
        };
    }

}
