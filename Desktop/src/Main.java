
import engine.Processing.Processor;
import engine.util.Counter;

import java.lang.Thread;

public class Main {
	public static String testsPath;
	
	public static void main(String[] args) {
		Main.testsPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String[] parts = Main.testsPath.split("/");
		Main.testsPath = "";
		for( int i=0; i < parts.length-1; ++i )
			Main.testsPath += "/"+parts[i];
		
		//MODES 1. CLI mode 2. BATCH testing
		String mode = "cli";
		if( args.length > 1 )
			mode = args[0];
		
		if( mode.equals("cli"))
		{
			System.out.println("#Counter++ Testing facility: CLI mode");
			CLI c = new CLI();
			c.run();
			System.out.println("#Counter++ exited CLI mode");
			return;
		}
		
		if( mode.equals("batch"))
		{
			System.out.println("#Counter++ Testing facility: BATCH testing mode");
			
			// TODO Auto-generated method stub
			Counter c = new Counter();
			Processor p = new Processor(c);
			double percentageCount = 0.0;
			int correctCount = 7;
			
			//Use file reader to read acutal count from file.txt
			System.out.println();
			p.setModel("./tests/testModels/clap.wav");
			p.setInput("./tests/testInput/7clap.wav");
			long startTime = System.currentTimeMillis();
			p.start();
			while(p.isRunning()){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime);
			p.stop();
			percentageCount = (double)c.getCount()/correctCount*100;
			
			System.out.println(percentageCount+"% accuracy "+((double)duration/1000)+"s ("+c.getCount()+" = "+correctCount+")"); 
		}
	}

}
