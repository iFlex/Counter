/*
 * Author: Milorad Liviu Felix
 * 14 Jan 2014  22:00 GMT
 */

package engine.Processing.algorithms;

import android.util.Log;
import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.audio.*;
import engine.util.*;
import rory.bain.counter.app.addActivity;

import org.jtransforms.fft.DoubleFFT_1D;

import java.lang.Math.*;
import java.io.FileNotFoundException;

//debug
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//
public class RawRidgeRecogniser extends Recogniser {
	
	//alternative
	private RingBuffer buff,lagger,ddlt;
	//private RingSum chk;
    ArrayList<Short> output;

	public RawRidgeRecogniser(Counter c){
		super(c);
        output = new ArrayList<Short>(10);
		System.out.println("RAW ridge recogniser");
	}

	public synchronized void setModel(String name){
		super.setModel(name);
		buff = new RingBuffer(rawModel.length);
		lagger = new RingBuffer(rawModel.length);
		ddlt = new RingBuffer(rawModel.length);
		//chk = new RingSum(rawModel.length);
		for( int i = 1 ; i < rawModel.length; ++i )
			buff.push(0);
	}

    @Override
    public synchronized void setRawModel(Data sample){
        rawModel = sample.get();
        buff = new RingBuffer(rawModel.length);
        lagger = new RingBuffer(rawModel.length);
        ddlt = new RingBuffer(rawModel.length);
        //chk = new RingSum(rawModel.length);
        for( int i = 1 ; i < rawModel.length; ++i )
            buff.push(0);
    }

	double runnerAvg = 0,theAvg=0;
	double maxDrop = 65535,lastDrop = 0;
    int maxDropPos=0, startTrack=0,nrDrops = 0,nrUps = 0,dropsTillMin = 0,upsTillMin = 0;

	private void _processNext(double a){
		buff.push(a);
		position++;
		double certain = 0;
		//if buffer has reached proper size for comparison then perform fft
		if( buff.length() == buff.getCapacity() )
		{
			double accumulator = 0, max = 0,lhs,rhs;
			int i;
			for( i = 0; i < rawModel.length ; i++ ){
				lhs =  Math.abs(buff.get(i));//*buff.b[i];
				rhs =  Math.abs(rawModel[i]);//*rawModel[iter];
				lhs =  Math.abs( lhs - rhs ); //accidental mistake yelded interesting result -= in stead of =
				accumulator += lhs;
				
				if( max < lhs )
					max = lhs;
			}
			accumulator /= i;
			accumulator /= max;
			runnerAvg += accumulator;
			lagger.push(accumulator);
			theAvg = (runnerAvg/position);
            output.add((short)(accumulator*25000));
            if( accumulator < theAvg ){
				if(startTrack == 0)
				{
					startTrack = (int) position;
					
				}
				//track
                if( lastDrop > accumulator )//dropping
                    nrDrops ++;
                else
                    nrUps ++;
                lastDrop = accumulator;

				if(accumulator < maxDrop ){
					maxDrop = accumulator;
					maxDropPos = (int) position;
                    dropsTillMin = nrDrops;
				    upsTillMin = nrUps;
                }

            }
			else
			{
				//problematic: detect if the max drop is low enough
				if( startTrack != 0 && maxDrop <= theAvg * 0.75){ //only consider counting if the drop was low enough
					//calculate how fast the maximum was reached
					int dist =( buff.getCapacity() - ( maxDropPos - startTrack )); 
					if(dist < 0)
						dist = 1;

					certain   = (
                            ( (double)dist / buff.getCapacity() ) +
                            ( (double)dropsTillMin / ( dropsTillMin + upsTillMin ) )
                    )/2;

					System.out.println("c:"+certain+
                    " peakDist:"+(((double)dist / buff.getCapacity()))+
                    " du:"+( (double)dropsTillMin / ( dropsTillMin + upsTillMin ) )+
                    " dn:"+dropsTillMin+" up:"+upsTillMin+
                    " maxDrop:"+maxDrop+" avg:"+theAvg+
                    " dist:"+( maxDropPos - startTrack )+" max:"+buff.getCapacity());
				}
				//evaluate
				maxDrop = theAvg;
                maxDropPos = (int) position;
				startTrack = 0;
                nrDrops = 0;
                nrUps = 0;
			}

			if(certain > 0.5)
			{
				counter.increment(certain);
				System.out.println(certain+" Count:"+counter.getCount());
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
            //plot accumulator
		}	
	}
	
	@Override
	public void process(Data data) {
		double[] d = data.get();
        Log.d("RRR:","dl:"+data.get().length);
		for( int i = 0; i < d.length; ++i) {
            _processNext(d[i]);
            if(position % 1000 == 0 ) {
                short[] arr = new short[output.size()];
                for (int j = 0; j < output.size(); ++j)
                    arr[j] = output.get(j);

                addActivity.modelVisuals.updateAudioData(arr);
                Log.d("Plotter:", "plotting after:" + position);
            }
        }
    }
}
