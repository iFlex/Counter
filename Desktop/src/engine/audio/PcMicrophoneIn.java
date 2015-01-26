package engine.audio;
import engine.audio.AudioIn;
import engine.util.Data;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.*;

public class PcMicrophoneIn extends AudioIn{
	TargetDataLine line;
	DataLine.Info info;
	AudioFormat format;//need to initialise this
	int bufferSize = 1000;
	int bytesPerSample = 1;
	public PcMicrophoneIn(){
		//AudioFormat(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian)
		format = new AudioFormat(44100, bytesPerSample*8, 1, true, true);
		info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
		if (!AudioSystem.isLineSupported(info)) {
		    // Handle the error ... 
			System.out.println("AudioSystem not supported! Aborting...");
		}
		// Obtain and open the line.
		try {
		    line = (TargetDataLine) AudioSystem.getLine(info);
		    line.open(format,bufferSize);
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
		byte[] data = new byte[line.getBufferSize()/2];
		line.start();
		System.out.println("Microphone now recording...");
		// Here, stopped is a global boolean set by another thread.
		while (canRun) {
		   // Read the next chunk of data from the TargetDataLine.
		   numBytesRead =  line.read(data, 0, data.length);
		   // Save this chunk of data.
		   //Data(byte[] b,int bytesPerSample, boolean signed, boolean bigEndian)
		   push(new Data(data,numBytesRead,bytesPerSample,true,true));
		}     
		System.out.println("MicrophoneIn exiting...");
		line.stop();
	}
}
