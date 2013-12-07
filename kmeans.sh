#!/bin/sh

# Number of Points
b=5

# Number of Cluster
k=4

# Number of Processes must be (k + 1)
p=5

cd src/

mpijavac Point.java ReadCSV.java ClusterPoints.java ClusterDNA.java Kmeans.java

cd ..

python ./DataGeneratorScripts/randomclustergen/generaterawdata.py -c $k  -p $b -o input/data_points.csv

python ./DataGeneratorScripts/randomdnagen/generaterawdata.py -c $k -d $b -o input/dna_strands.csv

start=$(date +'%s')
#mpirun -np $p -machinefile machines.txt java -cp ./src/ Kmeans $k
mpirun -np $p  java -cp ./src/ Kmeans $k
end=$(date +'%s')
diff=$(( $end - $start))
echo "JAVA Program took $diff seconds"
