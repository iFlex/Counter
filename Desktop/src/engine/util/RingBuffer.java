package engine.util;

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
	
	private int indx;
	public double getFirst(){
		indx = start;
		return b[indx];
	}
	public double getNext(){
		indx++;
		indx %= capacity;
		return b[indx];
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
