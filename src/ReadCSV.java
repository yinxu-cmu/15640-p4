import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadCSV {

	public ReadCSV(String inputFile, char option) {
		this.inputFile = inputFile;
		this.option = option;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public ArrayList read() throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		String line = "";
		String[] values = null;
		ArrayList retList = new ArrayList();

		if (this.option == 'p') {
			while ((line = br.readLine()) != null) {
				values = line.split(",");
				Point point = new Point(Double.parseDouble(values[0]),
						Double.parseDouble(values[1]));
				retList.add(point);
			}
		} else if (this.option == 'd') {
			while ((line = br.readLine()) != null) {
				values = line.split(",");
				retList.add(values[0]);
			}
		} else 
			System.err.println("wrong option");

		br.close();
		return retList;
	}

	private char option;
	private String inputFile;
}
