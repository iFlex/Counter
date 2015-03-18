package batchTesting;

import engine.Processing.Processor;
import engine.util.Counter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Tester {
	String listName;
	long listDuration;
	ArrayList<TestResult> testList;

	public Tester() {
	}

	// Test the passed argument filepath as a test list descriptor
	public void Test(String filepath) throws IOException {
		// Create a File Reader, argh.
		String line_listfile; // TODO better name
		listName = filepath;
		File testlistfile = new File(filepath); // TODO exception handling
		BufferedReader listfilein = new BufferedReader(new FileReader(
				testlistfile)); // TODO exception handling
		File testfile;
		BufferedReader testfilein;
		testList = new ArrayList<TestResult>(); // Yay for O(n�) elements

		long listStartTime = System.currentTimeMillis();

		while((line_listfile = listfilein.readLine()) != null)
		//do OLD
		{ 
			// Read the file and create an array of File handles for the Test
			// files
			//line_listfile = listfilein.readLine(); OLD
			testfile = null;
			testfilein = null;
			try{
				testfile = new File(line_listfile); // TODO exception handling
				testfilein = new BufferedReader(new FileReader(testfile)); // TODO exception handling
			}
			catch(Exception e){
				System.err.println("Error occurred when trying to read the file" + line_listfile);
				continue;
				//break; //OLD
			}
			
			TestResult tr = new TestResult(line_listfile, testfilein.readLine());
			long testStartTime = System.currentTimeMillis();
			// For each file, get the model and test all of the other wav files
			
			String testf;
			while((testf = testfilein.readLine())!=null)	
			//do OLD
			{
				//String testf = testfilein.readLine();	OLD
				//if(testf == null){	OLD
				//	break;	OLD
				//}	OLD
				FileResult fr = new FileResult(		
						testf,
						parseCorrectCount( tr.getModel(), testf ));

				Counter c = new Counter();
				Processor p = new Processor(c);
				// Setting the model and file
				p.setModel(tr.getModel());
				p.setInput(fr.getFileName());
				long startTime = System.currentTimeMillis();
				p.blockingRun();
				System.out.println(c.getCount());
				fr.setDuration(System.currentTimeMillis() - startTime);
				fr.setActualCount(c.getCount());
				
				//TODO Change to the correct statistical function
				if(!(fr.getActualCount() == 0 || fr.getCorrectCount() == 0)){
					if( fr.getActualCount() > fr.getCorrectCount() )
						fr.setAccuracy(100* ((( (double) fr.getCorrectCount()) / (double) fr.getActualCount())));
					else
						fr.setAccuracy(100* ((( (double) fr.getActualCount()) / (double) fr.getCorrectCount())));
				}
				else if (fr.getActualCount() != 0 && fr.getCorrectCount() == 0){
					fr.setAccuracy(((double) 1.0/(1.0 + (double) fr.getActualCount())));
				
				}
				else{
					fr.setAccuracy(0.0);
				}
				
				// (double)c.getCount()/correctCount*100;
				tr.addFileResult(fr);

			}// while (true); OLD

			tr.setDuration(System.currentTimeMillis() - testStartTime);
			testList.add(tr);

		}// while(line_listfile != null) ; OLD

		listDuration = (System.currentTimeMillis() - listStartTime);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hhmmss");
		Date date = new Date();
		generateReport("./tests/results/"+dateFormat.format(date));
		listfilein.close();
	}


	/*
	 * ========= Test Results for Test List : FILENAME.lst ========== Success
	 * Rate Per Batch: [ PERCENTAGE_BATCH1, PERCENTAGE_BATCH2, etc ] Tests Run:
	 * NUMBER Overall Success Rate: NUMBER Time Taken: TIME [UNIT]
	 * 
	 * ===== Results for Test : FILENAME.tst ===== Model: FILENAME.wav Files
	 * Tested: NUMBER Total Time Taken: TIME [UNIT] Global Accuracy: NUMBER%
	 * 
	 * == Results for file : FILENAME.wav == ���������������� Correct = Counted
	 * NUMBER = NUMBER Accuracy: NUMBER% ���������������� Wav Length: TIME
	 * [UNIT] Time Taken: TIME [UNIT]
	 */
	public void generateReport(String filePath)
	{
		FileOutputStream fw = null;
		try {
			 fw = new FileOutputStream(new File(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		double overallSuccessRate = 0;
		try {
			fw.write(("========= Test Results for Test List: " + listName + " =========" + '\n'+"Success Rate per Batch: \n").getBytes());
			
			
			for(int i = 0; i<testList.size(); i++)
			{
				fw.write((testList.get(i).getFileName() + " - " + testList.get(i).getGlobalAccuracy()+"%" + ", ").getBytes());
				overallSuccessRate += testList.get(i).getGlobalAccuracy();
			}
			
			overallSuccessRate = overallSuccessRate/testList.size();
			
			fw.write(('\n' + "Tests Run: " + testList.size() + '\n' 
					  + "Overall Success Rate: " + overallSuccessRate + "%" + '\n' +
								"Time Taken: " + listDuration/1000.000 +"s" +  '\n' +'\n' + '\n').getBytes()); //Changed duration from ms to s
			for (int j = 0; j < testList.size();j++)
				fw.write(testList.get(j).getTestReport().getBytes());
			
			fw.close();
			System.out.println("BatchTester:Test report generated "+filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("BatchTester:Test report generation failed...");
		}
	}

	private int parseCorrectCount(String modelFileName, String targetFileName) {
		
		if( modelFileName == null || targetFileName == null){
			//TODO: throw some exception instead since this should not happen!
			return 0;
		}
		
		String [] model = modelFileName.split("/");
		String [] target = targetFileName.split("/");
		modelFileName = model[model.length - 1];
		targetFileName = target[target.length - 1];
		
		modelFileName = modelFileName.substring(0,modelFileName.lastIndexOf("."));
		modelFileName = modelFileName.toLowerCase();
		
		
		boolean sameBatch = false;
		//filename_count_version
		String[] parts = targetFileName.split("_");
		parts[0] = parts[0].toLowerCase();
		
		if( modelFileName.equals(parts[0]))
			sameBatch = true;
		
		if(sameBatch && parts.length > 1)
			return Integer.parseInt(parts[1]);
		
		return 0;
	}

	// This is the descriptor class for a .tst file

	// This is the descriptor class for each test run on a wav file

}
