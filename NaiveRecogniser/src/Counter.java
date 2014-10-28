
public class Counter {
	private int count;
	private double error;
	
	public Counter(){
		reset();
	}
	
	public void reset(){
		count = 0;
		error = 0;
	}
	
	public void increment(double uncertainty){
	if(uncertainty > 0.5){
		count+=1;
	}
		error += (1 - uncertainty)/2;
	}
	
	public double getUncertainty(){
		return error;
	}
	
	public int getCount(){
		return count;
	}
}
