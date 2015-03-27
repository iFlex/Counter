//Author: Milorad Liviu Felix
package engine.util;
//own implementation of Ring Buffer
public class RingBuffer{
	
	public double[] b;
	public int start,stop,_length,capacity;
	
	public RingBuffer(int length){
		start=stop=_length=0;
		capacity = length;
		b = new double[capacity];
	}
	
	private void normaliseIndexes(){
		if(_length!=0)
		{
			if(start >= 0)
				start %= capacity;
			else
				start = capacity + start;
			
			if( stop >= 0 )
				stop %= capacity;
			else
				stop = capacity + stop;
		}
		
	}
	
	public void push(double d){
		b[stop++] = d;
		
		if( capacity == _length )
			start++;
		else
			_length++;
		
		normaliseIndexes();
	}
	
	public double pop() throws Exception{
		if( _length > 0 )
		{
			double r = b[start];
			start++;
			_length--;
			normaliseIndexes();
			return r;
		}
		throw new Exception("Empty ring buffer");
	}
	
	public double popEnd() throws Exception{
		if( _length > 0 )
		{
			double r = b[stop];
			stop--;
			_length--;
			normaliseIndexes();
			return r;
	
		}
		throw new Exception("Empty ring buffer");
	}
	
	public double getFirst(){
		return b[start];
	}
	
	public double getLast(){
		int pos = stop-1;
		if( pos < 0 )
			pos = capacity + pos;
		return b[pos];
	}
	public double get(int pos){
		pos += start;
		pos %= capacity;
		return b[pos];
	}
	public int getCapacity(){
		return capacity;
	}
	
	public int length(){
		return _length;
	}
	@Override
	public String toString(){
		String str = "";
		int len = _length;
		for( int i = start ; len > 0 ; ++i,len-- )
		{
			i%=capacity;
			str+= " "+b[i];
		}
		return str;
	}
}
