/*
* Author: Milorad Liviu Felix
*/
package engine.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import rory.bain.counter.app.MainActivity;

public class Counter {
	private int count;
	private double uncertainty;
	private Handler comm = null;

	public Counter(){
	    comm = null;
        reset();
	}

    public Counter(Handler h){
        comm = h;
        reset();
    }

    public void setCommChannel( Handler h ){
        comm = h;
    }

	public void reset(){
		count = 0;
		uncertainty = 0;
        Log.d("Count:","reset");
	}
	
	public void increment(double certainty) {
        if (certainty > 0.5)
            count++;
        Log.d("Count:","increment:"+count);
        this.uncertainty += (1 - certainty) / 2; //Updates uncertainty value
        if (comm != null) {
            Message msg = new Message();
            msg.arg1 = count;
            comm.dispatchMessage(msg);
        }
    }
	
	public double getUncertainty(){
		return Math.round(uncertainty*100); //multiply uncertainty by 100 to get whole number value
	}
	
	public int getCount(){
		return count;
	}
}

