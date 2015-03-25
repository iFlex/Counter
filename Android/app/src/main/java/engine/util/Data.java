/*
* Author: Milorad Liviu Felix
* Sat 6 December 2014 17:15GMT
* Data class is used to convert the data from multiple input representation methods to double array of amplitudes 
*/
package engine.util;
// Gets data from any format and converts it to a common format, AKA double
public class Data
{
	private double[] d;
	private short[] _d;
	public Data(){
		d = null;
	}
	
	//TODO: turn to generic type for function below
    public Data(short[] b,int usableLength){
    	_d = b;
        d = new double[usableLength];
        _d = new short[usableLength];
        for( int i = 0 ; i < usableLength; ++ i ){
            d[i] = b[i];
            d[i] /= 32768;
    	}
    }

	public Data(double[] b,int usableLength){
		d = new double[usableLength];
		_d = new short[usableLength];
		for( int i = 0 ; i < usableLength; ++ i )
		{
			d[i] = b[i];
		   _d[i] = (short)b[i];
		}
	}
	
	public Data(byte[] b,int usableLength){
		d = new double[usableLength];
		_d = new short[usableLength];
		for( int i = 0 ; i < usableLength; ++ i ){
			d[i] = b[i];
			d[i] /= 128;
		}
	}

	public Data(byte[] b,int usableLength,int bytesPerSample, boolean signed, boolean bigEndian){
		
		int length = usableLength / bytesPerSample;
		d = new double[length];
		_d = new short[usableLength];
		long val = 0; 
		long rangeSize = (1<<(bytesPerSample*8)) - 1;
		long signLimit = (1<<(bytesPerSample*8-1));
		long sign = 1;
		
		for( int i = 0; i < b.length; i+=bytesPerSample )
		{
			val = b[i];
			for(int j = 1; j < bytesPerSample; ++j )
			{
				val <<= 8;
				val |= b[i+j];
			}
			sign = 1;
			if( signed && val > signLimit )
				sign = -1;
			if( bigEndian == false ){
				//need to flip number
				long aux = 0;
				while( val != 0 ){
					aux <<= 1;
					aux |= (val&1);
					val >>= 1;
				}
				val = aux;
			}
			d[i/bytesPerSample] = sign*val;//sign*(double)val/rangeSize;
			d[i/bytesPerSample] /= 128;
		}
		
	}

	public void extend (Data od){
		if( od == null )
		{
			System.out.println("Error: You have tried to extend a data object with an empty object");
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
		
		d = dta;
	}
	
	public void set(double[] d){
		this.d = d;
	}
	
	public double[] get(){
		return d;
	}
	
	public byte[] getRaw(){
		byte[] ret = new byte[_d.length*2];
		int idx = 0;
		for(int i=0;i<_d.length;++i){
			ret[idx++]=(byte)(_d[i] & 0x00ff);
			ret[idx++]=(byte)(_d[i] >> 8);
		}
		return ret;
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
