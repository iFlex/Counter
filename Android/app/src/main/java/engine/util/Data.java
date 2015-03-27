/*
* Author: Milorad Liviu Felix
* Sat 6 December 2014 17:15GMT
* Data class is used to convert the data from multiple input representation methods to double array of amplitudes 
*/
package engine.util;
// Code with explanatory comments in ./Desktop/src/engine
public class Data
{
    private double[] d;
    private short[] _d;
    public Data(){
        d = null;
    }
    //form data object from short array of length specified by second parameter
    public Data(short[] b,int usableLength){
        _d = b;
        d = new double[usableLength];
        _d = new short[usableLength];
        for( int i = 0 ; i < usableLength && i < b.length; ++ i ){
            d[i] = b[i];
            d[i] /= 32768;
        }
    }
    //form data object from double array of length specified by second parameter
    public Data(double[] b,int usableLength){
        d = new double[usableLength];
        _d = new short[usableLength];
        for( int i = 0 ; i < usableLength && i < b.length ; ++ i )
        {
            d[i] = b[i];
            _d[i] = (short)b[i];
        }
    }
    //form data object from byte array of length specified by second parameter
    public Data(byte[] b,int usableLength){
        d = new double[usableLength];
        _d = new short[usableLength];
        for( int i = 0 ; i < usableLength && i < b.length ; ++ i ){
            d[i] = b[i];
            d[i] /= 128;
        }
    }

    //concatenate the current data object with another one
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
    //get raw data in doule format
    public double[] get(){
        return d;
    }
    //get raw data in byte format
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
