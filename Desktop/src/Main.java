
import engine.Processing.Processor;
import engine.util.Counter;
import batchTesting.*;

import java.io.IOException;
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
		if( args.length > 0 )
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
			Tester t = new Tester();
			String filename = "./tests/batches/batch_1.list";
			
			if(args.length > 1)
				filename = args[1];
			
			if(args.length > 2)
				t.setAlgorithm( args[2] );
			
			try {
				t.Test(filename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
