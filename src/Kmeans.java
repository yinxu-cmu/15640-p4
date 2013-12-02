import java.io.IOException;
import java.util.ArrayList;
//import mpi.*; 

public class Kmeans {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	@SuppressWarnings({"unchecked"})
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
//		MPI.Init(args); 
		
		int numClusters = 2;
		
		if (args.length > 0) {
			numClusters = Integer.parseInt(args[0]);
		}
		
		
		System.out.println("java program started");
			
		String outputFileP = "./outputPoints.csv";
		String inputFileP = "./input/data_points.csv";
		ArrayList<Point> points = new ArrayList<Point>();
		
		ReadCSV readCSVP = new ReadCSV(inputFileP, Kmeans.point);
		points = readCSVP.read();
		new ClusterPoints(points, outputFileP, numClusters);

		String inputFileD = "./input/dna_strands.csv";
		String outputFileD = "./outputDNA.csv";
		ArrayList<String> dnaStrands = new ArrayList<String>();
		
		ReadCSV readCSVD = new ReadCSV(inputFileD, Kmeans.dna);
		dnaStrands = readCSVD.read();
		new ClusterDNA(dnaStrands, outputFileD, numClusters);
		
		System.out.println("java program ended");
		
//		MPI.Finalize(); 
	}
	
	public static final char point = 'p';
	public static final char dna = 'd';


}
