import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import mpi.*;

@SuppressWarnings("unchecked")
public class ClusterDNA {

	public ClusterDNA() {

	}

	@SuppressWarnings("unchecked")
	public ClusterDNA(ArrayList<String> dnaStrands, String outputFile, int numClusters) throws Exception {
		// get the DNA strands length
		DNAlength = dnaStrands.get(0).length();

		// randomly init centroids
		centroids = new String[numClusters];
		int numCentroids = centroids.length;
		for (int i = 0; i < numCentroids; i++)
			centroids[i] = this.generateRandomDNA(DNAlength);

		// start interations
		int numDNAStrands = dnaStrands.size();
		clusters = new ArrayList[numClusters];
		
		int myRank;
		int senderRank;
		int dest;
		int size;

		myRank = MPI.COMM_WORLD.Rank();
		size = MPI.COMM_WORLD.Size();

		for (int i = 0; i < numInterations; i++) {
			for (int j = 0; j < numClusters; j++)
				clusters[j] = new ArrayList<String>();
			
			int numDNAStrandsSlave = numDNAStrands / (size - 1);
			int[] DNA2clusterMaster = new int[numDNAStrands];
			int[] DNA2clusterSlave = new int[numDNAStrandsSlave];
			
			// slave processes here
			if (myRank != 0) {
				// get centroids from master
				MPI.COMM_WORLD.Recv(centroids, 0, numCentroids, MPI.OBJECT, 0, 0);
				
				// calculate the nearest centroid for every point in slave's range
				for (int j = 0; j < numDNAStrandsSlave; j++) {
					DNA2clusterSlave[j] = getNearestCentroid(dnaStrands
							.get(((myRank - 1) * numDNAStrandsSlave) + j));
				}
				
				// send the result back to master
				MPI.COMM_WORLD.Send(DNA2clusterSlave, 0, numDNAStrandsSlave, MPI.INT, 0, 100);
				
				// get a cluster of points from master and recalculate the centroid
				MPI.COMM_WORLD.Recv(oneCluster, 0, 1, MPI.OBJECT, 0, 1);
				oneCentroid[0] = recalculateCentroid(oneCluster[0]);
				MPI.COMM_WORLD.Send(oneCentroid, 0, 1, MPI.OBJECT, 0, 99);

			}
			// master process here
			else {
				// send centroids to every slave
				for (senderRank = 1; senderRank < size; senderRank++) {
					MPI.COMM_WORLD.Send(centroids, 0, numCentroids, MPI.OBJECT, senderRank, 0);
				}
				
				// get clustering result and glue them together
				for (senderRank = 1; senderRank < size; senderRank++) {

					Status status = MPI.COMM_WORLD.Recv(DNA2clusterSlave, 0, numDNAStrandsSlave, MPI.INT,
							MPI.ANY_SOURCE, 100);

					for (int k = 0; k < numDNAStrandsSlave; k++) {
						DNA2clusterMaster[((status.source - 1) * numDNAStrandsSlave) + k] = DNA2clusterSlave[k];
					}

				}
				
				for (int k = 0; k < numDNAStrands; k++) {
					clusters[DNA2clusterMaster[k]].add(dnaStrands.get(k));
				}
				
				// parallel recalculate centroids
				for (senderRank = 1; senderRank < size; senderRank++) {
					oneCluster[0] = clusters[senderRank - 1];
					MPI.COMM_WORLD.Send(oneCluster, 0, 1, MPI.OBJECT, senderRank, 1);
				}
				
				for (senderRank = 1; senderRank < size; senderRank++) {
					Status status = MPI.COMM_WORLD.Recv(oneCentroid, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, 99);
					centroids[status.source - 1] = oneCentroid[0];
				}
			}

//			for (int j = 0; j < numDNAStrands; j++) {
//				clusters[getNearestCentroid(dnaStrands.get(j))].add(dnaStrands.get(j));
//			}

			// recalculate the each centroid
//			for (int j = 0; j < numCentroids; j++) {
//				centroids[j] = recalculateCentroid(clusters[j]);
//			}
		}
		
//		for (String centroid : centroids)
//			System.out.println(centroid);
		
		// write the result into the output file
		FileWriter writer = new FileWriter(outputFile);
		for (int i = 0; i < numClusters; i++) {
			for (String DNAStrand : clusters[i]) {
				writer.append("" + DNAStrand);
				writer.append(',');
				writer.append("" + i);
				writer.append('\n');
				writer.flush();
			}
		}
		writer.close();
	}

	private int getNearestCentroid(String DNA) {
		int index = -1;
		int len = this.centroids.length;
		int minDistance = Integer.MAX_VALUE;
		int distance;

		for (int i = 0; i < len; i++) {
			distance = 0;
			for (int j = 0; j < this.DNAlength; j++) {
				if (this.centroids[i].charAt(j) != DNA.charAt(j))
					distance++;
			}
			if (distance < minDistance) {
				minDistance = distance;
				index = i;
			}
		}

		return index;
	}

	private String recalculateCentroid(ArrayList<String> DNAStrands) {
		
		if (DNAStrands.size() == 0) {
			return generateRandomDNA(DNAlength);
		}
		
		StringBuilder newCentroid = new StringBuilder();
		for (int i = 0; i < this.DNAlength; i++) {

			// get the frequency of each kind of nucleobase at "i" position
			// for all strands in the current cluster
			int[] freqNucleobases = new int[this.numNucleobases];
			for (String DNAStrand : DNAStrands) {
				char nucleobase = DNAStrand.charAt(i);
				switch (nucleobase) {
					case 'A' :
						freqNucleobases[0]++;
						break;
					case 'C' :
						freqNucleobases[1]++;
						break;
					case 'G' :
						freqNucleobases[2]++;
						break;
					case 'T' :
						freqNucleobases[3]++;
						break;
				}
			}

			// get the most populous nucleobase
			int maxFreq = Integer.MIN_VALUE;
			int popIndex = -1;
			for (int j = 0; j < this.numNucleobases; j++) {
				if (freqNucleobases[j] > maxFreq) {
					maxFreq = freqNucleobases[j];
					popIndex = j;
				}
			}
			// attach the most populous nucleobase to the new centroid
			newCentroid.append(this.nucleobases[popIndex]);
			
		}

		return new String(newCentroid);
	}

	private String generateRandomDNA(int length) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < length; i++)
			ret.append(this.nucleobases[this.random.nextInt(this.numNucleobases)]);

		return new String(ret);
	}

	private Random random = new Random();

//	private int numCluster = 2;
	private int numInterations = 100;
	private String[] centroids;
	private String[] oneCentroid = new String[1];
	private ArrayList<String>[] clusters;
	private ArrayList<String>[] oneCluster = new ArrayList[1];

	private int DNAlength;
	private final char[] nucleobases = {'A', 'C', 'G', 'T'};
	private final int numNucleobases = 4;

}
