import java.util.Scanner;
import engine.Processing.*;
import engine.Processing.algorithms.*;
import engine.util.Counter;

public class MainActivity {

	public static Processor processor;
	private static Counter count;
	public static void main(String[] args)
	{
		System.out.println("Toggle the recorder");
		count = new Counter();
		processor = new Processor(count);
		processor.setInput("./res/7clap.wav");
		processor.setModel("./res/clap.wav");
		
		Scanner sc = new Scanner(System.in);
		while(true){
			String s = sc.nextLine();
			if(s.equals("start"))
				processor.start();
			if(s.equals("stop"))
			{
				processor.stop();
				System.out.println("Count:"+count.getCount());
			}
			if(s.equals("reset"))
				count.reset();
			if(s.equals("exit"))
				break;
			
			if(s.equals("setInput"))
				processor.setInput(sc.nextLine());
			
			if(s.equals("setModel"))
				processor.setModel(sc.nextLine());
		}
		System.out.println("Bye!");
	}
}
