# techknacq-core
# Table of Content
 - [Topic Modeling](#techknacq-topic)
 - [Hierarchy Clustering](#techknacq-hierarchy-clustering)
 - [Concept Graph](#concept-graph-generation)
 - [Reading List](#generate-reading-list)

# TechKnacq-topic
The topic modeling implementation to learn the document topic affiliation and word distribution for each topic
##Compile
  1. The easiest way is to comile with the IDE (either Netbeans or others) and generate a jar file
  
  2. Command line compile:
     mkdir classes 
     javac -cp "lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" -d classes @name.txt

##Run in command line
1. With jar file generated from IDE or ant:

   Simply type java -jar [jarfilename] [arguments], where arguments are specified in Usage as below
   
2. Without jar file:
  java -classpath ".\classes;lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" -Xmx1024m topic.Main [arguments], where arguments are specified in Usage as below


##Usage
Our program accepts five parameters, that is:

        + //args[0]: String: dirname
        + //args[1]: Int: topicnum (default 20)
        + //args[2]: Int: k, where k denotes top-k word to be printed for each topic (default k=10)
        + //args[3]: double: parameter alpha (default 0.07)
        + //args[4]: String: prefix name for topic model (default tech) 

where 

         + dirname is the directory for the text corpus. Note that each document is stored as single text file.

         + topicnum is the number of topics

         + k is the number of top-words to be printed for each topic

         + alpha is the dirichlet parameter

         + prefix is a string to distinguish the topic models.

To execute the program, type java -jar TechKnacq.jar [args], and the argument directory name is required. For the other parameters, we can use the default values if otherwise specified.

## Input
Put each document as a single file under the same directory, the directory name is an input for the program.

## Output
The output of results will be stored in two directories: the current work space directory and the ./lib/output directory. The output of results contain the following files:

       ./lib/output/final.beta: the word-topic matrix, where each line denotes a topic, each column denotes a word, and the value denotes the likelihood of a word belongs to this topic.

      ./lib/prefixdocument2topic.txt: the document-topic matrix, where each line is with the following format:
 
      [documentname][tab][topic1]:[value][tab][topic2]:[value][tab].....

      ./lib/prefixtopic.csv: the topic 30 words from the entire corpus

      ./lib/prefixtopic.txt: the top-k word distribution of topics, where for each topic, we print out the top-k words.

      It is formatted as follows:

topic 000
"word1",value
"word2",value
....
"wordk",value

Topic 019
"word1",value
"word2",value
....
"wordk",value

##Notes
+Remember to put the lib with the same directory of the jar file

+Remember to create a output folder within lib directory

+For Linux, please rename ./lib/lda-linux to ./lib/lda 

+Remember to unzip jar files within lib directory:)


#TechKnacq-hierarchy clustering

1. Given the co-occurrence matrices, the first step is to run the Graphformat under TopicModeling\src\util to generate the Pajek .net format.

1.1 .Net format introduction:
http://gephi.github.io/users/supported-graph-formats/pajek-net-format/

1.2 Comile and run instruction for java

compile: 

mkdir classes 
     javac -cp "lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" -d classes @name.txt

run: java [mainclassfilename] [arguments]

arguments:

the first argument: keyfilename or the topic file names

the second argument: co-occurrence matrix file name

the third argument: outputfilename

2. The second step is to run the hierarchy clustering over the outputed .net topic graph file

2.1 Compile Infomap

type make 

2.2 Run the Infomap to get the hierarchy clustering

type --help to get detailed instruction for running Infomap

2.3 Obtained the hierarchy clustering results that are stored as .tree format outputted by Infomap and the information flow results that are stored as .flow format

# Concept Graph Generation
  ## Information Flow Graph
  1. Obtain the .tree format and the information flow results .flow from running the infomap with the .net format for co-occurrence matrices, see (TechKnacq-hierarchy clustering) in above for more details

  2. Run the ReadflowNetwork under TechKnacq-topic\TopicModeling\src\util to obtain the edge table format for the topic dependency Graph
  
     2.1 Compile the ReadFlowNetwork

        javac -cp "lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" -d classes @name.txt

     2.2 Run the ReadFlowNetwork

         java -classpath ".\classes;lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" -Xmx1024m util.ReadFlowNetwork [arguments]
         
      2.3 Arguments:
          Usage: [keyfilename] [treefilename] [flowfilename] [outputfilename]
         
  3. Run the graph format code within format script to format the edge table format to the adjacent list format specified in (Generate reading list) as follows.
  
     3.1 Compile

         G++ edge2weightstandard.cpp -O3 -o format
     3.2 Usage
     
         format [inputgraphfile] [# nodes]
  ## Cross Entropy Graph Generation
  
  1. Run the techknacq-core/src/main/java/edu/isi/techknacq/topics/graph/Comparisononalledges.java
     with Arguments:
     Usage [keyfile] [tree file] [topic composition file] [# topics] [citation file] [flow file] [topicscorefile] [maximum number of files or words]

  2. The output for the Co-occurrence based cross entropy approach is saved in a file with name "entropy1.txt" while co-citation based cross entropy approach is saved in another file with name "entropy2.txt"
  
  3. Run the graph format code within format script to format the edge table format to the adjacent list format specified in (Generate reading list) as follows.
  

     3.1 Compile

         G++ edge2weightstandard.cpp -O3 -o format
     3.2 Usage
     
         format [inputgraphfile] [# nodes]
         example: format entroy1.txt 300
         
 
        
      
# Generate reading list
##Compile
  1. The easiest way is to comile with the IDE (either Netbeans or others) and generate a jar file
  
  2. Command line compile:
  
     mkdir classes 

     javac -cp "lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" -d classes @name.txt
     
##Run
1. With jar file generated from IDE or ant:

   Simply type java -jar [jarfilename] [arguments], where arguments are specified in Usage as below
   
2. Without jar file:

  java -classpath ".\classes;lib\\jackson-core-2.5.0.jar;lib\\KStem.jar;lib\\lucene-core-2.3.2.jar" -Xmx1024m readinglist.Getreadinglist [arguments], where arguments are specified in Usage as below

##Usage
Our program accepts the following parameters, that are:


         //args[0]: keyword (string);
         
         //args[1]: doc2topicfilename (string);
         
         //args[2]: topickeyname (string);
         
         //args[3]: topicgraphfilename (string);
         
         //args[4]: dockeyname (string);
         
         //args[5]: the page rank file (String);
         
         //args[6]: number of docs per topic (Integer)
         
         //args[7]: number of maximum dependence topics (Integer);
         
         //args[8]: a list of bad papers that to be filtered out.
         
##Input format

###keyword
Note that if the keyword is not unigram, we use "_" to connect each term within the input keyword
###doc2topicfile
Each line denotes the topic representation for a document, with the following format:
[documentname][tab][topic1]:[value][tab][topic2]:[value][tab].....
###topickeyname 
Each line denotes the word distribution for a topic, and it is formated as the mallet output format
###topicgraphfile
The graph file is with the following format:
The first line is number of nodes, and starting from the second lins is the adjacence list of each node formated as follows:

node_id,degree_d:neighboreid1,weight1:neighborid2,weight2:...neighboridd,weightd

Note that the node_id is within the range [0,n-1], where n is number of nodes, and the list of neighbors are sorted in ascending order too.

An example of input graph format is as follows:

3

0,2:1,1.0:2,1.0

1,2:0,1.0:2,1.0

2,2:0,1.0:1,1.0

where this graph is a triangle with three vertices
###dockeyname
The index.json file for metadata information of each document
###Pagerankfile

A graph.net format representation for citation network, and the pagerank score is associated with each document node. An example of pagerank file can be found here:  \Data\ReadingListInputExample

###Filterfile

It is a comma separate file that contains the document key and a binary value


##Output format
The output is saved in a JSON file with name keyword_readinglist





