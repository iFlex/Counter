
public class Cpp
{
	public static Counter counter;
	public static Processor processor;

	public static void main(String[] args)
	{
		System.out.println("Testing the reading ");
		counter = new Counter();
		processor = new Processor();
		
		processor.start();
	}
}
