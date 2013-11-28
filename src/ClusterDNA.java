import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ClusterDNA {

	public ClusterDNA() {

	}

	public ClusterDNA(ArrayList<String> dnaStrands, String outputFile, int numClusters) throws IOException {
		// get the DNA strands length
		DNAlength = dnaStrands.get(0).length();

		// randomly init centroids
		centroids = new String[numCluster];
		int numCentroids = centroids.length;
		for (int i = 0; i < numCentroids; i++)
			centroids[i] = this.generateRandomDNA(DNAlength);

		// start interations
		int numDNAStrands = dnaStrands.size();
		clusters = new ArrayList[numCluster];

		for (int i = 0; i < numInterations; i++) {
			for (int j = 0; j < numCluster; j++)
				clusters[j] = new ArrayList<String>();

			for (int j = 0; j < numDNAStrands; j++) {
				clusters[getNearestCentroid(dnaStrands.get(j))].add(dnaStrands.get(j));
			}

			// recalculate the each centroid
			for (int j = 0; j < numCentroids; j++) {
				centroids[j] = recalculateCentroid(clusters[j]);
			}
		}
		
		for (String centroid : centroids)
			System.out.println(centroid);
		
		// write the result into the output file
		FileWriter writer = new FileWriter(outputFile);
		for (int i = 0; i < numCluster; i++) {
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

	@SuppressWarnings("unused")
	private int numCluster = 2;
	private int numInterations = 100;
	private String[] centroids;
	private ArrayList<String>[] clusters;

	private int DNAlength;
	private final char[] nucleobases = {'A', 'C', 'G', 'T'};
	private final int numNucleobases = 4;

}
