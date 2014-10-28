import java.io.File;
import java.io.IOException;


public class Sample {
	private double[] data;
	private int dataLen;
	public Sample(double[] sound){
		data = sound;
	}
	public boolean loads(String path ) throws IOException, WavFileException{
		try{
			WavFile wavfile = WavFile.openWavFile(new File(path));
			
			
			 int numChannels = wavfile.getNumChannels();
			 int framesLen;
			 dataLen = 0;
			 
			 double[] buffer = new double[100 * numChannels];
			 do
	         {
				 // Read frames into buffer
				 framesLen = wavfile.readFrames(buffer, 100);
				 framesLen *= numChannels;
				 // Loop through frames and look for minimum and maximum value
				 for (int s = 0 ; s <  framesLen ; s++)
					 data[dataLen++] = buffer[s];
	            
	         }
	         while (framesLen != 0);

	         // Close the wavFile
	         wavfile.close();
			 return true;
			
		}
		catch (IOException e){
			return false;
		}
		
	}
	
	public boolean compare(Sample b){
		return false;
		//to be implement
	}
}
