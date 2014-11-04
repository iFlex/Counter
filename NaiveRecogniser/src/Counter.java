
public class Counter {
	private int count;
	private double uncertainty;
	
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
		this.uncertainty += (1 - uncertainty)/2; //Updates uncertainty value 
	}
	
	public int getUncertainty(){
		return uncertainty; //remember you need to return an integer representing the uncertainty not the precentage
	}
	
	public int getCount(){
		return count;
	}
}
