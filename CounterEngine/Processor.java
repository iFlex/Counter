
public class Processor implements Runnable
{
	private Data data;
	private AudioIn audioIn;
	private Thread t;

	private AtomicBoolean running;

	public Processor()
	{
		//this.rec = new ();
		this.data = new Data();
		this.audioIn = new FileIn("./caca.txt");
		
		t = new Thread(this);
		running = new AtomicBoolean(false);
	}	

	public void run(){
		running.set(true);
		while(true)
		{
			Data d = audioIn.get();
			if(d)
				System.out.println("Data:"+d.toString());
			else
				return;
		}
		running.set(false);
	}

	public void start(){
		t.start();
		audioIn.start();
	}

	public void stop(){
		t.stop();
		audioIn.stop();
	}
	public void isRunning(){
		return running.get() == true;
	}
}
