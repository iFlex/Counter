// Gets data from audioIn, puts it into the the data format and passes it to the recognizer
public class Processor implements Runnable
{
	private Data data;
	private Recognizer rec;
	private AudioIn audioIn;

	public Processor()
	{
		this.data = new Data();
		this.rec = new MedianFilteringRecognizer();
		this.audioIn = new AudioIn();
	}	

	public void run()
	{
	}
	public void stop()
	{
	}
}
