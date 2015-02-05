/*
 * Author: Milorad Liviu Felix
 * 14 Jan 2014  22:00 GMT
 */
package engine.Processing.algorithms;

import engine.Processing.Processor;

import org.jtransforms.fft.DoubleFFT_1D;

import engine.Processing.Recogniser;
import engine.util.Counter;
import engine.util.Data;
import engine.audio.*;

import java.lang.Math.*;
import java.io.FileNotFoundException;

//debug
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

//
public class FFT_FastRegorniser implements Recogniser {

	// it reads its own data
	private AudioIn SampleIn;
	// Sample data
	private Data sample;
	// the counter
	private Counter counter;
	DoubleFFT_1D fftDo;
	double[] fftdta;
	double[] samplefft;
	// debug
	FileOutputStream dbg;
	FileOutputStream rto;
	FileOutputStream sampl;
	FileOutputStream mic;
	FileOutputStream fftmic;
	public int co = 0;

	/*
	 * private void FFT(){ }
	 */

	public synchronized void setModel(String name) {
		sample = new Data();
		// for now load the sample here
		SampleIn = new FileIn(name);
		SampleIn.blockingStart();
		// 1. get the data
		Data d = SampleIn.getNext();
		while (d != null) {
			sample.extend(d);
			d = SampleIn.getNext();
		}
		// 2. FFT the data
		double[] dta = sample.get();
		if (dta != null) {
			// temp
			samplefft = binit(dta, 10);
			write2file(samplefft, "model", 0);
			/*
			 * fftDo = new DoubleFFT_1D(dta.length);
			samplefft = new double[dta.length * 2];
			fftdta = new double[samplefft.length];
			System.out.println("Sample length:" + dta.length);
			System.arraycopy(dta, 0, samplefft, 0, dta.length);
			fftDo.realForwardFull(samplefft);
			*/
			// beautifyFFT(samplefft);
			// Normalise(samplefft,dta.length);
			
		} else
			System.out.println("Error: could not initialise correctly! Sample is empty");
	}

	public FFT_FastRegorniser(Counter c) {
		System.out.println("FFT ridge recogniser");
		// debug
		dbg = null;
		try {
			dbg = new FileOutputStream(new File("output.txt"));
			rto = new FileOutputStream(new File("ratio.txt"));
			sampl = new FileOutputStream(new File("sample.txt"));
			mic = new FileOutputStream(new File("mic.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		counter = c;
	}
	public int getFrameSize(){
		if(samplefft == null )
			return 0;
		return samplefft.length;
	}
	
	private double[] binit(double[] input, int binsize) {
		int cb = binsize;
		double bin = 0;
		double[] sampl = new double[input.length / binsize + 1];
		System.out.println("binning:" + input.length + " to:"
				+ (input.length / binsize));
		for (int i = 0; i < input.length; ++i) {
			if (cb != 0) {
				bin += input[i];
				cb--;
			}
			if (cb == 0 || i == input.length - 1) {
				bin /= (binsize - cb);
				sampl[i / binsize] = bin;

				cb = binsize;
				bin = 0;
			}
		}
		return sampl;
	}

	int cp = 0;

	@Override
	public void process(Data data) {
		cp++;
		System.out.println("Sample len:" + data.get().length);
		samplefft = binit(data.get(),10);
		write2file(samplefft, "mic", cp);
		// long startTime = System.nanoTime();
		// long endTime = System.nanoTime();
		// long duration = (endTime - startTime);
		// if( co % 1000 == 0)
		// System.out.println("Frame:"+co);
		// if(d.length != 0)
		// System.out.println("dT:"+(double)duration/1000000/d.length+"ms - sampling period:"+((1.0/44100)*1000)+" ms processed:"+d.length+" frames");
		if (samplefft.length == 0)
			System.out.println("END or data");
	}

	private void write2file(double[] data, String name, int index) {
		try {
			fftmic = new FileOutputStream(new File("./debug/_fft/" + (index)
					+ name + ".txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int len = data.length;
		for (int i = 0; i < len; ++i) {
			try {
				fftmic.write((data[i] + "\n").getBytes());
			} catch (IOException e) {
				// TODO Auto-gesnerated catch block
				e.printStackTrace();
			}
		}
		try {
			fftmic.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
