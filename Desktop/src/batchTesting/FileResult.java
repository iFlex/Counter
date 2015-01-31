package batchTesting;

public class FileResult {
	private String filename; // Have
	private int correctCount; // Have
	private int actualCount; // Have
	//private double countError; // Necessary?
	private double accuracy; // Have
	//long waveLength; // TODO
	private long duration; // Have
	
	public FileResult(String filename, int actualCount){
		this.filename = filename;
		this.actualCount = actualCount;
	}
	
	public String getFileName() {
		return filename;
	}
	public void setFileName(String fileName) {
		this.filename = fileName;
	}
	public int getCorrectCount() {
		return correctCount;
	}
	public void setCorrectCount(int correctCount) {
		this.correctCount = correctCount;
	}
	public int getActualCount() {
		return actualCount;
	}
	public void setActualCount(int actualCount) {
		this.actualCount = actualCount;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public void printFileResult(){
		
		System.out.println("== Results for file: " + filename + " ==" + '\n' + 
				"---------------------------" + '\n' + "Correct Count: " + correctCount + '\n' +
				"Actual Count: " + actualCount + '\n' +
				"Accuracy: " + accuracy + '\n' +
				"---------------------------" + 
				"Time taken: " + duration + '\n');
		
		
	}
	
	
}


