package engine.Processing.debug;
import engine.Processing.Recogniser;
import engine.util.Data;

import org.jtransforms.fft.DoubleFFT_1D;

import java.lang.Math.*;
import java.io.FileNotFoundException;
//debug
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

public class micInputOutput implements Recogniser {
	FileOutputStream out;
	int fileIndex = 0;
	int position = 0;
	int limit = 44100;// 1 file = 1 second
	double max,min,mean;
	public synchronized void setModel(String name){
	}
	
	@Override
	public void process(Data data) {
		// TODO Auto-generated method stub	
		double[] d = data.get();
		for( int i = 0 ; i < d.length ; ++ i, ++ position)
		{
			if( position == limit )
			{	
				position = 0;
				fileIndex++;
				mean /= limit;
				try {
				//	out.write(("M:"+max+"\nm:"+min+"\na:"+mean).getBytes());
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if( position == 0 )
			{
				max = -100000;
				min = -max;
				mean = 0;
				try {
					out = new FileOutputStream(new File("./debug/"+fileIndex+"x"+limit+".txt"));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mean += d[i];
			if(max < d[i])
				max = d[i];
			if(min > d[i])
				min = d[i];
			
			try {
				out.write((d[i]+"\n").getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}
	
}
