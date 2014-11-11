import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CounterTest {

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
	 * @throws Exception 
	 */
	@Test
	public void normalTests() throws Exception {

		Double epsilon = 0.01;  //Accuracy
		
		double [] i = {0.1, 0.123, 0.123456789, 0.9, 0.99999999999};  //Args
		int [] countResults = {0, 0, 0, 1, 2};  // Expected results for count
		double [] errorResults = {0.0, 0.0, 0.0, 0.0, 0.0};  // (dummy) Expected results for error
		
		// 45.0, 89.0, 133.0, 138.0, 138.0  ACTUAL RESULTS!!
		
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
	 * @throws Exception 
	 */
	@Test
	public void extremeTests() throws Exception {

		Double epsilon = 0.01;  //Accuracy
		
		double [] i = {0.0, 1.0};  //Args
		int [] countResults = {0, 1};  // Expected results for count
		double [] errorResults = {0.0, 0.0};  // (dummy) Expected results for error
		
		//50.0, 50.0    //ACTUAL RESULTS
		
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
	 * @throws Exception 
	 */
	@Test
	public void exceptionalTests() throws Exception {

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
	 * Tests to ensure exceptions are thrown when numbers out-with acceptable range are entered 
	 * @throws Exception 
	 */
	@Test (expected = Exception.class)
	public void errorThrownTest1() throws Exception {
		
		c.increment(-1.0); 
	}
	
	@Test (expected = Exception.class)
	public void errorThrownTest2() throws Exception {
		
		c.increment(-56789.4); 
	}
	
	@Test (expected = Exception.class)
	public void errorThrownTest3() throws Exception {
		
		c.increment(Double.MIN_VALUE);  
	}
	
			@Test
			public void extraTest() throws Exception {
							
				Double epsilon = 0.01;  //Accuracy
				int expectedCount = 0;
				double expectedError = 0.0;   //It computes 50.0 for the error - where the hell does it get this from!?
							
				c.increment(Double.MIN_VALUE);  
							
				Integer cResult = c.getCount();
				Double eResult = c.getUncertainty();
							
				assertEquals(expectedCount, cResult, epsilon);
				assertEquals(expectedError, eResult, epsilon);
			}
	
	@Test (expected = Exception.class)
	public void errorThrownTest4() throws Exception {
		
		c.increment(1.01); 
	}
	
	@Test (expected = Exception.class)
	public void errorThrownTest5() throws Exception {
		
		c.increment(56789.4); 
	}
	
	@Test (expected = Exception.class)
	public void errorThrownTest6() throws Exception {
		
		c.increment(Double.MAX_VALUE); 
	}
	
	
	

}
