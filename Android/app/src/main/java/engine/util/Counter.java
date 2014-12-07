package engine.util;

import android.os.Message;

import rory.bain.counter.app.MainActivity;

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
	
	public void increment(double certainty){
		if(certainty > 0.5)
			count++;
		
		this.uncertainty += (1 - certainty)/2; //Updates uncertainty value

        Message m = new Message();
        m.arg1 = 0;
        MainActivity.handler.sendMessage(m);
	}
	
	
	public double getUncertainty(){
		return Math.round(uncertainty*100); //multiply uncertainty by 100 to get whole number value
	}
	
	public int getCount(){
		return count;
	}
}

