import java.util.ArrayList;

public class History {
	ArrayList<HistoryRecord> records = new ArrayList<HistoryRecord>();

	private void loadFromStorage() {
		try {
			ArrayList<String> files = HistoryRecord.allFiles();
			for (int i = 0; i < files.size(); i++) {
				HistoryRecord hr = new HistoryRecord(files.get(i));
				records.add(hr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public History(ArrayList<HistoryRecord> records) {
		this.records = records;
	}

	public History() {
	}

	public void createRecord(String countee, int count) {
		HistoryRecord hr = new HistoryRecord();
		hr.setCountee(countee);
		hr.setCount(count);
		try {
			hr.writeFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteRecord(int index) {
		try {
			HistoryRecord file = records.get(index);
			file.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> allRecords() {
		ArrayList<String> insideRecords = new ArrayList<String>();
		try {
			ArrayList<String> files = HistoryRecord.allFiles();
			for (int i = 0; i < files.size(); i++) {
				HistoryRecord hr = new HistoryRecord();
				hr.readFile(files.get(i).toString());
				insideRecords.add(hr.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return insideRecords;
	}
}
