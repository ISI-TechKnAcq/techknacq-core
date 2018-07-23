/*
 * The new reading list generation algorithm
 */

package edu.isi.techknacq.readinglist;

import edu.isi.techknacq.graph.ConceptDepth;
import edu.isi.techknacq.graph.Node;
import edu.isi.techknacq.graph.ReadGraph;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.isi.techknacq.topic.WeightPair;
import edu.isi.techknacq.topic.WordPair;

/**
 *
 * @author linhong
 * @since May 15 2015
 */
public class NewReadingList {
    // The function that matches a query to topics.
    private KeywordToConcept match1;

    // The map function that matches each paper to the pagerank score.
    private HashMap<String, Double> paperPageRank;

    // The map function that matches each paper to the pedegogical role type.
    private HashMap<String, String> paperPVtype;

    // The map function that matches each paper to the text complexity score.
    private HashMap<String, Double> paperComplexity;

    // The map function that assignes each pedagogical role type with a score.
    private HashMap<String, Double> typeToScore;

    private ArrayList<ArrayList<WordPair>> wordInTopic;
    private ArrayList<String> topicKeys;
    private ArrayList<Integer> hitTopic;
    private Map<String, String> docmap;
    private List []topicToDocs;
    private ArrayList<String> docfiles;
    private int []ordertopic;
    private HashSet<String> authorLists;
    private int relevancek = 10;
    private double w1 = 0.6;
    private double w2 = 0.2;
    private double w3 = 0.1;
    private double w4 = 0.001;
    private Logger logger = Logger.getLogger(NewReadingList.class.getName());

    /**
     * @param keyname: the file name of word distribution for each topic
     * @param pagerankfile: the file name of page rank score
     * @param docfile: the file name of Meta-data information for each document
     * @param dnum: the number of documents for each topic
     * @param doc2conceptfile: the filename of topic composition for each
     *        document
     * @param filterfile: the filename of bad paper versus good paper
     @
    **/
    public void readData(String keyname, String pagerankfile, String docfile,
                         int dnum, String doc2conceptfile, String filterfile) {
        match1 = new KeywordToConcept();
        match1.readKey(keyname);
        System.out.println("Finish reading topic");
        this.wordInTopic = match1.getWeightTopic();
        this.topicKeys = match1.getTopics();
        readPageRankscore(pagerankfile);
        System.out.println("Finish reading pagerank");
        ReadDocumentKey rdk = new ReadDocumentKey(docfile);
        rdk.readFile();
        System.out.println("Finish reading document");
        docmap = rdk.getDocMap();
        ConceptToDoc Getdoc = new ConceptToDoc();
        Getdoc.initNum(topicKeys.size());
        Getdoc.addFilter(filterfile);
        Getdoc.getTopK(dnum * relevancek, doc2conceptfile);
        topicToDocs = Getdoc.getTopic2Doc();
        docfiles = Getdoc.getDocName();
        System.out.println("Finish reading data");
    }

