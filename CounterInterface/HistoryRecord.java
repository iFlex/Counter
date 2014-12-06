import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryRecord {
	public static String basePath = "history";
	private Date date = new Date();
	private int count;
	private String countee;
	private BufferedWriter writer;
	private String fileName;
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

	public String getDate() {
		return df.format(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getCountee() {
		return countee;
	}

	public void setCountee(String countee) {
		this.countee = countee;
	}

	public HistoryRecord(String countee, int count, Date date) {
		this.date = date;
		this.count = count;
		this.countee = countee;
	}

	public HistoryRecord(String file) {
		try {
			readFile(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HistoryRecord() {
	}

	public void writeFile() throws IOException {
		try {
			fileName = new SimpleDateFormat("dd MMMM yyyy HH-mm-ss'.txt'")
					.format(new Date());
			File file = new File(basePath, fileName);
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(toString());
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readFile(String file) throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(new FileReader(basePath
				+ "/" + file));
		fileName = file;
		String line = reader.readLine();
		String[] parts = line.split(" ");
		setCountee(parts[0]);
		setCount(Integer.parseInt(parts[1]));
		Date parsed_date = df.parse(parts[2] + " " + parts[3]);
		setDate(parsed_date);
		reader.close();
	}

	public void destroy() {
		try {
			Files.delete(Paths.get(basePath + "/" + fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return getCountee() + " " + getCount() + " " + getDate();
	}

	public static ArrayList<String> allFiles() throws IOException {
		ArrayList<String> files = new ArrayList<String>();
		Files.walk(Paths.get(basePath + "/")).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				files.add(filePath.toString().replace(basePath + "/", ""));
			}
		});
		return files;

	}

}
