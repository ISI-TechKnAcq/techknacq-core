package edu.isi.techknacq.topics.readinglist;

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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.graph.Conceptdepth;
import edu.isi.techknacq.topics.graph.Node;
import edu.isi.techknacq.topics.graph.ReadGraph;
import edu.isi.techknacq.topics.topic.Weightpair;
import edu.isi.techknacq.topics.topic.WordPair;


public class ReadingList2 {
    private Map<String, Double> paperpagerank;
    private ArrayList<ArrayList<WordPair>> wordintopic;
    private List<String> topickeys;
    private List<Integer> hittopic;
    private Map<String, String> docmap;
    private List []topic2docs;
    private ArrayList<String> docfiles;
    private int []ordertopic;
    private Set<String> authorlists;
    private Logger logger = Logger.getLogger(BaselineReadingList.class);

    public void readData(String keyword, String keyname, String pagerankfile,
                         String docfile, int dnum, String doc2conceptfile,
                         String filterfile) {
        Keyword2concept match1 = new Keyword2concept();
        match1.readKey(keyname);
        hittopic = match1.getMatch(keyword);
        this.wordintopic = match1.getWeightTopic();
        this.topickeys = match1.getTopics();
        readPageRankScore(pagerankfile);
        ReadDocumentkey rdk = new ReadDocumentkey(docfile);
        rdk.readFile();
        docmap = rdk.GetDocmap();
        Concept2doc Getdoc = new Concept2doc();
        Getdoc.initNum(topickeys.size());
        Getdoc.addFilter(filterfile);
        Getdoc.getTopK(dnum * 10, doc2conceptfile);
        topic2docs = Getdoc.getTopic2Doc();
        docfiles = Getdoc.getDocName();
    }

    public String getDocMeta(String id) {
        if (this.docmap.containsKey(id))
            return this.docmap.get(id);
        else
            return null;
    }

    public ArrayList<Integer> getDocs(int tindex) {
        ArrayList<Integer> mydocs = new ArrayList(topic2docs[tindex].size());
        for (int i = 0; i < topic2docs[tindex].size(); i++) {
            Weightpair o = (Weightpair)topic2docs[tindex].get(i);
            mydocs.add(o.getindex());
        }
        return mydocs;
    }

    public String floatToString(double val, double max) {
        int color_dec = (int)(255 * (1 - val/max));
        String str = Integer.toHexString(color_dec);
        if (str.length() > 1)
            str = "#" + str + str + str;
        else
            str = "#" + 0 + str + 0 + str + 0 + str;
        return str;
    }

    public String printTopics(int tindex) {
        String topicname = "<blockquote><p>";
        double minvalue = 1;
        double maxvalue = 0;
        for (int i = 0; i < this.wordintopic.get(tindex).size(); i++) {
            WordPair w = wordintopic.get(tindex).get(i);
            double value = w.getprob();
            if (value > maxvalue)
                maxvalue = value;
            if (value < minvalue) {
                minvalue = value;
            }
        }
        for (int i = 0; i < this.wordintopic.get(tindex).size(); i++) {
            WordPair w = wordintopic.get(tindex).get(i);
            String word = w.getWord();
            double value = w.getprob();
            if (word.startsWith("#") && word.endsWith("#")) {
                word = word.replace("#", "");
                topicname += "<a href=\"http://wikipedia.org/w/index.php?" +
                    "search=" + word.replace("_", "+") + "\">" +
                    "<span style=\"color: " + floatToString(value, maxvalue) +
                    "\" title=\"" + (int)(value * 100) + "% relevant\">" +
                    word.replace("_", "&nbsp;") + "</span></a>, ";
            } else {
                topicname += "<span style=\"color:" +
                    floatToString(value, maxvalue) +
                    "\" title=\"" + (int)(value*100) + "% relevant\">" +
                    word.replace("_", "&nbsp;") + "</span>, ";
            }
        }
        topicname = topicname.substring(0, topicname.length() - 2);
        topicname += "</p></blockquote>";
        return topicname;
    }

