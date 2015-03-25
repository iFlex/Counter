package unitTesting;

import java.util.concurrent.atomic.AtomicBoolean;

import engine.Processing.Processor;
import engine.Processing.Recogniser;
import engine.audio.AudioIn;
import engine.util.Counter;

public class TProcessor extends Processor{

	//Constructor to instantiate new counter
	public TProcessor() {
		super(new Counter());
	}
	
	/**
	 * All getters and setters for protected attributes in Processor class
	 * @return
	 */
	public AudioIn getAudioIn() {
		return audioIn;
	}

	public void setAudioIn(AudioIn audioIn) {
		this.audioIn = audioIn;
	}

	public Recogniser getN() {
		return consumer;
	}

	public void setN(Recogniser n) {
		this.consumer = n;
	}

	public Recogniser getDebug() {
		return debug;
	}

	public void setDebug(Recogniser debug) {
		this.debug = debug;
	}

	public Thread getT() {
		return t;
	}

	public void setT(Thread t) {
		this.t = t;
	}

	public AtomicBoolean getRunning() {
		return running;
	}

	public void setRunning(AtomicBoolean running) {
		this.running = running;
	}

	public boolean isCanRun() {
		return canRun;
	}

	public void setCanRun(boolean canRun) {
		this.canRun = canRun;
	}

	public Counter getCount() {
		return count;
	}

	public void setCount(Counter count) {
		this.count = count;
	}
	
}
