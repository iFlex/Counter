package batchTesting;

import java.util.ArrayList;

public class TestResult {
	private String fileName;
	private String model; // Have
	private long duration; // Have
	private ArrayList<FileResult> fileResult; // Have

	public TestResult(String filename, String model)
	{
		this.fileName = filename;
		this.model = model;
		this.duration = duration;
		fileResult = new ArrayList<FileResult>();
	}
	
	public void addFileResult(FileResult filename){
		fileResult.add(filename);
	}

	public long getNumFilesTested()
	{
		return fileResult.size();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public ArrayList<FileResult> getFileResult() {
		return fileResult;
	}

	public void setFileResult(ArrayList<FileResult> fileResult) {
		this.fileResult = fileResult;
	}

	public long getDuration() {
		return duration;
	}

	public double getGlobalAccuracy()
	{
		double retval = 0;
		for(int i = 0; i<fileResult.size(); i++)
		{
			retval += fileResult.get(i).getAccuracy();
		}
		retval /= fileResult.size();
		return retval;
	}
	
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void printTestResult(){
		System.out.println("===== Results for Test: " + fileName + " =====" + '\n' +
							"Model :" + model + '\n' +
							"Number of Files Tested: " + getNumFilesTested() + '\n'+
							"Total Time Taken: " + duration + '\n' +
							"Overall Test Accuracy: " + getGlobalAccuracy() + '\n');
		for (FileResult f: fileResult){
			f.printFileResult();
		}
	}
}
