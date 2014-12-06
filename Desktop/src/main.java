
public class main {

	public static Counter counter;
	public static Processor processor;

	public static void main(String[] args)
	{
		System.out.println("Testing the reading ");
		counter = new Counter();
		processor = new Processor();
		
		processor.start();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		processor.stop();
		System.out.println("Count:"+counter.getCount());
	}
}
