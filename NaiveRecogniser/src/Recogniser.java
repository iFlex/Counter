
public class Recogniser implements Runnable {
	private Thread runContext;
	//private Counter count;
	//private Sample model;
	public void run(){
		System.out.println("Running");
	}
	public Recogniser()
	{
		System.out.println("Recogniser running");
		runContext = new Thread(this);
	}
	public boolean start(){
		System.out.println("Recogniser started processor");
		runContext.start();
		return true;
	}
	public boolean stop(){
		return false;
	}
	public boolean feed(){
		return true;
	}
	public boolean setSample(String path){
		return true;
	}
}
