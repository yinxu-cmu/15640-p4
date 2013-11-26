import java.io.IOException;
import java.util.ArrayList;

public class Kmeans {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
//		System.out.println(System.getProperty("user.dir"));
		
		String inputFile = "./DataGeneratorScripts/input/cluster.csv";
		String outputFile = "./output.csv";
		int numClusters = 2;
		ArrayList<Point> points = new ArrayList<Point>();

		new ReadCSV(inputFile, points);
		new Clustering(points, outputFile, numClusters);
		
		System.out.println("debug");
	}

}
