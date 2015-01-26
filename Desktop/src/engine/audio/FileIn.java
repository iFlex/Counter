package engine.audio;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import engine.util.Data;

public class FileIn extends AudioIn{
	
	private String filePath;
	private WavFile wavFile;
	private int chunkSize = 1000;
	public FileIn(String filePath){
		this.filePath = filePath;
		try {
			wavFile = WavFile.openWavFile(new File(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WavFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	private void read(){
		System.out.println("FileIn:Blocking read...");
		int numChannels = wavFile.getNumChannels();
		wavFile.display();
		double[] buffer = new double[chunkSize * numChannels];
		int framesRead = 0;
		do                                                            
			{                                                             
				// Read frames into buffer                                
				try {
					framesRead = wavFile.readFrames(buffer, chunkSize);
					push(new Data(buffer,framesRead));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WavFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}             
					                          
			} while (framesRead != 0 && canRun);
		System.out.println("FileIn: finished reading file");
	}
	
	@Override
	public void run(){ 
		System.out.println("FileIn: running..");
		read();
		try {
			wavFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
