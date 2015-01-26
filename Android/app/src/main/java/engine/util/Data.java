/*
* Author: Milorad Liviu Felix
* Sat 6 December 2014 17:15GMT
* Data class is used to convert the data from multiple input representation methods to double array of amplitudes 
*/
package engine.util;

import java.nio.ByteBuffer;

// Gets data from any format and converts it to a common format, AKA double
public class Data
{
	private double[] d;
	
	public Data(){
		d = null;
	}
    //TODO: turn to generic type for function below
    public Data(short[] b,int usableLength){
        d = new double[usableLength];
        for( int i = 0 ; i < usableLength; ++ i )
            d[i] = b[i];
    }
    public Data(double[] b,int usableLength){
		d = new double[usableLength];
		for( int i = 0 ; i < usableLength; ++ i )
			d[i] = (double)b[i];
	}
	
	public Data(byte[] b,int usableLength){
		d = new double[usableLength];
		for( int i = 0 ; i < usableLength; ++ i )
			d[i] = b[i];
	}
	
	public Data(byte[] b,int usableLength,int bytesPerSample, boolean signed, boolean bigEndian){


		int length = usableLength / bytesPerSample;
		d = new double[length];
        ByteBuffer bb = ByteBuffer.wrap(b);
        for( int i = 0; i < length; ++i )
        {
            if( bytesPerSample == 1 )
                d[i] = bb.getChar();
            if( bytesPerSample == 2 )
                d[i] = bb.getShort();
        }
	}

	public void extend (Data od){
		if( od == null )
		{
			System.out.println("Error: You have tried to extend a data object with an empy object");
			return;
		}
		
		double[] o = od.get();
		
		int dlength = 0;
		if( d != null )
			dlength = d.length;
		
		double[] dta = new double[dlength + o.length];
		
		int i=0;
		for( i = 0 ; i < dlength ; ++ i )
			dta[i] = d[i];
		
		for(int j = 0; j < o.length; ++j)
			dta[i++] = o[j];
		
		//I hope this dereferecing of d does not cause Java Runtime to loose all it's references to that memory location and therefore cause a memory leak 
		d = dta;
	}
	
	public void set(double[] d){
		this.d = d;
	}
	
	public double[] get(){
		return d;
	}
	
	public int getLength(){
		if( d == null )
			return -1;
		
		return d.length;
	}
	
	public String toString(){
		String s = "";
		if(d != null)
			for( int i = 0; i < d.length; ++i )
				s += " "+d[i];
		return s;
	}
}
