import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadCSV {

	public ReadCSV() {

	}

	public ReadCSV(String inputFile, ArrayList<Point> points) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		String line = "";
		String[] values = null;

		while ((line = br.readLine()) != null) {
			values = line.split(",");
			Point point = new Point(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
			points.add(point);
		}
		
		br.close();

	}
}
