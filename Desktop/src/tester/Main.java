package tester;

import engine.Processing.Processor;
import engine.util.Counter;



public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Counter c = new Counter();
		Processor p = new Processor(c);
		double percentageCount = 0.0;
		int correctCount = 7;
		
		//Use file reader to read acutal count from file.txt
		System.out.println(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		p.setModel("res/testModels/clap.wav");
		p.setInput("res/testInput/7clap.wav");
		long startTime = System.currentTimeMillis();
		p.start();
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		while(duration < 8000){
			endTime = System.currentTimeMillis();
			duration = endTime - startTime;
		}
		p.stop();
		percentageCount = c.getCount()/correctCount*100;
		
		System.out.println("Predicted count: " + c.getCount() + "Actual Count: " + correctCount + '\n' + "Percentage count: " + percentageCount); 
		
		
		
		
		
	}

}
