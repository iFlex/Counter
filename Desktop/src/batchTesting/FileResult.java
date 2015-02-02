package batchTesting;

public class FileResult {
	private String filename; // Have
	private int correctCount; // Have
	private int actualCount; // Have
	//private double countError; // Necessary?
	private double accuracy; // Have
	//long waveLength; // TODO
	private long duration; // Have
	
	public FileResult(String filename, int correctCount){
		this.filename = filename;
		this.correctCount = correctCount;
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
	
	public String getFileReport(){
		
		return "== Results for file: " + filename + " ==" + '\n' + 
				"---------------------------" + '\n' + actualCount + " - " + "Actual Count"+ '\n' +
				correctCount + " - " + "Correct Count" + '\n' +
				accuracy + "%" +" Accuracy" +'\n' +
				"---------------------------" + '\n' +
				duration/1000.000 + "s" + " - " + "Time taken" + '\n'; //Changed duration from ms to s
		
	}
	
	
}


