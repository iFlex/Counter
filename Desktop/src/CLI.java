import static org.junit.Assert.assertTrue;

import java.util.Scanner;

import engine.Processing.*;
import engine.Processing.algorithms.*;
import engine.util.Counter;
import engine.util.RingSum;

public class CLI {

	public Processor processor;
	private Counter count;
	public void run()
	{
		System.out.println("Toggle the recorder");
		count = new Counter();
		processor = new Processor(count);
		processor.setModel("./tests/models/_clap.wav");
		processor.setInput("./tests/samples/clap_7_0.wav");
		Scanner sc = new Scanner(System.in);
		while(true){
			String s = sc.nextLine();
			
			if(s.equals("blr"))
				processor.blockingRun();
			
			if(s.equals("start") || s.equals("S"))
				processor.start();
			
			else if(s.equals("stop") || s.equals("s"))
			{
				processor.stop();
				System.out.println("Count:"+count.getCount());
			}
			else if(s.equals("reset") || s.equals("r"))
				count.reset();
			
			else if(s.equals("exit"))
				break;
			
			else if(s.equals("setInput") || s.equals("si"))
				processor.setInput(sc.nextLine());
			
			else if(s.equals("setModel") || s.equals("sm"))
				processor.setModel(sc.nextLine());
			else
				System.out.println("Unknown command '"+s+"'");
		}
		System.out.println("Bye!");
	}
}
