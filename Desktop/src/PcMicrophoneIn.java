import java.io.ByteArrayOutputStream;

import javax.sound.sampled.*;

public class PcMicrophoneIn extends AudioIn{
	TargetDataLine line;
	DataLine.Info info;
	AudioFormat format;//need to initialise this
	
	public PcMicrophoneIn(){
		
		format = new AudioFormat(44100, 8, 1, true, true);
		
		info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
		if (!AudioSystem.isLineSupported(info)) {
		    // Handle the error ... 
			System.out.println("AudioSystem not supported! Aborting...");
		}
		// Obtain and open the line.
		try {
		    line = (TargetDataLine) AudioSystem.getLine(info);
		    line.open(format);
		} catch (LineUnavailableException ex) {
			    // Handle the error ... 
			System.out.println("Could not open Microphone device:"+ex);
		}
	}
	
	public void run(){
		// Assume that the TargetDataLine, line, has already
		// been obtained and opened.
		ByteArrayOutputStream out  = new ByteArrayOutputStream();
		int numBytesRead;
		byte[] data = new byte[line.getBufferSize() / 5];
		line.start();
		// Here, stopped is a global boolean set by another thread.
		while (canRun) {
		   // Read the next chunk of data from the TargetDataLine.
		   numBytesRead =  line.read(data, 0, data.length);
		   // Save this chunk of data.
		   push(new Data(data));
		}     
		System.out.println("MicrophoneIn exiting...");
		line.stop();
	}
}
