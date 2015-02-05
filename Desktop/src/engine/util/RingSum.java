package engine.util;

public class RingSum{
	
	private RingBuffer b;
	private double sum;
	public RingSum(int length){
		b = new RingBuffer(length);
		sum = 0;
	}
	
	public void push(double d){
		if( b.length() == b.getCapacity() )//buffer is spinning
		{
			double s = 0;
			try {
				s = b.pop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sum -= s;
		}
		b.push(d);
		sum += d;
	}
	
	public double get(){
		return sum;
	}
}
