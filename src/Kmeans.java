import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import mpi.*; 

public class Kmeans {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	@SuppressWarnings({"unchecked"})
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		MPI.Init(args);
//		///
//		int myRank;
//		int senderRank;
//		int dest;
//		int tag = 50;
//		int size;
//		
//		Random random = new Random();
//		
//		myRank = MPI.COMM_WORLD.Rank();
//		size = MPI.COMM_WORLD.Size();
//		int[] buf = new int[1];
//		
//		if (myRank != 0) {
//			
//	        MPI.COMM_WORLD.Recv(buf, 0, buf.length, MPI.INT, 0, tag);
//	        System.out.println("rec all");
//	        Thread.sleep(random.nextInt(2000));
//	        buf[0] = myRank;
//	        MPI.COMM_WORLD.Send(buf, 0, 1, MPI.INT,0, 100);
//	        System.out.println("sent from"+myRank);
//	        
//		} else { // master
//			
//			for (senderRank = 1; senderRank < size; senderRank++) {
//				MPI.COMM_WORLD.Send(buf, 0, 1, MPI.INT, senderRank, tag);
//				System.out.println("MASTER sent all");
//			}
//			
//			for (senderRank = 1; senderRank < size; senderRank++) {
//				Status s = MPI.COMM_WORLD.Recv(buf, 0, 1, MPI.INT, MPI.ANY_SOURCE, 100);
//				System.out.println("master received from: " + s.source + buf[0]+ " : ");
//			}
//						
//		}
//		///
		
		int numClusters = 2;
		
		if (args.length > 0) {
			numClusters = Integer.parseInt(args[0]);
		}
		
		
//		System.out.println("java program started");
			
		String outputFileP = "./outputPoints.csv";
		String inputFileP = "./input/data_points.csv";
		ArrayList<Point> points = new ArrayList<Point>();
		
		ReadCSV readCSVP = new ReadCSV(inputFileP, Kmeans.point);
		points = readCSVP.read();
		new ClusterPoints(points, outputFileP, numClusters);

		
		
		///////
		String inputFileD = "./input/dna_strands.csv";
		String outputFileD = "./outputDNA.csv";
		ArrayList<String> dnaStrands = new ArrayList<String>();
		
		ReadCSV readCSVD = new ReadCSV(inputFileD, Kmeans.dna);
		dnaStrands = readCSVD.read();
		new ClusterDNA(dnaStrands, outputFileD, numClusters);
		
//		System.out.println("java program ended");
		
		MPI.Finalize(); 
	}
	
	public static final char point = 'p';
	public static final char dna = 'd';


}
