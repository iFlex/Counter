package unitTesting;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import engine.Processing.Processor;
import engine.util.Counter;

/**
 * Test harness for Processor.java, testing setModel()(indirectly), setInput(), run(), start(), stop(). 
 * @author James Brown
 * Current code coverage: 
 */
public class TestProcessor {
	
	@Before
	public void setUp() throws Exception {
		//Anything to add?
	}

	@After
	public void tearDown() throws Exception {
		//Anything to add?
	}

	/**
	 * Testing whole process creation and modification.
	 */
	@Test
	public void testRun1() {
	
		TProcessor p = new TProcessor();
		p.start();
		p.stop();
	}
	
	@Test
	public void testRun2() {
	
		//What happens if we start a process twice?
		TProcessor p = new TProcessor();
		p.start();
		p.start();
	}
	
	@Test
	public void testRun3() {
	
		//What happens if we stop a process twice
		TProcessor p = new TProcessor();
		p.start();
		p.stop();
		p.stop();
	}
	
	@Test
	public void testRun4() {
	
		//Can we start and stops threads multiple times
		TProcessor p = new TProcessor();
		
		for(int i = 0; i < 100; i++) {
			p.start();
			p.stop();
		}
	}
	
	
}
