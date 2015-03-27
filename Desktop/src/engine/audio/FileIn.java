package engine.audio;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import engine.util.Data;

public class FileIn extends AudioIn{
	
	private String filePath;
	private WavFile wavFile;
	private int chunkSize = 1000;
	//standard constructor simply opens wav file
	public FileIn(String filePath){
		this.filePath = filePath;
		try {
			wavFile = WavFile.openWavFile(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WavFileException e) {
			e.printStackTrace();
		}
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	//sequential read of file contents
	private void read(){
		System.out.println("FileIn:Blocking read...");
		int numChannels = wavFile.getNumChannels();
		wavFile.display();
		
		double[] buffer = new double[chunkSize * numChannels];
		int framesRead = 0;
		do                                                            
		{                                                          
			// Read frames from file into buffer, convert buffer to Data object and push them to the AudioIn Queue                                
			try {
				framesRead = wavFile.readFrames(buffer, chunkSize);
				//if the recording has multiple channels, only read one of them
				if( numChannels > 1 )
				{
					double[] nbuffer = new double[chunkSize];
					for( int i = 0 ; i < buffer.length; i += numChannels )
						nbuffer[(int)(i/numChannels)] = buffer[i];
					
					push(new Data(nbuffer,framesRead));
				}
				else
					push(new Data(buffer,framesRead));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WavFileException e) {
				e.printStackTrace();
			}             			                          
		} while (framesRead != 0 && canRun);
		System.out.println("FileIn: finished reading file");
	}
	
	@Override
	//in the audio sampler thread simply read file contents, push them to the queue and exit
	public void run(){ 
		System.out.println("FileIn: running..");
		read();
		try {
			wavFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ready = true;
	}
	
}
