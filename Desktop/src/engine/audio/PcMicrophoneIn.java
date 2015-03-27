package engine.audio;
import engine.audio.AudioIn;
import engine.util.Data;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.*;
//Audio Input form Microphone 
public class PcMicrophoneIn extends AudioIn{
	TargetDataLine line;
	DataLine.Info info;
	AudioFormat format;
	int bufferSize = 1000;
	int bytesPerSample = 1;
	//standard constructor sets the audio format and opens line to microphone device 
	public PcMicrophoneIn(){
		format = new AudioFormat(44100, bytesPerSample*8, 1, true, true);
		info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object
		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("AudioSystem not supported! Aborting...");
		}
		// Obtain and open the line.
		try {
		    line = (TargetDataLine) AudioSystem.getLine(info);
		    line.open(format,bufferSize);
		} catch (LineUnavailableException ex) {
			System.out.println("Could not open Microphone device:"+ex);
		}
	}
	//main thread sets up buffers to read data in 
	//starts listening to the device line
	//reads on chunk of audio data, converts it to a Data object
	//and pushes it to the queue
	public void run(){
		ByteArrayOutputStream out  = new ByteArrayOutputStream();
		int numBytesRead;
		byte[] data = new byte[line.getBufferSize()/2];
		line.start();
		System.out.println("Microphone now recording...");
		while (canRun) {
		   numBytesRead =  line.read(data, 0, data.length);
		   push(new Data(data,numBytesRead));
		   //push(new Data(data,numBytesRead,bytesPerSample,true,true));
		}     
		System.out.println("MicrophoneIn exiting...");
		line.stop();
	}
}
