package engine.Processing.algorithms;


/*
 * Author: Pedro HC AVelar
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import engine.Processing.Recogniser;
import engine.util.Counter;
import engine.util.Data;
import engine.util.RingBuffer;
import engine.util.RingSum;

public class MLRawRidgeRecogniser extends Recogniser {
	
	//debug
	FileOutputStream dbg;
	FileOutputStream rto;
	FileOutputStream sampl;
	FileOutputStream mic;
	FileOutputStream dd;
	FileOutputStream smp;
	//alternative 
	private RingBuffer buff;
	private int depth;
	private double[][] deepRawModels;
	private double[][] deepBuffers;
	private RingSum bufsm;
	//optimization:
	private int jump;
	private double mean = 0;
	
	
	
// For _processnext() /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	double runnerAvg = 0,theAvg=0;
	double maxDrop = 65535, firstUnder = 0, minAvg = 0; 
	int maxDropPos=0, startTrack=0;
	int tmppos=0,indiecount=0;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public MLRawRidgeRecogniser(Counter c){
		super(c);
		System.out.println("RAW ridge recogniser");
		//debug
		dbg = null;
		try {
			
			dbg   = new FileOutputStream(new File("lagbehinder.txt"));
			rto   = new FileOutputStream(new File("accumulator.txt"));
			sampl = new FileOutputStream(new File("lagbehdelta.txt"));
			mic   = new FileOutputStream(new File("rawmicinput.txt"));
			dd    = new FileOutputStream(new File("zerocrosser.txt"));
			smp   = new FileOutputStream(new File("thesamplesn.txt"));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.depth = 0;
	}
	
	public synchronized void setModel(String name){
		super.setModel(name);
		buff = new RingBuffer(rawModel.length);
		for( int i = 1 ; i < rawModel.length; ++i )
		{
			if(mean < Math.abs(rawModel[i]))
				mean = Math.abs(rawModel[i]);
			//mean += Math.abs(rawModel[i]);
			buff.push(0);
			try {
				smp.write((rawModel[i-1]+"\n").getBytes());
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//mean /= rawModel.length;
		//jump = 0;
		jump = (int)(buff.getCapacity()*0.05);
		indiecount = 0;
		minAvg = mean;
		position = 0;
		runnerAvg = 0;
		System.out.println("Jump factor:"+jump+" mean:"+mean);
		
		System.out.println("Depth:"+this.depth);
		this.updateDeepModels();
	}
	
	public synchronized void setDepth(int depth)
	{
		this.depth = depth;
	}
	
	public synchronized void updateDeepModels()
	{
		this.deepRawModels = new double[this.depth][];
		int divisor = 2;
		for(int i = 0; i<this.depth; i++)
		{
			int actdivisor = divisor;
			for(int j = 0; j<i; j++)
			{
				actdivisor *= divisor;
			}
			this.deepRawModels[i] = new double[(this.rawModel.length)/(actdivisor)];
			for(int j = 0; j<this.deepRawModels[i].length; j++)
			{
				this.deepRawModels[i][j] = 0;
				for(int k = 0; k<actdivisor; k++)
				{
					this.deepRawModels[i][j] += this.rawModel[(j*actdivisor)+k];
				}
				this.deepRawModels[i][j] /= actdivisor;
			}
		}
	}
	
	public synchronized void updateDeepBuffers()
	{
		this.deepBuffers = new double[this.depth][];
		int divisor = 2;
		for(int i = 0; i<this.depth; i++)
		{
			int actdivisor = divisor;
			for(int j = 0; j<i; j++)
			{
				actdivisor *= divisor;
			}
			this.deepBuffers[i] = new double[(this.buff.length())/(actdivisor)];
			for(int j = 0; j<this.deepBuffers[i].length; j++)
			{
				this.deepBuffers[i][j] = 0;
				for(int k = 0; k<actdivisor; k++)
				{
					this.deepBuffers[i][j] += this.buff.get((j*actdivisor)+k);
				}
				this.deepBuffers[i][j] /= actdivisor;
			}
		}
	}
	
	private void _processNext(double a){
		buff.push(a);
		double certain = 0;
		position++;
		//if buffer has reached proper size for comparison then perform fft
		if( buff.length() == buff.getCapacity() )
		{
////////////////////////////////////// NEW //////////////////////////////////////
			this.updateDeepBuffers();
////////////////////////////////////// OLD //////////////////////////////////////
			tmppos++;
////////////////////////////////////// NEW //////////////////////////////////////
			double[] deepaccumulator = new double[this.depth];
			for(int i = 0; i<this.depth; i++)
			{				deepaccumulator[i] = 0;			}
			double[] deepmax = new double[this.depth];
			for(int i = 0; i<this.depth; i++)
			{				deepmax[i] = 0;			}
			double[] deeplhs = new double[this.depth];
			double[] deeprhs = new double[this.depth];
////////////////////////////////////// OLD //////////////////////////////////////
			double accumulator = 0, max = 0,lhs,rhs;
			int i;
			for( i = 0; i < rawModel.length ; i++ )
				if(max < Math.abs(buff.get(i)))
					max = Math.abs(buff.get(i));
////////////////////////////////////// NEW //////////////////////////////////////
			for(int k = 0; k<this.depth; k++)
			{
				for( i = 0; i < this.deepRawModels[k].length ; i++ )
				{
					deepmax[k] = deepmax[k] < Math.abs(this.deepBuffers[k][i]) ? deepmax[k] = Math.abs(buff.get(i)) : deepmax[k];
				}
			}
////////////////////////////////////// OLD //////////////////////////////////////
			for( i = 0; i < rawModel.length ; i++ ){
				lhs = Math.abs( buff.get(i) );
				rhs = Math.abs( rawModel[i] );
				lhs = Math.abs( lhs - rhs );
				accumulator += lhs;
				if( max < lhs )
					max = lhs;
			}
			accumulator /= i;
////////////////////////////////////// NEW //////////////////////////////////////
			for(int k = 0; k<this.depth; k++)
			{
					for( i = 0; i < this.deepRawModels[k].length ; i++ )
					{
						deeplhs[k] = Math.abs( this.deepBuffers[k][i] );
						deeprhs[k] = Math.abs( this.deepRawModels[k][i] );
						deeplhs[k] = Math.abs( deeplhs[k] - deeprhs[k] );
						deepaccumulator[k] += deeplhs[k];
						deepmax[k] = deepmax[k] < deeplhs[k] ? deeplhs[k] : deepmax[k];
					}
				deepaccumulator[k] /= i;
			}
			
			
			// AVERAGING THE ACCUMULATOR
			for(int k = 0; k<this.depth; k++)
			{
				accumulator += deepaccumulator[k];
			}
			accumulator /= (double)(deepaccumulator.length+1);
			
			

//////////////////////////////////////OLD //////////////////////////////////////
			if(runnerAvg == 0 )
				runnerAvg = accumulator;
			else
				runnerAvg   += accumulator;
			theAvg = ( runnerAvg / tmppos);
			// Suggestion: this.runnerAvg = this.runnerAvg == 0 ? accumulator : this.runnerAvg+accumulator;
			
			if( accumulator < theAvg ) { //accumulator < theAvg && accumulator < mean ){
				if(startTrack == 0)
				{
					startTrack = (int) position;//(int) tmppos;
					firstUnder = accumulator;
				}
				//track
				if(accumulator < maxDrop ){
					maxDrop = accumulator;
					maxDropPos = (int) position;//(int) tmppos;
				}
			}
			else if( startTrack != 0 )
			{
				//known issue: microphone input with lower volume than sample does not get recognised
				//V1
				//counter.increment( 0.30 + (1 - maxDrop) );
				//V3
				if( maxDrop < 0.08 )
					counter.increment(  (1 - maxDrop) );
				//counter.increment(maxDrop);
				System.out.println( maxDrop +" certain:"+(1 - maxDrop)+" Count:"+counter.getCount()+" time:"+((double)maxDropPos/44100));
				maxDrop = theAvg;
				maxDropPos = (int) position;//(int) tmppos;
				startTrack = 0;
			}
			try {
				rto.write((accumulator+"\n").getBytes());
				//number of zero crossings in this graph should give away the count
				mic.write((a+"\n").getBytes());
				dd.write((theAvg+"\n").getBytes());//(chk.get()+"\n").getBytes());//( Math.abs(ddlt.getFirst() + ddlt.getLast()) + "\n" ).getBytes() );
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(certain > 0.5 )
			{
				counter.increment(certain);
				System.out.println(certain+" Count:"+counter.getCount()+" time:"+((double)position/44100));
			}
			
			//now optimize
			for( int ji = 0; ji < jump ; ++ ji )
				try {
					buff.pop();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}	
	}
	
	@Override
	public void process(Data data) {
		double[] d = data.get();
		for( int i = 0; i < d.length; ++i)
			_processNext(d[i]);
		if( data.getLength() == 0 ){
			System.out.println("End of data! Ic:"+indiecount);
		}
		//else
			//System.out.println("Pos:"+position);
	}
}
