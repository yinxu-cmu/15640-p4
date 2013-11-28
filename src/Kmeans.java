import java.io.IOException;
import java.util.ArrayList;

public class Kmeans {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	@SuppressWarnings({"unused", "unchecked"})
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String outputFile = "./output.csv";
		int numClusters = 2;
		
		
		String inputFile = "./DataGeneratorScripts/input/data_points.csv";
		ArrayList<Point> points = new ArrayList<Point>();
		
		ReadCSV readCSV = new ReadCSV(inputFile, Kmeans.point);
		points = readCSV.read();
		new ClusterPoints(points, outputFile, numClusters);

//		String inputFile = "./DataGeneratorScripts/input/dna_strands.csv";
//		ArrayList<String> dnaStrands = new ArrayList<String>();
//		
//		ReadCSV readCSV = new ReadCSV(inputFile, Kmeans.dna);
//		dnaStrands = readCSV.read();
//		new ClusterDNA(dnaStrands, outputFile, numClusters);
		
		System.out.println("debug");
	}
	
	public static final char point = 'p';
	public static final char dna = 'd';


}
