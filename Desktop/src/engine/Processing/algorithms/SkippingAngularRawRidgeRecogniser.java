/*
 * Author: Pedro HC Avelar
 * Author: Milorad Liviu Felix
 * 14 Jan 2014  22:00 GMT
 */

package engine.Processing.algorithms;

import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.audio.*;
import engine.util.*;
import java.lang.Math.*;
import java.io.FileNotFoundException;
//debug
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
public class SkippingAngularRawRidgeRecogniser extends Recogniser {
	//debug
	FileOutputStream dbg;
	FileOutputStream rto;
	FileOutputStream sampl;
	FileOutputStream mic;
	FileOutputStream dd;
	FileOutputStream smp;
	FileOutputStream ang;
	//alternative 
	private RingBuffer buff,lagger,ddlt;
	//private RingSum chk;
	int jump = 0; 
	// Angle stuff
	private double[] AngleHeights = new double[3];
	private long[] AngleFrames = new long[3];
	private int numInAngleHeights = 0;
	private boolean beginning = false;
	
	public SkippingAngularRawRidgeRecogniser(Counter c)
	{
		super(c);
		System.out.println("Skipping Angular Raw Ridge Recogniser");
		//debug
		dbg = null;
		try {
			dbg   = new FileOutputStream(new File("lagbehinder.txt"));
			rto   = new FileOutputStream(new File("accumulator.txt"));
			sampl = new FileOutputStream(new File("lagbehdelta.txt"));
			mic   = new FileOutputStream(new File("rawmicinput.txt"));
			dd    = new FileOutputStream(new File("zerocrosser.txt"));
			smp   = new FileOutputStream(new File("thesamplesn.txt"));
			this.ang = new FileOutputStream(new File("angle.txt"));
			
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void setModel(String name){
		super.setModel(name);
		buff = new RingBuffer(rawModel.length);
		lagger = new RingBuffer(rawModel.length);
		ddlt = new RingBuffer(rawModel.length);
		//chk = new RingSum(rawModel.length);
		for( int i = 1 ; i < rawModel.length; ++i )
		{
			buff.push(0);
			try {
				smp.write((rawModel[i-1]+"\n").getBytes());
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		jump = (int)(rawModel.length*0.1);
	}
	
	double runnerAvg = 0,theAvg=0;
	double maxDrop = 65535; int maxDropPos=0, startTrack=0;
	private void _processNext(double a)
	{
		buff.push(a);
		position++;
		double certain = 0;
		
		//if buffer has reached proper size for comparison then perform fft
		if( buff.length() == buff.getCapacity() )
		{
			double accumulator = 0, max = 0,lhs,rhs;
			int i;
			for( i = 0; i < rawModel.length ; i++ )
			{
				lhs =  Math.abs(buff.get(i));//*buff.b[i];
				rhs =  Math.abs(rawModel[i]);//*rawModel[iter];
				lhs =  Math.abs( lhs - rhs ); 
				accumulator += lhs;
				if( max < lhs )
				{
					max = lhs;
				}
			}
			
			accumulator /= i;
			accumulator /= max;
			runnerAvg += accumulator;
			lagger.push(accumulator);
			theAvg = (runnerAvg/position);
			
			// If the angle buffer is full, then see whether we are in an up slope or down slope
			if(this.beginning)
			{
				this.AngleHeights[0] = accumulator;
				this.AngleFrames[0] = position;
			}
			else
			{
				System.err.println("Num In Angle Heights: " + this.numInAngleHeights);
				int up = ((this.numInAngleHeights%2) == 0) ? 1 : -1; // Check if it is going upwards or downwards
				if(this.AngleHeights[this.numInAngleHeights] <= accumulator*up)
				{
					System.err.println("Last position: X:" + this.AngleHeights[this.numInAngleHeights] + " Y:" + this.AngleFrames[this.numInAngleHeights]);
					System.err.println("Current position: X:" + accumulator + " Y:" + position);
					this.AngleHeights[this.numInAngleHeights] = accumulator;
					this.AngleFrames[this.numInAngleHeights] = position;
					System.err.println("Going" + (((this.numInAngleHeights%2) == 0) ? "up" : "down"));
				}
				else
				{
					this.numInAngleHeights++;
					
					if(this.numInAngleHeights < 3)
					{
						this.AngleHeights[this.numInAngleHeights] = accumulator;
						this.AngleFrames[this.numInAngleHeights] = position;
					}
					else
					{
						System.err.println("First point: X:" + this.AngleHeights[0] + " Y:" + this.AngleFrames[0]);
						System.err.println("Second point: X:" + this.AngleHeights[1] + " Y:" + this.AngleFrames[1]);
						System.err.println("Third point: X:" + this.AngleHeights[2] + " Y:" + this.AngleFrames[2]);
						// Process the angle;
						double[] NormalizedAngleFrames = new double[3];
						NormalizedAngleFrames[0] = 0.0; // Is zero because AngleFrames[0] is the start of the x axis
						NormalizedAngleFrames[1] = (this.AngleFrames[1] - this.AngleFrames[0])/(this.AngleFrames[2] - this.AngleFrames[0]); // FIXME Division by zero, should not be happening! 
						NormalizedAngleFrames[2] = 1.0; // Is 1.0 because we're normalizing so that AngleFrames[2] is at point 1.0 of the x axis.
						
						// X is the first point, Y is the second, Z is the third. They also mean the angles of the triangle at that intersection point
						// x is the distance between Z and Y, y between Z and X, z between Y and X
						double x,y,z;
						x = Math.sqrt( Math.pow(this.AngleHeights[1]-this.AngleHeights[2], 2) + Math.pow(NormalizedAngleFrames[1]-NormalizedAngleFrames[2], 2) );
						y = Math.sqrt( Math.pow(this.AngleHeights[0]-this.AngleHeights[2], 2) + Math.pow(NormalizedAngleFrames[0]-NormalizedAngleFrames[2], 2) );
						z = Math.sqrt( Math.pow(this.AngleHeights[0]-this.AngleHeights[1], 2) + Math.pow(NormalizedAngleFrames[0]-NormalizedAngleFrames[1], 2) );
						// cos Y = ((x*x)+(z*z)-(y*y))/(2*x*z)
						double angle = Math.acos( ((x*x)+(z*z)-(y*y))/(2*x*z) );
						try
						{
							this.ang.write((angle+"\n").getBytes());
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						
						this.AngleHeights[0] = this.AngleHeights[2];
						this.AngleFrames[0] = this.AngleFrames[2];
						this.numInAngleHeights = 1;
						this.AngleHeights[this.numInAngleHeights] = accumulator;
						this.AngleFrames[this.numInAngleHeights] = position;
					}
				}
					
				}
				
			
			
			if( accumulator < theAvg )
			{
				if(startTrack == 0)
				{
					startTrack = (int) position;
				}
				if(accumulator < maxDrop )
				{
					maxDrop = accumulator;
					maxDropPos = (int) position;
				}
			} // If the accumulator is bigger than the average
			else
			{
				//problematic: detect if the max drop is low enough
				if( startTrack != 0 && maxDrop <= theAvg * 0.75) //only consider counting if the drop was low enough
				{ 
					//calculate how fast the maximum was reached
					int dist =( buff.getCapacity() - ( maxDropPos - startTrack )); 
					if(dist < 0)
						dist = 1;
					certain = ((double)dist / buff.getCapacity())*1.5;
					//if(certain > 0.5)
						System.out.println("crt:"+certain+" maxDrop:"+maxDrop+" avg:"+theAvg+" dist:"+( maxDropPos - startTrack )+" max:"+buff.getCapacity()+" time:"+((double)position/44100));
				}
				//evaluate
				maxDrop = theAvg;
				maxDropPos = (int) position;
				startTrack = 0;
			}
			try {
				rto.write((accumulator+"\n").getBytes());
				dbg.write((lagger.b[lagger.start]+"\n").getBytes());
				//number of zero crossings in this graph should give away the count
				sampl.write((accumulator - lagger.b[lagger.start]+"\n").getBytes() );
				mic.write((a+"\n").getBytes());
				dd.write((runnerAvg/position+"\n").getBytes());//(chk.get()+"\n").getBytes());//( Math.abs(ddlt.getFirst() + ddlt.getLast()) + "\n" ).getBytes() );
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(certain > 0.5)
			{
				counter.increment(certain);
				System.out.println(certain+" Count:"+counter.getCount()+" time:"+((double)position/44100));
				/*int jump = (int)(buff.getCapacity());
				while(jump-- > 0)
					try {
						buff.pop();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						break;
					}*/
			}
			
			
			for(int j = 0; j < jump; j++)
			{
				try
				{
					buff.pop();
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		} // End if(buffer is full)
		
	} // End _processNext
	
	@Override
	public void process(Data data) {
		double[] d = data.get();
		for( int i = 0; i < d.length; ++i)
			_processNext(d[i]);
		if( data.getLength() == 0 )
			System.out.println("End of data!");
	}
}
