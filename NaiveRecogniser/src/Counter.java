
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
		error += (1 - uncertainty)/2;  //Updates error value 
	}
	
	public double getUncertainty(){
		return error; //remember you need to return an integer representing the uncertainty not the precentage
	}
	
	public int getCount(){
		return count;
	}
}
