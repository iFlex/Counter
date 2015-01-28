/*package unitTesting;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import engine.util.Data;

*//**
 * Test harness for the Data class. Tests all 4 constructors and extend() (left get/set/toString). 
 * @author James Brown
 * Current code coverage: 
 *//*
public class TestData {	
	
	@Before
	public void setUp() throws Exception {
		//Anything to add? 
	}

	@After
	public void tearDown() throws Exception {
		//Anything to add?
	}
	
	
	/////////////////// Testing Data(double[]) /////////////////////////////
		
	@Test
	public void testDataDouble1() {

		int i = 0;
		//Single conversion
		double[] testData = {5.0};  //Dummy data
		double[] expectedResult = {5.0};
		Data d = new Data(testData);  //Call constructor 
		double test = new Double(d.toString());
		assertEquals(expectedResult[i], test, 0);  
	}
	
	@Test
	public void testDataDouble2() {

		//Normal data
		double[] testData = {11.0, 23.0, 35.0, 43.0, 50.0};  //Dummy data
		double[] expectedResult = {11.0, 23.0, 35.0, 43.0, 50.0};
		Data d = new Data(testData);   //Call Constructor
		double[] test = d.get();  //Extract object into array for comparison
		
		for(int i = 0; i < testData.length; i++) {	
			assertEquals(expectedResult[i], test[i], 0);
		}
	}
	
	@Test
	public void testDataDouble3() {

		//Larger normal data
		double[] testData = {100.0, 1000.0, 10000.0, 100000.0, 1000000.0};  //Dummy data
		double[] expectedResult = {100.0, 1000.0, 10000.0, 100000.0, 1000000.0};
		Data d = new Data(testData);   //Call constructor
		double[] test = d.get();  //Extract object into array for comparison
		
		for(int i = 0; i < testData.length; i++) {	
			assertEquals(expectedResult[i], test[i], 0);
		}
	}
	
	@Test
	public void testDataDouble4() {
 
		//Min and max values.
		double[] testData = {Double.MIN_VALUE, Double.MAX_VALUE};  //Dummy data
		double[] expectedResult = {4.9E-324, 1.7976931348623157E308};
		Data d = new Data(testData);  //Call constructor
		double[] test = d.get();  //Extract object into array for comparison
		
		for(int i = 0; i < testData.length; i++) {	
			assertEquals(expectedResult[i], test[i], 0);
		}
	}
	
	@Test
	public void testDataDouble5() {

		//Try casting integers as doubles
		double[] testData = {(double) 1, (double) 1000000, (double) Integer.MIN_VALUE, (double) Integer.MAX_VALUE};  //Dummy data
		double[] expectedResult = {1.0, 1000000.0, -2.147483648E9, 2.147483647E9};
		Data d  = new Data(testData);  //Call constructor
		double[] test = d.get();  //Extract object into array for comparison
		
		for(int i = 0; i < testData.length; i++) {	
			assertEquals(expectedResult[i], test[i], 0);
		}
	}
	
	@Test
	public void testDataDouble6() {

		int i = 0;
		//The method should ignore negatives - it doesn't! 
		double[] testData = {-1.0};  //Dummy data
		double[] expectedResult = {1.0};
		Data d = new Data(testData);  //Call constructor
		double test = new Double(d.toString());
		assertEquals(expectedResult[i], test, 0);   
	}
	
	//////////////////// End of Data(double[]) tests ///////////////////////////////////////
	
	//////////////////// Testing Data(byte[]) //////////////////////////////////////////////
	
	//REMEMBER TO ADD SETTING OF D!
	
	@Test
	public void testDataByte1() {

		//Normal values
		byte[] testData = {(byte)1, (byte)10, (byte)100, (byte)126};  //Dummy data
		byte[] expectedResult = {(byte)1.0, (byte)10.0, (byte)100.0, (byte)126.0};
		Data d = new Data(testData);  //Call constructor
		double[] test = d.get();  //Extract object into array for comparison
		
		for(int i = 0; i < testData.length; i++) {	
			assertEquals(expectedResult[i], test[i], 0);
		}
	
	}
	
	@Test
	public void testDataByte2() {

		//Min and max byte values
		byte[] testData = {(byte)-128, (byte)0, (byte)127};  //Dummy data
		byte[] expectedResult = {(byte)-128.0, (byte)0.0, (byte)127.0};
		Data d = new Data(testData);  //Call constructor
		double[] test = d.get();  //Extract object into array for comparison
		
		for(int i = 0; i < testData.length; i++) {	
			assertEquals(expectedResult[i], test[i], 0);
		}
	}
	
	@Test
	public void testDataByte3() {

		//These values are out-with the range for a byte value - Java defaults them to farthest away value with-in valid range
		byte[] testData = {(byte)-129, (byte)128};  //Dummy data
		byte[] expectedResult = {(byte)0.0, (byte)0.0};
		Data d = new Data(testData);  //Call constructor
		double[] test = d.get();  //Extract object into array for comparison
		
		for(int i = 0; i < testData.length; i++) {	
			assertEquals(expectedResult[i], test[i], 0);
		}
	}
	
	@Test
	public void testDataByte4() {

		//Very large positive and negative doubles just to see what happens - should default to -1.
		byte[] testData = {(byte)-123456789.0, (byte)123456789.0};  //Dummy data
		byte[] expectedResult = {(byte)0.0, (byte)0.0};
		Data d = new Data(testData);
		double[] test = d.get();  //Extract object into array for comparison
		
		for(int i = 0; i < testData.length; i++) {	
			assertEquals(expectedResult[i], test[i], 0);
		}
	}
	
	//////////////////// End of Data(byte[]) tests ///////////////////////////////////////
	
	//////////////////// Testing Data(byte[], int form) //////////////////////////////////
	
	@Test
	public void testDataByteWithForm1() {

		byte[] testData = {(byte)137, (byte)22, (byte)512, (byte)45, (byte)46, (byte)657, (byte)34, (byte)68};  //Dummy data
		byte[] expectedResult = {(byte)0.0, (byte)0.0, (byte)0.0, (byte)0.0, (byte)0.0, (byte)0.0, (byte)0.0, (byte)0.0};
		int testForm = 1;  //What should this value be?
		Data d = new Data(testData, testForm);  //Call the constructor
		double[] test = d.get();  //Extract object into array for comparison
		
		for(int i = 0; i < testData.length; i++) {	
			assertEquals(expectedResult[i], test[i], 0);
		}
	}

	//////////////////// End of Data(byte[], int form) tests /////////////////////////////
	
	/////////////////// Testing extend(Data od) //////////////////////////////////////////
	
	//Testing to be done!
	
	/////////////////// End of extend(Data od) testing ///////////////////////////////////
}
*/