# techknacq-core

This repository contains Java code for various core functionality of the
TechKnAcq project, including methods for predicting concept dependency
relations and for generating reading lists.

## Table of Contents

- [Overview](#overview)
- [Topic Modeling](#topic-modeling)
- [Hierarchy Clustering](#hierarchy-clustering)
- [Concept Graph Generation](#concept-graph-generation)
- [Basic Reading List Generation](#generate-reading-list)
- [Pedagogical Reading List Generation](#new-reading-list)


## Overview

Some of the documentation below is historical.

To compile the package, as it is used by techknacq-tk, run:

```bash
mvn package
```

## Topic Modeling

This code provides one method of generating topic models defined by a
distribution over words and a distribution over documents. The TechKnAcq
production system instead uses the implementation of LDA in Mallet.

NB: This repository does not contain all of the required files to run
the topic modeling code, e.g., lib/lda, referenced below.

### Compile

1. The easiest way is to compile with an IDE (e.g., Netbeans) to generate
a jar file.

2. To compile from the command line:

```bash
mkdir classes
javac -cp "lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" -d classes @name.txt
```

### Run

Create the lib/output directory.

Unzip the jar files in the lib directory.

To run on Linux, rename lib/lda-linux to lib/lda.


1. With a jar file generated from IDE or ant:
```bash
java -jar [jarfilename] [dirname] [topicnum] [k] [alpha] [prefix]
```

2. Without jar file:
```bash
java -classpath ".\classes;lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" \
     -Xmx1024m topic.Main [dirname] [topicnum] [k] [alpha] [prefix]
```

The arguments are:

1. *dirname*: The corpus directory containing plain-text files.
2. *topicnum*: The number of topics. Default: 20.
3. *k*: The number of words to print for each topic. Default: 10
4. *alpha*: The Dirichlet parameter. Default: 0.07.
5. *prefix*: The name for the topic model. Default: tech.

The directory name argument is required; the others are optional.

### Input

The corpus directory cannot contain subdirectories.

### Output

The resulting output will be stored in the current working directory and
lib/output. The output files include:

- `lib/output/final.beta`: The word-topic matrix, where each line denotes a
topic, each column denotes a word, and each value is the likelihood that
the word belongs to this topic.

- `lib/prefixdocument2topic.txt`: The document-topic matrix, where each
line is of the format:
```
[documentname]\t[topic1]:[value]\t[topic2]:[value]\t...
```

- `lib/prefixtopic.csv`: The topic 30 words from the entire corpus

- `lib/prefixtopic.txt`: The top-k word distribution of topics. It is formatted
as follows:
```
topic 000
"word1",value
"word2",value
...
"wordk",value

Topic 019
"word1",value
"word2",value
....
"wordk",value
```


## Hierarchy Clustering

NB: The compilation instructions below will not work for this repository,
which does not include 'name.txt', which was used in the TechKnAcq-topic
repository.

1. Given the co-occurrence matrices, the first step is to run the Graphformat
code (in `src/main/java/edu/isi/techknacq/topics/graph`) to generate the Pajek
.net format. For an introduction to this format, see
http://gephi.github.io/users/supported-graph-formats/pajek-net-format

2. Compile:
```bash
mkdir classes
javac -cp "lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" \
      -d classes @name.txt
```

3. Run:
```java [mainclassfilename] [keyfilename] [matrix] [outputfile]```

Arguments:
- *keyfilename* or the topic file name.
- *matrix*: The co-occurrence matrix file name.
- *outputfile*: The name for the output file.

4. To run the hierarchical clustering over the outputed .net topic graph file,
first compile Infomap using `make`.

5. Run Infomap to get the hierarchy clustering. Use the --help flag to get
detailed instructions for running Infomap.

6. You'll have obtained the hierarchy clustering resultsm, stored as .tree
format outputted by Infomap and the information flow results that are stored
as .flow format.


## Concept Graph Generation

### Information Flow Graph

1. Obtain the .tree format and the information flow results .flow from
running the infomap with the .net format for co-occurrence matrices,
see (TechKnAcq-hierarchy clustering) in above for more details.

2. Run the ReadflowNetwork under TechKnAcq-topic/TopicModeling/src/util to
obtain the edge table format for the topic dependency Graph

2.1. Compile the ReadFlowNetwork

```bash
javac -cp "lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" \
      -d classes @name.txt
```

2.2. Run the ReadFlowNetwork

```bash
java -classpath ".\classes;lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" \
     -Xmx1024m util.ReadFlowNetwork [arguments]
```

2.3. Arguments: [keyfilename] [treefilename] [flowfilename] [outputfilename]

3. Run the graph formatting code in the `format` script to format the edge
table format to the adjacency list format specified in (Generate reading list)
as follows.

3.1. Compile

```bash
cd util
g++ edge2weightstandard.cpp -O3 -o format
```

3.2. Usage: format [inputgraphfile] [# nodes]


### Cross Entropy Graph Generation

1. Run `src/main/java/edu/isi/techknacq/topics/graph/ComparisonOnAllEdges.java`
with arguments:
```
[keyfile] [tree file] [topic composition file] [# topics] [citation file]
[flow file] [topicscorefile] [maximum number of files or words]
```

2. The output for the co-occurrence based cross entropy approach is
saved in a file with name "entropy1.txt" while co-citation based
cross entropy approach is saved in another file with name
"entropy2.txt".

3. Run the graph format code within format script to format the edge table
format to the adjacent list format specified in (Generate reading list).
See the directions above.


## Generate reading list

### Compile

1. The easiest way is to comile with the IDE (either Netbeans or others)
and generate a jar file.
2. Command line compile:
```bash
mkdir classes
javac -cp "lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" \
      -d classes @name.txt
```

### Run

1. With jar file generated from IDE or ant:

Simply type `java -jar [jarfilename] [arguments]`, where arguments are
specified in Usage as below.

2. Without jar file:
```
java -classpath ".\classes;lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" -Xmx1024m readinglist.Getreadinglist [arguments]
```
where arguments are specified in Usage as below

### Usage

Our program accepts the following parameters, that are:
1. keyword (string)
2. doc2topicfilename (string)
3. topickeyname (string)
4. topicgraphfilename (string)
5. dockeyname (string)
6. the page rank file (String)
7. number of docs per topic (Integer)
8. number of maximum dependence topics (Integer)
9. a list of bad papers that to be filtered out

### Input format

#### keyword

Note that if the keyword is not unigram, we use `_` to connect each term
within the input keyword.

#### doc2topicfile

Each line denotes the topic representation for a document, with the
following format:
```
[documentname][tab][topic1]:[value][tab][topic2]:[value][tab]...
```

#### topickeyname

Each line denotes the word distribution for a topic, and it is formatted
as the mallet output format.

#### topicgraphfile

The first line is number of nodes, and starting from the second line is
the adjacency list of each node, formatted as follows:

```
node_id,degree_d:neighboreid1,weight1:neighborid2,weight2:...neighboridd,weightd
```

Note that the `node_id` is within the range [0,n-1], where n is number of
nodes, and the list of neighbors are sorted in ascending order too.

An example of input graph format is as follows:

```
3

0,2:1,1.0:2,1.0

1,2:0,1.0:2,1.0

2,2:0,1.0:1,1.0
```

where this graph is a triangle with three vertices

#### dockeyname
The index.json file for metadata information of each document

#### Pagerankfile

A graph.net format representation for citation network, and the pagerank score
is associated with each document node. (At ISI, an example of pagerank file
can be found in Data/ReadingListInputExample.)

#### Filterfile

It is a comma separate file that contains the document key and a binary value


### Output format

The output is saved in a JSON file with name `<keyword>_readinglist`


## New Reading List

### Main Function

`techknacq-core/src/main/java/edu/isi/techknacq/topics/readinglist/NewReadingList.java`

### Run with jar file
Simply type `java -jar [jarfilename] [arguments]`, where arguments are
specified in Usage as below.

### Run without jar file

`java -classpath
".\classes;lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" -Xmx1024m readinglist.NewReadingList [arguments]`, where arguments are
specified in Usage as below.

### Usage

Our program accepts the following parameters, that are:

1. keyword (string);
2. doc2topicfilename (string);
3. topickeyname (string);
4. topicgraphfilename (string);
5. dockeyname (string);
6. the page rank file (String);
7. number of docs per topic (Integer)
8. number of maximum dependence topics (Integer);
9. a list of bad papers that to be filtered out.
10. the file of pedagogical type of each document
11. configuration file

### Input format

#### The file of pedagogical type of each document

Each row is seperated by tab with three columns:

- first column: labeled/unlabeled (string) e.g.: `unlabeled`
- second column: ID of documents, e.g.: `ACL-X98-1030`
- third column: pedagogical type (string), e.g.: `['survey']`

#### The configuration file

An example of the configuration file is
[here](https://github.com/ISI-TechKnAcq/techknacq-core/blob/master/config.txt).

Basically, we need to specify

- the mapping from pedagogical type to a score (double type)
- the parameter value: e.g., the relevance threshold
- the coefficent weight for each feature (i.e., the weight controls the
contribution of each feature in ordering documents)

All the other input files are using the same format as specfied as above.


## Citation

If you use this code, cite:

> Jonathan Gordon, Linhong Zhu, Aram Galstyan, Prem Natarajan, and Gully
> Burns. 2016. Modeling Concept Dependencies in a Scientific Corpus. In
> Proceedings of the 54th Annual Meeting of the Association for
> Computational Linguistics (ACL). https://doi.org/10.18653/v1/P16-1082


## Acknowledgments

This research is based upon work supported in part by the Office of the
Director of National Intelligence (ODNI), Intelligence Advanced Research
Projects Activity (IARPA), via Air Force Research Laboratory (AFRL). The views
and conclusions contained herein are those of the authors and should not be
interpreted as necessarily representing the official policies or endorsements,
either expressed or implied, of ODNI, IARPA, AFRL, or the U.S. Government. The
U.S. Government is authorized to reproduce and distribute reprints for
Governmental purposes notwithstanding any copyright annotation thereon.
