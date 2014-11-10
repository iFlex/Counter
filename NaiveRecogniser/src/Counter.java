import java.text.DecimalFormat;

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
	
	public void increment(double uncertainty) throws Exception{
			if(!(uncertainty > 1 || uncertainty < 0)){
				if(uncertainty > 0.5){
					count+=1;
				}
				this.uncertainty += (1 - uncertainty)/2; //Updates uncertainty value 
				DecimalFormat numberFormat = new DecimalFormat("#.00");//used to display uncertainty to two decimal places
				this.uncertainty = Double.valueOf(numberFormat.format(this.uncertainty)); //updates uncertainty to two decimal places
			}
			else{
				throw new Exception("Invalid Input Exception");
			}
	}
	
	
	public double getUncertainty(){
		return Math.round(uncertainty*100); //multiply uncertainty by 100 to get whole number value
	}
	
	public int getCount(){
		return count;
	}
}
