import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Class that tests Counter.java - tests are divided into normal/extreme/exceptional
 * @author James Brown
 *
 */
public class counterTest {
	
	protected Counter c;  //Counter object on which tests will be instantiated and run

	@Before
	public void setUp() throws Exception {
		this.c = new Counter();
	}

	@After
	public void tearDown() throws Exception {
		this.c = null;
	}

	/**
	 * Normal test suite - standard data that would be expected from sound
	 */
	@Test
	public void normalTests() {

		Double epsilon = 0.01;  //Accuracy
		
		double [] i = {0.1, 0.123, 0.123456789, 0.9, 0.99999999999};  //Args
		int [] countResults = {1, 2, 3, 4, 5};  // Expected results for count
		double [] errorResults = {0.00, 0.00, 0.00, 0.00, 0.00};  // (dummy) Expected results for error
		
		for (int n = 0; n < i.length; n++) {
		
			c.increment(i[n]);
			
			Integer cResult = c.getCount();
			Double eResult = c.getUncertainty();
			
			assertEquals(countResults[n], cResult, epsilon);  //Check count
			assertEquals(errorResults[n], eResult, epsilon);  //Check error
		}

	}
	
	/**
	 * Extreme test suite - values at boundaries of acceptance
	 */
	@Test
	public void extremeTests() {

		Double epsilon = 0.01;  //Accuracy
		
		double [] i = {0.0, 1.0};  //Args
		int [] countResults = {1, 2};  // Expected results for count
		double [] errorResults = {0.00, 0.00};  // (dummy) Expected results for error
		
		for (int n = 0; n < i.length; n++) {
			
		c.increment(i[n]);
			
		Integer cResult = c.getCount();
		Double eResult = c.getUncertainty();
			
		assertEquals(countResults[n], cResult, epsilon);  //Check count
		assertEquals(errorResults[n], eResult, epsilon);  //(dummy) Check error
		}
	}



	/**
	 * Exceptional test suite - stupid data to break it!
	 */
	@Test
	public void exceptionalTests() {

		Double epsilon = 0.01;  //Accuracy
	
		double [] i = {-1.0, -56789.4, Double.MIN_VALUE, 1.01, 56789.4, Double.MAX_VALUE};  //Args
		int [] countResults = {0, 0, 0, 0, 0, 0};  // Expected results for count
		double [] errorResults = {0.00, 0.00, 0.00, 0.00, 0.00, 0.00};  // (dummy) Expected results for error
	
		for (int n = 0; n < i.length; n++) {
	
			c.increment(i[n]);
		
			Integer cResult = c.getCount();
			Double eResult = c.getUncertainty();
		
			assertEquals(countResults[n], cResult, epsilon);  //Check count
			assertEquals(errorResults[n], eResult, epsilon);  //Check error
		}
	}
	
	/**
	 * Tests to ensure exceptions are thrown
	 */
	@Test
	public void errorThrownTests() {
		
		//Write tests to ensure errors are thrown when they should be - once Hashim adds them...
	}

}





