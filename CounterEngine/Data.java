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

	public Data(byte[] b,int form){	
		long long val; 
		int index = 0;
	    byte bytesPerRecord = form;
	    byte crntByte = 0;
        
		d = double[(int)( b.length / bytesPerRecord )];
	    for (int i = 0; i < b.length ; ++i) {
            val <<= 8;
            val |= (byte) b[i];
            if(crntByte < bytesPerRecord)
            	crntByte++; 
            else {
                crntByte = 0;
                b.[index++] = val;
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
		for( int i=0; i < d.length; ++i )
			s += " "+d[i];
		return s;
	}
}
