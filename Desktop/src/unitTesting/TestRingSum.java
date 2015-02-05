package unitTesting;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import engine.util.*;

/**
 * Test harness for Processor.java, testing setModel()(indirectly), setInput(), run(), start(), stop(). 
 * @author James Brown
 * Current code coverage: 
 */
public class TestRingSum {
	
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
	
		RingSum s = new RingSum(3);
		for( int i = 0; i < 120; ++i )
		{
			s.push(0);
			s.push(1);
			s.push(0);
			assertTrue(s.get() == 1);
		}
		
	}	
}