    /*
     * Read the pedagogical type of each document
     */
    public void readPV(String PVtypefile) {
        this.paperPVtype = new HashMap<String,String>(30000);
        try {
            FileInputStream fstream1;
            fstream1 = new FileInputStream(PVtypefile);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String docid;
            String type;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.next();
                docid = sc.next();
                type = sc.next();
                if (!this.paperPVtype.containsKey(docid)) {
                    this.paperPVtype.put(docid, type);
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Read the configuration files that provides:
     * 1. the heuristic rules that combines different document features for
     *    ranking
     * 2. the heuristic rules that assignes different document types with
     *    different weight
     * 3. other parameters such as relevance threshold
     * Those heuristic rules will be automatically learned with feedbacks
     * from users who evaluate the reading list
     */
    public void readConfiguration(String filename) {
        try {
            this.typeToScore = new HashMap<String,Double>(5);
            FileInputStream fstream1;
            fstream1 = new FileInputStream(filename);
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            while ((strline = br.readLine()) != null) {
                if (strline.contains("#PVvalue")) {
                    while (!(strline = br.readLine()).contains("#end")) {
                        Scanner sc = new Scanner(strline);
                        sc.useDelimiter("\t");
                        String type = sc.next();
                        double v = sc.nextDouble();
                        this.typeToScore.put(type, v);
                    }
                }
                if (strline.contains("#relevancethreshold")) {
                    strline = br.readLine();
                    this.relevancek = Integer.parseInt(strline);
                    br.readLine();
                }
                if (strline.contains("#weight")) {
                    while (!(strline = br.readLine()).contains("#end")) {
                        Scanner sc = new Scanner(strline);
                        sc.useDelimiter("\t");
                        int id = sc.nextInt();
                        double w = sc.nextDouble();
                        if (id == 1)
                            this.w1 = w;
                        if (id == 2)
                            this.w2 = w;
                        if (id == 3)
                            this.w3 = w;
                        if (id == 4)
                            this.w4 = w;
                    }
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public String getDocMeta(String id) {
        if (this.docmap.containsKey(id))
            return this.docmap.get(id);
        else
            return "author: ??, title: ??";
    }

    public ArrayList<WeightPair> getDocs(int tindex) {
        ArrayList<WeightPair> mydocs =
            new ArrayList<WeightPair>(topicToDocs[tindex].size());
        for (int i = 0; i < topicToDocs[tindex].size(); i++) {
            WeightPair o = (WeightPair)topicToDocs[tindex].get(i);
            mydocs.add(o);
        }
        return mydocs;
    }

    public void readPageRankscore(String filename) {
        try {
            this.paperPageRank =
                new HashMap<String,Double>(this.topicKeys.size());
            FileInputStream fstream1;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            br.readLine(); // Skip node vertices line
            br.readLine(); // Skip column name line
            String keyname;
            double value;
            String sr;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t| ");
                sr = sc.next();
                if (sr.contains("*Edge") || sr.contains("*Arc"))
                    break;
                keyname = sc.next();
                keyname = keyname.substring(1, keyname.length() - 1);
                value = sc.nextDouble();
                if (!this.paperPageRank.containsKey(keyname)) {
                    this.paperPageRank.put(keyname, value);
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public String printTopics(int tindex) {
        String topicname;
        topicname = "\"topic\": [\n";
        double minvalue = 1;
        double maxvalue = 0;
        for (int i = 0; i < this.wordInTopic.get(tindex).size(); i++) {
            WordPair w = wordInTopic.get(tindex).get(i);
            double value = w.getprob();
            if (value > maxvalue)
                maxvalue = value;
            if (value < minvalue) {
                minvalue = value;
            }
        }
        for (int i = 0; i < this.wordInTopic.get(tindex).size(); i++) {
            WordPair w = wordInTopic.get(tindex).get(i);
            String word = w.getWord();
            double value = w.getprob();
            topicname += "{";
            topicname += "\"word\": \"" + word + "\",";
            topicname += "\"value\": " + value + "}";
            if (i < this.wordInTopic.get(tindex).size() - 1)
                topicname += ",";
        }
        topicname += "],\n";
        topicname += "\"topicID\":";
        topicname += " ";
        topicname += tindex;
        topicname += ",";
        return topicname;
    }

    public String extractAuthor(String metadata) {
        int index1;
        int index2;
        if (metadata != null) {
            index1 = metadata.indexOf("author:");
            index2 = metadata.indexOf("title:");
        } else {
            index1 = -1;
            index2 = -1;
        }
        String author = null;
        if (index1 >= 0 && index2 >= 0) {
            author = metadata.substring(index1 + 8, index2);
        }
        return author;
    }

    public String printDocName(String metadata, String did, double score) {
        String name;
        int index1;
        int index2;
        if (metadata != null) {
            index1 = metadata.indexOf("author:");
            index2 = metadata.indexOf("title:");
        } else {
            index1 = -1;
            index2 = -1;
        }
        String author;
        String title;
        if (index1 >= 0 && index2 >= 0) {
            author = metadata.substring(index1 + 8, index2);
        } else
            author = null;
        if (index2 >= 0) {
            title = metadata.substring(index2 + 7, metadata.length());
        } else
            title = null;
        name = "\n\t\t{";
        name = name + "\"author\": \"" + author + "\", \"title\": \"" +
            title + "\",\"ID\": \"" + did + "\"},";
        return name;
    }

    /*
     * Order documents that are relevent to topic tindx by the combination of
     * document features:
     * 1. pagerank score;
     * 2. pedogocial type score;
     * 3. text complexity score;
     * 4. relevance score;
     */
    public String getTopDoc(int tindex, int dnum, List mylist,
                            boolean [] isvisit) {
        ArrayList<WeightPair> mydocs = this.getDocs(tindex);
        mylist.clear();
        String docstring = "";
        for (int i = 0; i < mydocs.size(); i++) {
            WeightPair o = mydocs.get(i);
            int Did = o.getIndex();
            double value4 = o.getWeight(); // value4: relevance score;
            if (isvisit[Did])
                continue;
            String dockey = this.docfiles.get(Did);
            double value1; // value1: pagerankscore;
            if (this.paperPageRank.containsKey(dockey))
                value1 = this.paperPageRank.get(dockey);
            else
                value1 = 0;
            double value2; // Pedgogical value score;
            if (this.paperPVtype.containsKey(dockey)) {
                String type = this.paperPVtype.get(dockey);
                if (!this.typeToScore.containsKey(type)) {
                    value2 = 0;
                } else
                    value2 = this.typeToScore.get(type);
            } else
                value2 = 0;
            double value = value1 * w1 + value2 * w2 + value4 * w4;
            mylist.add(new WeightPair(value, Did));
        }
        docstring += "\n\"documents\": [";
        int j = 0;
        int dcount = 0;
        Collections.sort(mylist);
        while (dcount < dnum && j < mylist.size() && dcount < mylist.size()) {
            WeightPair o = (WeightPair)mylist.get(j);
            int Did = o.getIndex();
            isvisit[Did] = true;
            String dfile = docfiles.get(Did);
            String metavalue = this.getDocMeta(dfile);
            if (metavalue == null) {
                System.out.println(Did);
            }
            String author = this.extractAuthor(metavalue);
            if (!this.authorLists.contains(author)) {
                String name = this.printDocName(metavalue, dfile,
                                                o.getWeight());
                docstring += name;
                this.authorLists.add(author);
                dcount++;
            }
            j++;
        }
        docstring = docstring.substring(0, docstring.length() - 1);
        docstring += "]";
        return docstring;
    }

    public void run(String keyword, String graphfile, int maxtopic, int dnum) {
        try {
            FileWriter fstream = new FileWriter(keyword + "_readinglist.json",
                                                false);
            BufferedWriter out = new BufferedWriter(fstream);
            ReadGraph myreader = new ReadGraph(graphfile);
            Node []G = myreader.getGraph();
            this.ordertopic = myreader.orderNode();
            ConceptDepth Dependency = new ConceptDepth();
            Dependency.initGraph(G);
            Dependency.initTopics(this.topicKeys);
            boolean []isvisit = new boolean[this.docfiles.size()];
            for (int i = 0; i < isvisit.length; i++) {
                isvisit[i] = false;
            }

            // Get matched topic.
            hitTopic = match1.getMatch(keyword);
            char []isTopicVisit = new char[this.topicKeys.size()];
            Arrays.fill(isTopicVisit, 'v');
            List mylist = new ArrayList<WeightPair>(100);
            this.authorLists = new HashSet<String>();
            out.write("{");
            out.write("\"keyword\": \"" + keyword + "\",\n");

            // Get dependency topic.
            out.write("\"Match documents\": [\n\t");
            for (int i = 0; i < hitTopic.size(); i++) {
                int tindex = hitTopic.get(i);
                isTopicVisit[tindex] = 'm';
                ArrayList<Integer> deptopics = Dependency.getTopNode(maxtopic,
                                                                     tindex);
                for (int j = 0; j < deptopics.size(); j++) {
                    int ddtindex = deptopics.get(j);
                    if (isTopicVisit[ddtindex] != 'm')
                        isTopicVisit[ddtindex] = 'd';
                }
                out.write("{");
                out.write(this.printTopics(tindex));

                out.write(this.getTopDoc(tindex, dnum, mylist, isvisit));
                out.write("\n}");
                if (i < hitTopic.size() - 1)
                    out.write(",\n");
                else
                    out.write("],\n");
            }

            // Order dependency topic by topic complexity.
            int endindex = 0;
            for (int i = 0; i < this.ordertopic.length; i++) {
                int tindex = ordertopic[i];
                if (isTopicVisit[tindex] == 'v' || isTopicVisit[tindex] == 'm')
                    continue;
                if (isTopicVisit[tindex] == 'd' && i > endindex)
                    endindex = i;
            }
            System.out.println(endindex);
            out.write("\"Dependency documents\": [\n\t");
            for (int i = 0; i < this.ordertopic.length; i++) {
                int tindex = ordertopic[i];
                if (isTopicVisit[tindex] == 'v' || isTopicVisit[tindex] == 'm')
                    continue;
                out.write("{");
                out.write(this.printTopics(tindex));
                out.write(this.getTopDoc(tindex, dnum, mylist, isvisit));
                out.write("\n}");
                if (i < endindex)
                    out.write(",\n");
                else
                    out.write("]\n");
            }

            String s = Dependency.getSubgraphInString(keyword);
            System.out.println(s);
            out.write("}");
            out.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String []args) {
        if (args.length < 6) {
            System.out.print("Usage [keyword] [doc2topic] [topickey] " +
                             "[topicgraph] [dockey] [pagerankfile] " +
                             "[docs/topic] [max_topic] [filterfile]");
            System.out.println("[PVfile] [Configure file]");
            System.exit(2);
        }
        int dnum = 3;
        int maxtnum = 5;
        String filterfile = "yes-no.csv";
        String configurefile = "config.txt";
        String pvfile = "doc_label_details_1390.txt";
        if (args.length > 6)
            dnum = Integer.parseInt(args[6]);
        if (args.length > 7)
            maxtnum = Integer.parseInt(args[7]);
        if (args.length > 8)
            filterfile = args[8];
        if (args.length > 9)
            pvfile = args[9];
        if (args.length > 10)
            configurefile = args[10];
        NewReadingList myreadinglist = new NewReadingList();
        // String keyword, String keyname, String pagerankfile,
        // String docfile, int dnum, String doc2conceptfile
        myreadinglist.readData(args[2], args[5], args[4], dnum, args[1],
                               filterfile);
        myreadinglist.readPV(pvfile);
        myreadinglist.readConfiguration(configurefile);
        // String keyword, String graphfile, int maxtopic, int dnum
        myreadinglist.run(args[0], args[3], maxtnum, dnum);
    }
}
