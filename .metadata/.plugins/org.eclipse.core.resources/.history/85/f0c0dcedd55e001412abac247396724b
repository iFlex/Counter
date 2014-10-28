import com.musicg.wave.Wave;
import com.musicg.graphic.GraphicRender;
import com.musicg.wave.extension.Spectrogram;

import java.io.File;
public class mainAudio {

	public static void main(String[] args) {
		Recogniser a = new Recogniser();
		a.start();
		String filename = "./liviu1.wav";		
		try
	      {
	         // Open the wav file specified as the first argument
	         WavFile wavFile = WavFile.openWavFile(new File(filename));

	         // Display information about the wav file
	         wavFile.display();
	         
	         // Get the number of audio channels in the wav file
	         int numChannels = wavFile.getNumChannels();

	         // Create a buffer of 100 frames
	         double[] buffer = new double[100 * numChannels];

	         int framesRead;
	         double min = Double.MAX_VALUE;
	         double max = Double.MIN_VALUE;
	         int nr = 0;
	         do
	         {
	            // Read frames into buffer
	            framesRead = wavFile.readFrames(buffer, 100);
	            nr++;
	            // Loop through frames and look for minimum and maximum value

	            //System.out.println("Line:"+nr);
	            for (int s=0 ; s<framesRead * numChannels ; s++)
	            {
	               if (buffer[s] > max) max = buffer[s];
	               if (buffer[s] < min) min = buffer[s];
	               //System.out.println( buffer[s]);
	            }
	         }
	         while (framesRead != 0);

	         // Output the minimum and maximum value
	         System.out.printf("Min: %f, Max: %f\n", min, max);
	         // Close the wavFile
	         wavFile.close();

	      }
	      catch (Exception e)
	      {
	         System.err.println(e);
	      }
	}

}
