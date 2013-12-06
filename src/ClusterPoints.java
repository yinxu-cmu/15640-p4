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
		double rangeX = maxX - minX;
		double rangeY = maxY - minY;
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
			if (myRank != 0) {
				int[] point2clusterSlave = new int[numPointsSlave];
				for (int j = 0; j < numPointsSlave; j++) {
					point2clusterSlave[j] = getNearestCentroid(points
							.get(((myRank - 1) * numPointsSlave) + j));
				}

				MPI.COMM_WORLD.Send(point2clusterSlave, 0, numPointsSlave, MPI.INT, 0, 100);

			} else {
				for (senderRank = 1; senderRank < size; senderRank++) {
					int[] point2clusterSlave = new int[numPointsSlave];
					Status s = MPI.COMM_WORLD.Recv(point2clusterSlave, 0, numPointsSlave, MPI.INT,
							MPI.ANY_SOURCE, 100);
					for (int j = 0; j < numPointsSlave; j++) {
						point2clusterMaster[((s.source - 1) * numPointsSlave) + j] = point2clusterSlave[j];
					}
				}
			}

			for (int j = 0; j < numPoints; j++) {
				clusters[point2clusterMaster[j]].add(points.get(j));
			}

			// recalculate the each centroid
			for (int j = 0; j < numCentroids; j++) {
				centroids[j] = recalculateCentroid(clusters[j]);
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
		double sumX = 0;
		double sumY = 0;
		for (Point point : points) {
			sumX += point.getX();
			sumY += point.getY();
		}

		int numPoints = points.size();
		Point centroid = new Point(sumX / numPoints, sumY / numPoints);
		return centroid;
	}

	private Random random = new Random();

	private double minX = Double.MAX_VALUE;
	private double maxX = Double.MIN_VALUE;
	private double minY = Double.MAX_VALUE;
	private double maxY = Double.MIN_VALUE;

	@SuppressWarnings("unused")
	private int numCluster = 2;
	private int numInterations = 10;
	private Point[] centroids;
	private ArrayList<Point>[] clusters;
}
