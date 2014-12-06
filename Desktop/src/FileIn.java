import java.io.File;
import java.io.FileReader;
import java.io.IOException;


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
		double[] buffer = new double[100 * numChannels];
		int framesRead = 0;
		do                                                            
			{                                                             
				// Read frames into buffer                                
				try {
					framesRead = wavFile.readFrames(buffer, 100);
					this.push(new Data(buffer));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WavFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}             
					                          
			} while (framesRead != 0);    
		
		
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
