package engine.audio;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import engine.util.Data;

public class FileIn extends AudioIn{
	
	private String filePath;
	private WavFile wavFile;
	
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
		System.out.println("Testing!");
		int numChannels = wavFile.getNumChannels();                   
		// Create a buffer of 100 frames                              
		double[] buffer = new double[128 * numChannels];
		int framesRead = 0;
		do                                                            
			{                                                             
				// Read frames into buffer                                
				try {
					framesRead = wavFile.readFrames(buffer, 128);
					push(new Data(buffer,framesRead));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WavFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}             
					                          
			} while (framesRead != 0 && canRun);    
		
		
	}
	
	@Override
	public void run(){ 
		
		read();
		try {
			wavFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
