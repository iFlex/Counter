package batchTesting;

import engine.Processing.Processor;
import engine.util.Counter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Tester
{
	String listName;
	long listDuration;
	ArrayList<TestResult> testList;

	public Tester()
	{
	}

	// Test the passed argument filepath as a test list descriptor
	public void Test(String filepath)
	{
		// Create a File Reader, argh.
		String line_listfile; // TODO better name
		listName = filepath;
		File testlistfile = new File(filepath); // TODO exception handling
		BufferedReader listfilein = new BufferedReader(new FileReader(testlistfile)); // TODO exception handling
		File testfile;
		BufferedReader testfilein;
		testList = new ArrayList<TestResult>(); // Yay for O(n³) elements
		
		long listStartTime = System.currentTimeMillis();
		do
		{
			// Read the file and create an array of File handles for the Test files
			line_listfile = listfilein.readLine();
			testfile = new File(line_listfile); // TODO exception handling
			testfilein = new BufferedReader(new FileReader(testfile)); // TODO exception handling

			TestResult tr = new TestResult;
			tr.fileName = line_listfile;
			tr.model = testfilein.readline();
			long testStartTime = System.currentTimeMillis();
			// For each file, get the model and test all of the other wav files
			do
			{
				FileResult fr = new FileResult();

				fr.fileName = testfilein.readline();

				Counter c = new Counter();
				Processor p = new Processor(c);
				double percentageCount = 0.0;
				fr.correctCount = parseCorrectCount(tr.model, fr.fileName);

				// Setting the model and file
				p.setModel(model);
				p.setInput(fr.fileName);
				long startTime = System.currentTimeMillis();
				p.blockingRun();
				fr.duration = (System.currentTimeMillis() - startTime);
				fr.actualCount = c.getCount();
				fr.accuracy = 100 * (((double)fr.actualCount-(double)fr.correctCount)/(double)fr.correctCount);
				//(double)c.getCount()/correctCount*100;

				tr.fileResult.add(fr);

			} while(line_listfile != null);

			tr.duration = System.currentTimeMillis() - testStartTime;
			testList.add(tr);

		} while(line_listfile != null);

		listDuration = (System.currentTimeMillis() - startTime);
	}

	// TODO
	//
	/*
========= Test Results for Test List : FILENAME.lst ==========
Success Rate Per Batch: [ PERCENTAGE_BATCH1, PERCENTAGE_BATCH2, etc ] 
Tests Run: NUMBER
Overall Success Rate: NUMBER
Time Taken: TIME [UNIT]

===== Results for Test : FILENAME.tst =====
Model: FILENAME.wav
Files Tested: NUMBER
Total Time Taken: TIME [UNIT]
Global Accuracy: NUMBER%

== Results for file : FILENAME.wav ==
————————————————
Correct = Counted
NUMBER = NUMBER 
Accuracy: NUMBER%
————————————————
Wav Length: TIME [UNIT]
Time Taken: TIME [UNIT]
	 */
	public void generateReport(String filePath)
	{
		fileName; // List file name
		long testsRun = testList.length();
		double[] successRatePerBatch = new double[testsRun]();
		for(int i = 0; i<testsRun; i++)
		{
			successRatePerBatch[i] = testList.get(i).getGlobalAccuracy();
		}
		double overallSuccessRate;
		overallSuccessRate = SUM(sucessRatePerBatch)/testsRun; // PSEUDOCODE
		listduration; // Time Taken
		// TODO the rest
	}

	// TODO parse the filenames to see what is the correct count.
	// This will check if the model name and the target name is the same, if so return the count on the target name, otherwise return zero
	// Use RegEx?
	private int parseCorrectCount(String modelFileName, String targetFileName)
	{
		return 0;
	}

	// This is the descriptor class for a .tst file
	private class TestResult
	{
		String fileName;
		String model; // Have
		long duration; // Have
		ArrayList<FileResult> fileResult; // Have

		public TestResult()
		{
			fileResult = new ArrayList<FileResult>();
		}

		public long getNumFilesTested()
		{
			return fileResult.size();
		}

		public double getGlobalAccuracy()
		{
			double retval = 0;
			for(int i = 0; i<fileResult.size(); i++)
			{
				retval += fileResult.get(i).getAccuracy();
			}
			retval /= fileResult.length();
			return retval;
		}
	}

	// This is the descriptor class for each test run on a wav file
	private class FileResult
	{
		String fileName; // Have
		int correctCount; // Have
		int actualCount; // Have
		double countError; // Necessary?
		double accuracy; // Have
		long waveLength; // TODO
		long duration; // Have
	}
}
