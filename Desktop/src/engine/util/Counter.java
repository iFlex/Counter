package engine.util;

public class Counter {
	private int count;
	private double uncertainty;
	
	public Counter(){
		reset();
	}
	
	public void reset(){
		count = 0;
		uncertainty = 0;
	}
	//increase count if certainty is above 50%, and add the inverse certainty to total error
	public void increment(double certainty){
		if(certainty > 0.5)
			count++;
		
		this.uncertainty += (1 - certainty)/2; //Updates uncertainty value 
	}
	
	public void setCount(int c){
		count = c;
	}
	
	//multiply certainty by 100 to get percentage
	public double getUncertainty(){
		return Math.round(uncertainty*100);
	}
	
	public int getCount(){
		return count;
	}
}

