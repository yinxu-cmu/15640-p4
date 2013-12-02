import sys
import getopt
import random
import string

def usage():
    print '$> python generaterawdata.py <required args> [optional args]\n' + \
        '\t-c <#>\t\tNumber of clusters to generate\n' + \
        '\t-d <#>\t\tNumber of DNA strands per cluster\n' + \
        '\t-o <file>\tFilename for the output of the raw data\n' + \
        '\t-l [#]\t\Length of each DNA strand\n'  

       
def DNAdiff(dna1, dna2):
    return sum ( dna1[i] != dna2[i] for i in range(length) )

def generateDNAcentroid(length):
    return ''.join(random.choice('ACGT') for x in range(length))

def generateDNA(dna, variance):
    dnaRet = ''
    replaceIndice = random.sample(range(length), variance)

    for i in range(length):
        if i in replaceIndice:
            dnaRet += random.choice('ACGT'.replace(dna[i], ''))
        else:
            dnaRet += dna[i]

    return dnaRet 

def tooClose(dna, dnas, minDis):

    for dna_tmp in dnas:
        if DNAdiff(dna, dna_tmp) < minDis:
            return True
    return False

def handleArgs(args):
    # set up return values
    numClusters = -1
    numDNA = -1
    output = None
    length = 20

    try:
        optlist, args = getopt.getopt(args[1:], 'c:d:v:o:')
    except getopt.GetoptError, err:
        print str(err)
        usage()
        sys.exit(2)

    for key, val in optlist:
        # first, the required arguments
        if   key == '-c':
            numClusters = int(val)
        elif key == '-d':
            numDNA = int(val)
        elif key == '-o':
            output = val.strip()
        # now, the optional argument
        elif key == '-v':
            length = int(val)

    # check required arguments were inputted  
    if numClusters < 0 or numDNA < 0 or \
            length < 1 or \
            output is None:
        usage()
        sys.exit()
    return (numClusters, numDNA, output, \
            length)

def drawOrigin(maxValue):
    return numpy.random.uniform(0, maxValue, 2)

# start by reading the command line
numClusters, \
numDNA, \
output, \
length = handleArgs(sys.argv)

writer = open(output, "w")


# step 1: generate each DNA centroid
centroids_dna = []
for i in range(0, numClusters):
    centroid_dna_tmp = generateDNAcentroid(length)

    # is it far enough from the others?
    while (tooClose(centroid_dna_tmp, centroids_dna, 1)):
        centroid_dna_tmp = generateDNAcentroid(length)

    centroids_dna.append(centroid_dna_tmp)


# step 2: generate the DNA strands for each centroid
strands = []
minVar = 1
maxVar = 5
for i in range(0, numClusters):
    # compute the variance for this cluster
    variance = random.randint(minVar, maxVar)
    cluster = centroids_dna[i]
    for j in range(0, numDNA):
        writer.write(generateDNA(cluster, variance))
        writer.write('\n')

print 'finish generate DNA csv file'