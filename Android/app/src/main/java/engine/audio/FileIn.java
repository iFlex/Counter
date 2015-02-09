package engine.audio;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import engine.util.Data;

public class FileIn extends AudioIn{
	//FIXME Different
	/*
	private String filePath;
	private WavFile wavFile;
	private int chunkSize = 1000
	 */
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
	
	//FIXME Different
	//Method on Desktop too big to copy and not cause confusion, see the desktop app's folder for its method
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
	
	//FIXME Different
	//The difference is not the println
	/*
	public void run(){ 
		System.out.println("FileIn: running..");
		read();
		try {
			wavFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ready = true;
	
	 */
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