    public String extractAuthor(String metadata) {
        int index1 = metadata.indexOf("author:");
        int index2 = metadata.indexOf("title:");
        String author = null;
        if (index1 >= 0 && index2 >= 0) {
            author = metadata.substring(index1 + 8, index2);
        }
        return author;
    }

    public String printDocName(String metadata, String did, double score) {
        int index1 = metadata.indexOf("author:");
        int index2 = metadata.indexOf("title:");
        String author = null;
        String title = null;
        if (index1 >= 0 && index2 >= 0) {
            author = metadata.substring(index1 + 8,index2);
        }
        if (index2 >= 0) {
            title = metadata.substring(index2 + 7, metadata.length());
        }
        String name = author + ": <a href=\"http://www.aclweb.org/anthology/" +
            did.charAt(0) + "/" + did.substring(0, 3) + "/" + did + ".pdf\">" +
            title + "</a> (" + score + ")";
        name = name.replace(" A ", " a ");
        name = name.replace(" Of ", " of ");
        name = name.replace(" As ", " as ");
        name = name.replace(" The ", " the ");
        name = name.replace(" To ",  " to ");
        name = name.replace(" And ", " and ");
        name = name.replace(" For ",  " for ");
        name = name.replace(" In ",  " in ");
        name = name.replace(" With ", " with ");
        name = name.replace(" By ", " by ");
        name = name.replace(" On ", " on ");
        name = name.replace(" - ", " &ndash; ");
        name = name.replace(" -- ", " &ndash; ");
        return name;
    }

