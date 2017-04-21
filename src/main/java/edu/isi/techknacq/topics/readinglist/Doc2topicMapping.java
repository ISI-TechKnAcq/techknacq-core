package edu.isi.techknacq.topics.readinglist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.topic.Indexpair;
import edu.isi.techknacq.topics.topic.Weightpair;
import edu.isi.techknacq.topics.util.ReadWeightedTopicKey;
import edu.isi.techknacq.topics.util.StrUtil;

/**
 *
 * @author linhong
 */
public class Doc2topicMapping {
    private List []paper2topic;
    private HashMap<String, Integer> paperids;
    private ArrayList<String> papernames;
    private int pnum = 0;
    private int tnum;
    private ArrayList<String> words;
    private List []topicinwords;
    private Logger logger = Logger.getLogger(Doc2topicMapping.class.getName());

    // Read concept composition for each document
    public void add(int pid, int tindex, double weight, int K) {
        Weightpair w = new Weightpair(weight, tindex);
        int index;
        if (paper2topic[pid].size() < K) {
            index = Collections.binarySearch(paper2topic[pid], w);
            if (index < 0)
                index = -index - 1;
            if (index < paper2topic[pid].size())
                paper2topic[pid].add(index, w);
            else
                paper2topic[pid].add(w);
        } else {
            double minweight = ((Weightpair)paper2topic[pid].get(paper2topic[pid].size()-1)).getweight();
            if (weight > minweight) {
                index = Collections.binarySearch(paper2topic[pid], w);
                if (index < 0)
                    index = -index - 1;
                if (index < paper2topic[pid].size()) {
                    paper2topic[pid].add(index, w);
                    paper2topic[pid].remove(paper2topic[pid].size() - 1);
                }
                else
                    paper2topic[pid].set(paper2topic[pid].size() - 1, w);
            }
        }
    }

    public void readC2D(String filename) {
        try {
            FileInputStream fstream1;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String docname;
            String topicname;
            int index1;
            int index2;
            int did;
            int tindex;
            double tweight;
            paperids = new HashMap(18000);
            papernames = new ArrayList<String>(18000);
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                docname = sc.next();
                // Change '\\'(windows file) to '/' (Linux file)
                docname = docname.substring(docname.lastIndexOf('/') + 1,
                                            docname.length() - 4);
                if (!this.paperids.containsKey(docname)) {
                    this.paperids.put(docname, pnum);
                    pnum++;
                    this.papernames.add(docname);
                }
            }
            in1.close();
            fstream1.close();
            fstream1 = new FileInputStream(filename);
            in1 = new DataInputStream(fstream1);
            br = new BufferedReader(new InputStreamReader(in1));
            paper2topic = new ArrayList[pnum];
            for (int i = 0; i < pnum; i++) {
                paper2topic[i] = new ArrayList<Weightpair>(10);
            }
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                docname = sc.next();
                // change '\\'(windows file) to '/' (Linux file)
                docname = docname.substring(docname.lastIndexOf('/') + 1,
                                            docname.length() - 4);
                // System.out.println(docname);
                if (!this.paperids.containsKey(docname))
                    continue;
                did = this.paperids.get(docname);
                // Getthelastname
                while (sc.hasNext()) {
                    topicname = sc.next();
                    index1 = topicname.indexOf("topic");
                    index2 = topicname.indexOf(":");
                    if (index1 >= 0 && index2 >= 0) {
                        tindex = Integer.parseInt(topicname.substring(index1+5,
                                                                      index2));
                        tweight = Double.parseDouble(topicname.substring(index2+1,
                                                                         topicname.length()));
                        this.add(did, tindex, tweight,10);
                    }
                }
            }
            in1.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void run(String c2dfile, String keyfile) {
        FileWriter fstream = null;
        try {
            ReadWeightedTopicKey myreader = new ReadWeightedTopicKey();
            myreader.read(keyfile, 20);
            myreader.conceptToWords(keyfile);
            this.topicinwords = myreader.getConceptInWord();
            this.words = myreader.getWordList();
            tnum = this.topicinwords.length;
            this.readC2D(c2dfile);
            List orderword = new ArrayList<Weightpair> (100);
            HashMap<Integer, Double> wordweight;
            wordweight = new HashMap<Integer, Double>(100);
            fstream = new FileWriter("Topicspotfull.txt",false);
            BufferedWriter out = new BufferedWriter(fstream);
            for (int i = 0; i < pnum; i++) {
                double w=0;
                wordweight.clear();
                for (int j = 0; j < paper2topic[i].size(); j++) {
                    Weightpair o = (Weightpair)paper2topic[i].get(j);
                    int tindex = o.getindex();
                    w += o.getweight();
                    double w2 = 0.0;
                    for (int k = 0; k < this.topicinwords[tindex].size();
                         k++) {
                       Indexpair p = (Indexpair)topicinwords[tindex].get(k);
                        int windex = p.getindex();
                        if (!wordweight.containsKey(windex)) {
                            wordweight.put(windex,
                                           p.getweight() * o.getweight());
                        } else {
                            double oldvalue = wordweight.get(windex);
                            wordweight.put(windex,
                                           p.getweight() * o.getweight() +
                                           oldvalue);
                        }
                        w2 += p.getweight();
                        if (w2 > 0.6)
                            break;
                    }
                    if (w > 0.52)
                        break;

                }
                orderword.clear();
                StrUtil.enumerateMap(wordweight, orderword);
                Collections.sort(orderword);
                out.write(this.papernames.get(i));
                for (int j = 0; j < orderword.size(); j++) {
                    Weightpair p = (Weightpair)orderword.get(j);
                    int windex = p.getindex();
                    out.write("\t" + this.words.get(windex) + ":" +
                              p.getweight());
                }
                out.write("\n");
            }
            out.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String []args) {
        if (args.length < 1) {
            System.out.println("Usage: [topic composition for documens file] " +
                               "[topic weighted key file]");
            System.exit(2);
        }
        Doc2topicMapping mytopic = new Doc2topicMapping();
        mytopic.run(args[0], args[1]);
        // mytopic.run("mallet-comp.txt", "mallet-15689-weightedkey.txt");
    }

}
