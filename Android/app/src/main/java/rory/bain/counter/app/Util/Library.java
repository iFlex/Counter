package rory.bain.counter.app.Util;
import java.util.ArrayList;


public class Library {
	private ArrayList<LibraryRecord> records;
	
	public Library(){
		records = new ArrayList<LibraryRecord>();
	}
	
	/** Adds a record to the library. If the record is already in the library, an exception is thrown */
	public void addRecord(LibraryRecord record) throws RecordException{
		if(!records.contains(record)){
			records.add(record);
		}
		else {
			throw new RecordException("Record already in Library");
		}
		
		
	}
	
	/** modifies a record according to user choice and recordElement is the replacement element.
	 * If the record is not in the library, then add the record to the library */
	public void modifyRecord(int recordNumber,String choice,String recordElement) throws RecordException{
		if(records.size() > recordNumber){
			if(choice.compareTo("icon") == 0){
				records.get(recordNumber).setIcon(recordElement);
			}
			else if(choice.compareTo("name") == 0){
				records.get(recordNumber).setName(recordElement);
			}
		}
		
	}
	
	/**Delete a record from the library and system. If there no records in the library an exception is thrown  */
	public void deleteRecord(LibraryRecord record) throws RecordException{
		if(records.contains(record)){
			records.remove(record);
			record.destroy();
		}
		else{
			throw new RecordException("Record not found");
		}
		
	}
	
	
	public ArrayList<LibraryRecord> getRecords(){
		return records;
	}
	
	
}
