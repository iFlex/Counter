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
	
	public Data(double[] b){
		d = b;
	}
	
	public Data( byte[] b)
	{
		d = new double[b.length];
		for( int i = 0 ; i < b.length ; ++i )
			d[i] = b[i];
	}

    public Data(int[] b)
    {
        d = new double[b.length];
        for(int i = 0; i<b.length; i++)
        {
            d[i] = b[i];
        }
    }
	
	public Data(byte[] b,String enc,boolean isBigEndian){
		byte val = 0;
		int index = 0;
	    int bytesPerRecord = 1;
        if(enc.equals("PCM16"))
            bytesPerRecord = 2;

	    int crntByte = 0;
		d = new double[(int)( b.length / bytesPerRecord )];

	    for (int i = 0; i < b.length ; ++i) {
            val <<= 8;
            val |= (byte) b[i];
            if(crntByte < bytesPerRecord)
            	crntByte++; 
            else {
                crntByte = 0;
                if(isBigEndian)
                    d[index] = val;
                else {
                    int v = 0;
                    d[index] = this.swapEndianFormat((byte) val);
                    //d[index] =  val;//v;
                }
                index++;
                val = 0;
            }

        }
        d = new double[b.length];
        for(int i =0 ; i < b.length; ++i )
            d[i] = (double) b[i];
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

    // Credit due
    // http://stackoverflow.com/questions/3842828/converting-little-endian-to-big-endian
    private byte swapEndianFormat(byte b) {
        int converted = 0x00;
        converted ^= (b & 0x80) >> 7;
        converted ^= (b & 0x40) >> 5;
        converted ^= (b & 0x20) >> 3;
        converted ^= (b & 0x10) >> 1;
        converted ^= (b & 0x08) << 1;
        converted ^= (b & 0x04) << 3;
        converted ^= (b & 0x02) << 5;
        converted ^= (b & 0x01) << 7;
        return (byte) (converted & 0xFF);
    }
}
