package batchTesting;

import engine.Processing.Processor;
import engine.util.Counter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
		testList = new ArrayList<TestResult>(); // Yay for O(n³) elements

		long listStartTime = System.currentTimeMillis();
		do {
			// Read the file and create an array of File handles for the Test
			// files
			line_listfile = listfilein.readLine();
			testfile = new File(line_listfile); // TODO exception handling
			testfilein = new BufferedReader(new FileReader(testfile)); // TODO
																		// exception
																		// handling

			TestResult tr = new TestResult(
					line_listfile, testfilein.readLine());
			long testStartTime = System.currentTimeMillis();
			// For each file, get the model and test all of the other wav files
			do {
				FileResult fr = new FileResult(testfilein.readLine(),
						parseCorrectCount(tr.getModel(), testfilein.readLine()));

				Counter c = new Counter();
				Processor p = new Processor(c);
				double percentageCount = 0.0;

				// Setting the model and file
				p.setModel(tr.getModel());
				p.setInput(fr.getFileName());
				long startTime = System.currentTimeMillis();
				p.blockingRun();
				fr.setDuration(System.currentTimeMillis() - startTime);
				fr.setActualCount(c.getCount());
				fr.setAccuracy(100 * (((double) fr.getActualCount() - (double) fr
						.getCorrectCount()) / (double) fr.getCorrectCount()));
				// (double)c.getCount()/correctCount*100;

				tr.addFileResult(fr);

			} while (testfilein.readLine() != null);

			tr.setDuration(System.currentTimeMillis() - testStartTime);
			testList.add(tr);

		} while (line_listfile != null);

		listDuration = (System.currentTimeMillis() - listStartTime);
	}

	// TODO
	//
	/*
	 * ========= Test Results for Test List : FILENAME.lst ========== Success
	 * Rate Per Batch: [ PERCENTAGE_BATCH1, PERCENTAGE_BATCH2, etc ] Tests Run:
	 * NUMBER Overall Success Rate: NUMBER Time Taken: TIME [UNIT]
	 * 
	 * ===== Results for Test : FILENAME.tst ===== Model: FILENAME.wav Files
	 * Tested: NUMBER Total Time Taken: TIME [UNIT] Global Accuracy: NUMBER%
	 * 
	 * == Results for file : FILENAME.wav == ———————————————— Correct = Counted
	 * NUMBER = NUMBER Accuracy: NUMBER% ———————————————— Wav Length: TIME
	 * [UNIT] Time Taken: TIME [UNIT]
	 */
	public void generateReport(String filePath)
	{
		double overallSuccessRate = 0;
		System.out.println("========= Test Results for Test List: " + listName + " =========" + '\n'
							+"Success Rate per Batch: ");
		
		for(int i = 0; i<testList.size(); i++)
				{
					System.out.println(testList.get(i).getFileName() + " - " + testList.get(i).getGlobalAccuracy() + ", ");
					overallSuccessRate += testList.get(i).getGlobalAccuracy();
				}
		
		overallSuccessRate = overallSuccessRate/testList.size();
		
		System.out.println("Tests Run: " + testList.size() + '\n' 
							+ "Overall Success Rate: " + overallSuccessRate + '\n' +
							"Time Taken: " + listDuration + '\n');
		for (int j = 0; j < testList.size();j++){
			testList.get(j).printTestResult();
		}
		
		// TODO the rest
	}

	// TODO parse the filenames to see what is the correct count.
	// This will check if the model name and the target name is the same, if so
	// return the count on the target name, otherwise return zero
	// Use RegEx?
	@SuppressWarnings("null")
	private int parseCorrectCount(String modelFileName, String targetFileName) {
		boolean sameBatch = false;
		
		for (int i = 0; i < targetFileName.length(); i++){
			if (targetFileName.substring(i) != "_" && targetFileName.substring(i) == modelFileName.substring(i)){
				sameBatch = true;
			}
			else if (targetFileName.substring(i) == "_"){
				break;
			}
			else{
				sameBatch = false;
			}
		}
		
		if(sameBatch){
			int index = targetFileName.indexOf("_") + 1;
			int lastIndex = targetFileName.lastIndexOf("_");
			return Integer.parseInt(targetFileName.substring(index, lastIndex));
		}
		
		return (Integer) null;
	}

	// This is the descriptor class for a .tst file

	// This is the descriptor class for each test run on a wav file

}
