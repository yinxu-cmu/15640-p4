import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import mpi.*;

public class ClusterPoints {

	public ClusterPoints() {

	}

	@SuppressWarnings({ "unchecked" })
	public ClusterPoints(ArrayList<Point> points, String outputFile, int numCluster)
			throws Exception {

		// get the min, max of x & y
		double x, y;
		for (Point point : points) {
			x = point.getX();
			y = point.getY();
			if (x < minX)
				minX = x;
			if (x > maxX)
				maxX = x;
			if (y < minY)
				minY = y;
			if (y > maxY)
				maxY = y;
		}

		// randomly init centroids
		centroids = new Point[numCluster];
		int numCentroids = centroids.length;
		double randomX, randomY;
		rangeX = maxX - minX;
		rangeY = maxY - minY;
		for (int i = 0; i < numCentroids; i++) {
			randomX = minX + random.nextDouble() * rangeX;
			randomY = minY + random.nextDouble() * rangeY;
			centroids[i] = new Point(randomX, randomY);
		}

		// start interations
		int numPoints = points.size();
		clusters = new ArrayList[numCluster];

		int myRank;
		int senderRank;
		int dest;
		int size;

		myRank = MPI.COMM_WORLD.Rank();
		size = MPI.COMM_WORLD.Size();

		for (int i = 0; i < numInterations; i++) {
			for (int j = 0; j < numCluster; j++)
				clusters[j] = new ArrayList<Point>();

			int numPointsSlave = numPoints / (size - 1);
			int[] point2clusterMaster = new int[numPoints];
			int[] point2clusterSlave = new int[numPointsSlave];
			int[] startSignal = new int[1];

			if (myRank != 0) {
				// start computation only after receive start signal from master
				MPI.COMM_WORLD.Recv(centroids, 0, numCentroids, MPI.OBJECT, 0, 0);

				for (int j = 0; j < numPointsSlave; j++) {
					point2clusterSlave[j] = getNearestCentroid(points
							.get(((myRank - 1) * numPointsSlave) + j));
				}

				MPI.COMM_WORLD.Send(point2clusterSlave, 0, numPointsSlave, MPI.INT, 0, 100);

//				String debuginfo = "\nSLAVE************ iteration" + i + "from" + myRank + "\n";
//				debuginfo += "centorids are:\n";
//				for (int k = 0; k < numCentroids; k++)
//					debuginfo += centroids[k] + "\t";
//
//				System.out.println(debuginfo);
				
				MPI.COMM_WORLD.Recv(oneCluster, 0, 1, MPI.OBJECT, 0, 1);
				oneCentroid[0] = recalculateCentroid(oneCluster[0]);
				MPI.COMM_WORLD.Send(oneCentroid, 0, 1, MPI.OBJECT, 0, 99);

			} else {
				// just send the start signal to eash slave
				for (senderRank = 1; senderRank < size; senderRank++) {
					MPI.COMM_WORLD.Send(centroids, 0, numCentroids, MPI.OBJECT, senderRank, 0);
				}

				for (senderRank = 1; senderRank < size; senderRank++) {

					Status status = MPI.COMM_WORLD.Recv(point2clusterSlave, 0, numPointsSlave, MPI.INT,
							MPI.ANY_SOURCE, 100);

					// debug print
					// String debuginfo = "\nMASTER-------- iteration " + i +
					// "from" + s.source + '\n';
					// for (int k = 0; k < point2clusterSlave.length; k++) {
					// debuginfo += point2clusterSlave[k] + "\t";
					// }

					for (int k = 0; k < numPointsSlave; k++) {
						point2clusterMaster[((status.source - 1) * numPointsSlave) + k] = point2clusterSlave[k];
					}

					// debuginfo += '\n';
					// for (int k = 0; k < point2clusterMaster.length; k++)
					// debuginfo += point2clusterMaster[k] + "\t";
					// System.out.println(debuginfo);

				}

				for (int k = 0; k < numPoints; k++) {
					clusters[point2clusterMaster[k]].add(points.get(k));
				}
				
				
				for (senderRank = 1; senderRank < size; senderRank++) {
					oneCluster[0] = clusters[senderRank - 1];
					MPI.COMM_WORLD.Send(oneCluster, 0, 1, MPI.OBJECT, senderRank, 1);
				}
				
				for (senderRank = 1; senderRank < size; senderRank++) {
					Status status = MPI.COMM_WORLD.Recv(oneCentroid, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, 99);
					centroids[status.source - 1] = oneCentroid[0];
				}

				// recalculate the each centroid
				/*for (int k = 0; k < numCentroids; k++) {
					centroids[k] = recalculateCentroid(clusters[k]);
				}*/

//				String debuginfo = "\n\n\n\n\n\n\n\n\nMASTER------------ iteration" + i
//						+ "cluster result\n";
//				for (int k = 0; k < point2clusterMaster.length; k++)
//					debuginfo += point2clusterMaster[k] + "\t";
//
//				debuginfo += "\nMASTER*********** iteration" + i
//						+ "after centroids recalculation\n";
//				debuginfo += "centorids are:\n";
//				for (int k = 0; k < numCentroids; k++)
//					debuginfo += centroids[k] + "\t";
//
//				System.out.println(debuginfo);

			}

		}

		// write the result into the output file
		FileWriter writer = new FileWriter(outputFile);
		for (int i = 0; i < numCluster; i++) {
			for (Point point : clusters[i]) {
				writer.append("" + point.getX());
				writer.append(',');
				writer.append("" + point.getY());
				writer.append(',');
				writer.append("" + i);
				writer.append('\n');
				writer.flush();
			}
		}
		writer.close();

	}

	/**
	 * @param point
	 * @return the index of the nearest centroid for a given point
	 */
	private int getNearestCentroid(Point point) {
		int index = -1;
		int len = this.centroids.length;
		double minDistance = Double.MAX_VALUE;
		double distance;

		for (int i = 0; i < len; i++) {
			distance = Math.pow((point.getX() - centroids[i].getX()), 2)
					+ Math.pow((point.getY() - centroids[i].getY()), 2);
			if (distance < minDistance) {
				minDistance = distance;
				index = i;
			}
		}

		return index;
	}

	/**
	 * @param points
	 * @return the centroid of a cluster of points
	 */
	private Point recalculateCentroid(ArrayList<Point> points) {
		int numPoints = points.size();
		Point centroid = null;

		if (numPoints == 0) {
			double randomX, randomY;
			randomX = minX + random.nextDouble() * rangeX;
			randomY = minY + random.nextDouble() * rangeY;
			centroid = new Point(randomX, randomY);
		} else {
			double sumX = 0;
			double sumY = 0;
			for (Point point : points) {
				sumX += point.getX();
				sumY += point.getY();
			}

			centroid = new Point(sumX / numPoints, sumY / numPoints);
		}
		return centroid;
	}

	private Random random = new Random();

	private double minX = Double.MAX_VALUE;
	private double maxX = Double.MIN_VALUE;
	private double minY = Double.MAX_VALUE;
	private double maxY = Double.MIN_VALUE;
	private double rangeX = 0.0;
	private double rangeY = 0.0;

	@SuppressWarnings("unused")
	private int numCluster = 2;
	private int numInterations = 100;
	private Point[] centroids;
	private Point[] oneCentroid = new Point[1];
	private ArrayList<Point>[] clusters;
	@SuppressWarnings("unchecked")
	private ArrayList<Point>[] oneCluster = new ArrayList[1];

}