    public void run(String keyword, String graphfile, int maxtopic, int dnum) {
        try {
            FileWriter fstream = new FileWriter(keyword + "_readinglist.html",
                                                false);
            BufferedWriter out = new BufferedWriter(fstream);
            ReadGraph myreader = new ReadGraph(graphfile);
            Node []G = myreader.getGraph();
            this.ordertopic = myreader.orderNode();
            Conceptdepth Dependency = new Conceptdepth();
            Dependency.initGraph(G);
            Dependency.initTopics(this.topickeys);
            // List mylist = new ArrayList<>(100);
            // boolean []isvisit = new boolean[this.docfiles.size()];
            // for (int i = 0; i < isvisit.length; i++) {
            //     isvisit[i] = false;
            // }
            // double value;
            // for (Integer tindex : this.hittopic) {
            //     ArrayList<Integer> mydocs = this.getDocs(tindex);
            //     for (Integer mydoc : mydocs) {
            //         int Did = mydoc;
            //         if (!isvisit[Did])
            //             isvisit[Did] = true;
            //         else
            //             continue;
            //         String dockey = this.docfiles.get(Did);
            //         if (this.paperpagerank.containsKey(dockey))
            //             value = this.paperpagerank.get(dockey);
            //         else
            //             value = -1;
            //         if (value > -1)
            //             mylist.add(new Weightpair(value,Did));
            //     }
            //     ArrayList<Integer> deptopics;
            //     deptopics = Dependency.getTopNode(maxtopic, tindex);
            //     for (Integer ddtopic : deptopics) {
            //         ArrayList<Integer> dddocs = this.getDocs(ddtopic);
            //         for (Integer mydoc : dddocs) {
            //             if (!isvisit[mydoc])
            //                 isvisit[mydoc] = true;
            //             else
            //                 continue;
            //             String dockey = this.docfiles.get(mydoc);
            //             if (this.paperpagerank.containsKey(dockey))
            //                 value = this.paperpagerank.get(dockey);
            //             else
            //                 value = -1;
            //             if (value > -1)
            //                 mylist.add(new Weightpair(value,mydoc));
            //         }
            //     }
            // }
            // Collections.sort(mylist);
            // for (int i = 0; i < isvisit.length; i++) {
            //     isvisit[i] = false;
            // }
            // int maxdoc = maxtopic * dnum * 3;
            // maxdoc += this.hittopic.size() * dnum * 4;
            // for (int i = 0; i < maxdoc && i < mylist.size(); i++) {
            //     Weightpair o = (Weightpair)mylist.get(i);
            //     int Did = o.getindex();
            //     isvisit[Did] = true;
            // }
            String html = "
<html>
<head>
<title>TechKnAcq Reading List</title>
<style type=\"text/css\">
body {
    margin: 2em auto;
    font-family: 'Univers LT Std', 'Helvetica', sans-serif;
    max-width: 900px;
    width: 90%;
}
article {
    border-top: 4px solid #888;
    padding-top: 3em;
    margin-top: 3em;
}
section {
    padding-bottom: 3em;
    border-bottom: 4px solid #888;
    margin-bottom: 4em;
}
section section {
    border: 0px;
    padding: 0px;
    margin: 0em 0em 3em 0em;
}
h1 { font-size: 18pt; }
h2 { font-size: 14pt; }
label { margin-right: 6px; }
input { margin-left: 6px; }
div.topic {
    padding: 1em;
}
p.rate { font-weight: bold; margin-left: 2em; }
blockquote { margin-left: 40px; }
a {
    text-decoration: none;
    font-style: italic;
    border-bottom: 1px dotted grey;
}
a:hover { color: blue !important; }
a:hover span { color: blue !important; }
</style>
</head>
<body>
<h1>Reading List for " + keyword + " </h1>";
            out.write(html);
            boolean []isvisit = new boolean[this.docfiles.size()];
            for (int i = 0; i < isvisit.length; i++) {
                isvisit[i] = false;
            }
            /*
             * Get matched topic and dependent topics
             */
            char []istopicvisit = new char[this.topickeys.size()];
            Arrays.fill(istopicvisit, 'v');
            List mylist = new ArrayList(100);
            this.authorlists = new HashSet();

            for (int i = 0; i < hittopic.size(); i++) {
                int tindex = hittopic.get(i);
                istopicvisit[tindex] = 'm';
                if (i == 0) {
                    istopicvisit[tindex] = 'v';
                    // Put the topic tindex at the very begining of the order
                    // list
                    out.write("<section>");
                    out.write("<h2>Overall topic: "+"</h2>");
                    out.write("<div class=\"topic\">");
                    out.write(this.printTopics(tindex));
                    /*
                     * Start retrival high quality papers;
                     */
                    ArrayList<Integer> mydocs=this.getDocs(tindex);

                    mylist.clear();
                    for (Integer mydoc : mydocs) {
                        int Did = mydoc;
                        if (isvisit[Did])
                            continue;
                        String dockey=this.docfiles.get(Did);
                        double value;
                        if (this.paperpagerank.containsKey(dockey))
                            value=this.paperpagerank.get(dockey);
                        else
                            value = -1;
                        if (value > -1)
                            mylist.add(new Weightpair(value,Did));
                    }
                    int dcount = Math.min(dnum, mylist.size());
                    out.write("<p>The best relevant " + dcount +
                              " documents: </p>");
                    int j = 0;
                    dcount = 0;
                    Collections.sort(mylist);
                    while (dcount < dnum && j < mylist.size() &&
                           dcount < mylist.size()) {
                        Weightpair o = (Weightpair)mylist.get(j);
                        int Did = o.getindex();
                        isvisit[Did] = true;
                        String dfile = docfiles.get(Did);
                        String metavalue = this.getDocMeta(dfile);
                        String author = this.extractAuthor(metavalue);
                        if (!this.authorlists.contains(author)) {
                            String name = this.printDocName(metavalue, dfile,
                                                            o.getweight());
                            out.write("<li>"+name+"</li>");
                            this.authorlists.add(author);
                            dcount++;
                        }
                        j++;
                    }
                    out.write("</div>");
                    out.write("</section>");
                }
                ArrayList<Integer> deptopics;
                deptopics = Dependency.getTopNode(maxtopic, tindex);
                for (Integer deptopic : deptopics) {
                    int ddtindex = deptopic;
                    if (istopicvisit[ddtindex] != 'm')
                        istopicvisit[ddtindex] = 'd';
                }
            }
            Dependency.getSubgraph(keyword);
            // Order topics by knowledge complexity
            for (int i = 0; i < this.ordertopic.length; i++) {
                int tindex = ordertopic[i];
                if (istopicvisit[tindex] == 'v')
                    continue;
                out.write("<section>");
                if (istopicvisit[tindex] == 'm') {
                    out.write("<h2>Matched topic: </h2>");
                } else{
                    out.write("<h2>Dependency topic: </h2>");
                }
                out.write("<div class=\"topic\">");
                out.write(this.printTopics(tindex));
                /*
                 * Start retrival high quality papers;
                 */
                ArrayList<Integer> mydocs = this.getDocs(tindex);
                mylist.clear();
                for (Integer mydoc : mydocs) {
                    int Did = mydoc;
                    if (isvisit[Did])
                        continue;
                    String dockey = this.docfiles.get(Did);
                    double value;
                    if (this.paperpagerank.containsKey(dockey))
                        value = this.paperpagerank.get(dockey);
                    else
                        value = -1;
                    if (value > -1)
                        mylist.add(new Weightpair(value,Did));
                }
                int dcount = Math.min(dnum, mylist.size());
                out.write("<p>The best relevant "+dcount+" documents: </p>");
                int j = 0;
                dcount = 0;
                Collections.sort(mylist);
                while (dcount < dnum && j < mylist.size() &&
                       dcount < mylist.size()) {
                    Weightpair o = (Weightpair)mylist.get(j);
                    int Did = o.getindex();
                    isvisit[Did] = true;
                    String dfile = docfiles.get(Did);
                    String metavalue = this.getDocMeta(dfile);
                    String author = this.extractAuthor(metavalue);
                    if (!this.authorlists.contains(author)) {
                        String name = this.printDocName(metavalue, dfile,
                                                        o.getweight());
                        out.write("<li>" + name + "</li>");
                        this.authorlists.add(author);
                        dcount++;
                    }
                    j++;
                }
                out.write("</div>");
                out.write("</section>");
            }
            out.write("</form>\n" +
                      "</article>\n" +
                      "</body>\n" +
                      "</html>");
            out.close();
            Dependency.getSubgraph(keyword);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void readPageRankScore(String filename) {
        try {
            this.paperpagerank = new HashMap(this.topickeys.size());
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
                if (!this.paperpagerank.containsKey(keyname)) {
                    this.paperpagerank.put(keyname, value);
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String []args) {
        // args[0]: keyword;
        // args[1]: doc2topicfilename;
        // args[2]: topicweightedkeyname;
        // args[3]: topicgraphfilename;
        // args[4]: dockeyname;
        // args[5]: number of docs per topic
        // args[6]: number of maximum dependence topics;
        if (args.length < 6) {
            System.out.println("Usage [keyword] [doc2topic] [topickey] " +
                               "[topicgraph] [dockey] [pagerankfile] " +
                               "[docs/topic] [max_topic] [filterfile]");
            System.exit(2);
        }
        int dnum = 3;
        int maxtnum = 10;
        String filterfile = "yes-no.csv";
        if (args.length > 6)
            dnum = Integer.parseInt(args[6]);
        if (args.length > 7)
            maxtnum = Integer.parseInt(args[7]);
        if (args.length > 8)
            filterfile = args[8];
        ReadingList2 myreadinglist = new ReadingList2();
        // String keyword, String keyname, String pagerankfile, String docfile,
        // int dnum, String doc2conceptfile
        myreadinglist.readData(args[0], args[2], args[5], args[4], dnum,
                               args[1], filterfile);
        // String keyword, String graphfile, int maxtopic, int dnum
        myreadinglist.run(args[0], args[3], maxtnum, dnum);
    }
}
