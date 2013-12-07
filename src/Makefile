JFLAGS = -d ../bin -cp "../bin:/usr/lib64/openmpi/lib"
JC = mpijavac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Point.java	\
	ReadCSV.java	\
	ClusterPoints.java	\
	ClusterDNA.java	\
	Kmeans.java

build: classes

classes: $(CLASSES:.java=.class)

mpi: mpijavac Point.java ReadCSV.java ClusterPoints.java ClusterDNA.java Kmeans.java

clean:
	$(RM) ../bin/*.class
