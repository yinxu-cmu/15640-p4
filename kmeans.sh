#!/bin/sh

# Number of Points
b=100

# Number of Cluster
k=10

# Number of Processors
p=1

# echo ********GENERATING $b INPUT POINTS EACH IN $k CLUSTERS 
python ./DataGeneratorScripts/randomclustergen/generaterawdata.py -c $k  -p $b -o input/data_points.csv

python ./DataGeneratorScripts/randomdnagen/generaterawdata.py -c $k -d $b -o input/dna_strands.csv

start=$(date +'%s')
mpirun -np $p java -cp ./bin/ Kmeans $k
end=$(date +'%s')
diff=$(( $end - $start))
echo "JAVA Program took $diff seconds"
