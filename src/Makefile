JFLAGS = -d ../bin
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	ReadCSV.java	\
	Point.java	\
	ClusterPoints.java	\
	ClusterDNA.java	\
	Kmeans.java	

build: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) ../bin/*.class
