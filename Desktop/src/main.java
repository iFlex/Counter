import java.util.Scanner;


public class main {

	public static Counter counter;
	public static Processor processor;

	public static void main(String[] args)
	{
		System.out.println("Toggle the recorder");
		counter = new Counter();
		processor = new Processor();
		Scanner sc = new Scanner(System.in);
		
		while(true){
			String s = sc.nextLine();
			if(s.equals("start"))
				processor.start();
			if(s.equals("stop"))
			{
				processor.stop();
				System.out.println("Count:"+counter.getCount());
			}
			if(s.equals("exit"))
				break;
		}
		System.out.println("Bye!");
	}
}
