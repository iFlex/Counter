/*
* Author: Milorad Liviu Felix
* Sat 6 December 2014 17:15GMT
* Data class is used to convert the data from multiple input representation methods to double array of amplitudes 
*/
// Gets data from any format and converts it to a common format, AKA double
public class Data
{
	private double[] d;
	
	public Data(double[] b){
		d = b;
	}
	
	public Data( byte[] b)
	{
		d = new double[b.length];
		for( int i = 0 ; i < b.length ; ++i )
			d[i] = b[i];
	}
	
	public Data(byte[] b,int form){	
		//broken, needs inspecting
		long val = 0; 
		int index = 0;
	    int bytesPerRecord = form;
	    int crntByte = 0;
        
		d = new double[(int)( b.length / bytesPerRecord )];
	    for (int i = 0; i < b.length ; ++i) {
            val <<= 8;
            val |= (byte) b[i];
            if(crntByte < bytesPerRecord)
            	crntByte++; 
            else {
                crntByte = 0;
                d[index++] = val;
                val = 0;
            }
        }
	}
	
	public void set(double d){

	}
	
	public double[] get(){
		return d;
	}
	
	public String toString(){
		String s = "";
		if(d != null)
			for( int i=0; i < d.length; ++i )
				s += " "+d[i];
		return s;
	}
}
