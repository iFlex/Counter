package rory.bain.counter.app.Util;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class LibraryRecord {
	
	private String name; //name of mechanism used i.e. counting pages, counting spokes
	private File icon; //icon of mechanism
	private int accessRate; //count for number of items mechanism is used 
	private String date; //date of when record was created
	private File directory; //mechanism folder
	private File description; //text file with description of mechanism
	private File wavFile; //Wav file
	private boolean directoryExists;// checks if directory is created
	private boolean descriptionExists; //checks if description file is  created
	private static boolean libraryExists; //Checks if library exists, if not it is created.
	private static File library; //Library that stores all mechanisms
	
	
	public LibraryRecord(String name, String icon, String soundPath) throws IOException{
		
		if(!libraryExists){
			library  = new File("library");
			libraryExists = library.mkdir();
		}
		
		/** Creates description content*/
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		this.date = dateFormat.format(date); // sets date to current date and stores as string
		this.name = name;
		this.icon = new File(icon);
		accessRate = 0;
		
		
		/**Creates directory for mechanism containing a description.txt and a wavFile*/
		directory = new File(library,name);
		directoryExists = directory.mkdir();
		description = new File(directory,"description.txt");
		descriptionExists = description.createNewFile();
		
		if(descriptionExists){
			FileWriter w = new FileWriter(description.toString());
			w.write(this.name + '\n' + accessRate + '\n' + this.icon + '\n' + this.date);
			w.close();
		}
		else {
			throw new IOException("Description file not created.");
		}
		
		wavFile = new File(directory,soundPath);
	}
	
	
	/**  Relevant mutator and accessor methods */

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getIcon() {
		return icon.toString();
	}


	public void setIcon(String icon) {
		this.icon = new File(icon);
	}


	public String getDate() {
		return date;
	}
	
	/** increments access rate when a certain mechanism is accessed*/
	public void incrementRate(){
		accessRate += 1;
	}
	
	public int getAccessRate(){
		return accessRate;
		
	}
	
	public File getDirectory(){
		return directory;
	}
	
	public String getDirectoryPath(){
		return directory.toString();
	}
	
	public boolean getdirectoryExists(){
		return directoryExists;
	}
	
	public boolean getdescriptionExists(){
		return descriptionExists;
	}
	
	public File getDescriptionFile(){
		return description;
	}
	
	public String getDescriptionPath(){
		return description.toString();
	}
	
	public File getWavFile(){
		return wavFile;
	}
	
	public String getWavFilePath(){
		return wavFile.toString();
	}
	
	public boolean getLibraryExists(){
		return libraryExists;
	}
	
	public File getLibrary(){
		return library;
	}
	
	/** Deletes a record from the system*/
	public void destroy(){
		if(directory.delete() && description.delete() && wavFile.delete()){
			System.out.println("Record deleted!");
		}
	}
}

	
