import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Scanner;

import engine.Processing.*;
import engine.Processing.algorithms.*;
import engine.audio.AudioIn;
import engine.audio.FileIn;
import engine.util.Counter;
import engine.util.Data;
import engine.audio.WavFile;
import engine.audio.WavFileException;

public class CLI {

	public Processor processor;
	private Counter count;
	public void run()
	{
		System.out.println("Toggle the recorder");
		count = new Counter();
		processor = new Processor(count);
		processor.setModel("./tests/models/clap.wav");
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
			else if(s.equals("testFromRam")){
				System.out.println("Initialising audio...");
				AudioIn a = new FileIn("./tests/samples/clap_7_0.wav");
				System.out.println("Starting audio_in");
				a.blockingStart();
				System.out.println("AudioIn finished now collecting data...");
				Data d = new Data();
				Data nxt;
				while((nxt = a.getNext()) != null )
				d.extend(nxt);
				a.stop();
				System.out.println("Stopped audio in");
				Counter c = new Counter();
				System.out.println("Starting recogniser");
				RawRidgeRecogniser rrr = new RawRidgeRecogniser(c);
				rrr.setModel("./tests/models/clap.wav");
				System.out.println("Starting recogniser's processing");
				long startTime = System.currentTimeMillis();
				rrr.process(d);
				long endTime = System.currentTimeMillis();
				System.out.println("Done:"+c.getCount()+" time:"+(double)(endTime-startTime)/1000);
			}
			else if(s.equals("copyFile")){
				System.out.println("Initialising audio...");
				AudioIn a = new FileIn("./tests/samples/clap_7_0.wav");
				System.out.println("Starting audio_in");
				a.blockingStart();
				System.out.println("AudioIn finished now collecting data...");
				Data d = new Data();
				Data nxt;
				while((nxt = a.getNext()) != null )
				d.extend(nxt);
				WavFile copy;
				try
				{
					copy = WavFile.newWavFile(new File("./tests/results/copy.wav"), 1, d.getLength(), 16, 44100);
				} catch(Exception e)
				{
					e.printStackTrace();
					return;
				}
				a.stop();
				System.out.println("Stopped audio in");
				System.out.println("Copying the file to the new file");
				try
				{
					copy.writeFrames(d.get(), d.getLength());
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				System.out.println("Copied the file!");
				try
				{
					copy.close();
				} catch(Exception e)
				{
					e.printStackTrace();
				}
				System.out.println("Done!");
			}
			else
				System.out.println("Unknown command '"+s+"'");
			
		}
		System.out.println("Bye!");
	}
}
