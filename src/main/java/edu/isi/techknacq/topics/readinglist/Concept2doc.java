package edu.isi.techknacq.topics.readinglist;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import infodynamics.measures.continuous.kernel.EntropyCalculatorKernel;

import edu.isi.techknacq.topics.topic.Weightpair;

/**
 *
 * @author linhong
 */
public class Concept2doc {
    private int conceptnum; // number of topics
    private List []topic2docs;
    private ArrayList<String> docnames;
    private HashMap<String, Integer> badpaper = null;
    private Logger logger = Logger.getLogger(Concept2doc.class.getName());

    public void addFilter(String filename) {
        try {
            badpaper = new HashMap<String, Integer>(1000);
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String pid;
            int value;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t");
                pid = sc.next();
                value = sc.nextInt();
                if (value == 0) {
                    badpaper.put(pid,value);
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void initNum(int tnum) {
        conceptnum = tnum;
    }

    // Input: the filename for document to topic matrix
    // K: in integer
    // compute the top k documents for each topic
    public void add(int tindex, double tweight, int dindex, int K) {
        Weightpair w = new Weightpair(tweight, dindex);
        int index;
        if (topic2docs[tindex].size() < K) {
            index = Collections.binarySearch(topic2docs[tindex], w);
            if (index < 0)
                index = -index - 1;
            if (index < topic2docs[tindex].size())
                topic2docs[tindex].add(index, w);
            else
                topic2docs[tindex].add(w);
        } else {
            double minweight = ((Weightpair)topic2docs[tindex].get(topic2docs[tindex].size() - 1)).getweight();
            if (tweight > minweight) {
                index = Collections.binarySearch(topic2docs[tindex], w);
                if (index < 0)
                    index = -index - 1;
                if (index < topic2docs[tindex].size()) {
                    topic2docs[tindex].add(index, w);
                    topic2docs[tindex].remove(topic2docs[tindex].size() - 1);
                } else
                    topic2docs[tindex].set(topic2docs[tindex].size() - 1, w);
            }
        }
    }

    public void getTopK(int K, String filename) {
        try {
            topic2docs = new ArrayList[conceptnum];
            this.docnames = new ArrayList<String>(10000);
            for (int i = 0; i < conceptnum; i++) {
                topic2docs[i] = new ArrayList<Weightpair>(K + 2);
            }
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String docname;
            String topicname;
            int index1;
            int index2;
            int tindex;
            double tweight;
            int dindex = 0;

            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                docname = sc.next();
                // System.out.println(docname);
                // change '\\'(windows file) to '/' (Linux file)
                docname = docname.substring(docname.lastIndexOf('/') + 1,
                                            docname.length() - 4);
                // System.out.println(docname);
                // Getthelastname
                if ((this.badpaper != null &&
                     !this.badpaper.containsKey(docname)) ||
                    this.badpaper == null) {
                    docnames.add(docname);
                    while (sc.hasNext()) {
                        topicname = sc.next();
                        // System.out.println(topicname);
                        index1 = topicname.indexOf("topic");
                        index2 = topicname.indexOf(":");
                        if (index1 >= 0 && index2 >= 0) {
                            tindex = Integer.parseInt(topicname.substring(index1 + 5, index2));
                            tweight = Double.parseDouble(topicname.substring(index2 + 1, topicname.length()));
                            // System.out.println("tindex: " + tindex + " " +
                            //                    tweight);
                            add(tindex, tweight, dindex, K);
                        }
                    }
                    dindex++;
                }
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public List [] getTopic2Doc() {
        return this.topic2docs;
    }

    public ArrayList<Integer> getDocs(int tindex) {
        ArrayList<Integer> mydocs =
            new ArrayList<Integer>(topic2docs[tindex].size());
        for (int i = 0; i < topic2docs[tindex].size(); i++) {
            Weightpair o = (Weightpair)topic2docs[tindex].get(i);
            mydocs.add(o.getindex());
        }
        return mydocs;
    }

    public ArrayList<Double> prune() {
        try {
            EntropyCalculatorKernel entropy = new EntropyCalculatorKernel();
            entropy.initialise();
            ArrayList<Double> topicentropy = new ArrayList<Double>(conceptnum);
            // TransferEntropyCalculatorKraskov teCalc;
            // teCalc = new TransferEntropyCalculatorKraskov();
            // Use Kraskov parameter K=4 for 4 nearest neighbours
            // teCalc.setProperty("k", "4");
            // Use history length 1 (Schreiber k=1)
            // teCalc.initialise(1);
            double []v1 = new double[topic2docs[0].size()];
            for (int i = 0; i < this.conceptnum; i++) {
                int tindex = i;
                for (int j = 0; j < topic2docs[tindex].size(); j++) {
                    Weightpair o = (Weightpair)topic2docs[tindex].get(j);
                    v1[j] = o.getweight();
                }
                entropy.setObservations(v1);
                topicentropy.add(entropy.computeAverageLocalOfObservations());
            }
            // for (int i = 0; i < topicentropy.size(); i++) {
            //     System.out.println(topicentropy.get(i));
            // }
            return topicentropy;
            // teCalc.setObservations(v1, v2);
            // System.out.println(teCalc.computeAverageLocalOfObservations());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ArrayList<String> getDocName() {
        return this.docnames;
    }

    public static void main(String args[]) {
        Concept2doc doc = new Concept2doc();
        int tnum = 200;
        int K = 100;
        String filename = "concept2doc.txt";
        doc.initNum(tnum);
        doc.getTopK(K, filename);
        int tindex = 180;
        ArrayList<Integer> mydocs = doc.getDocs(tindex);
        ReadDocumentkey rdk = new ReadDocumentkey("acl-meta.json");
        rdk.readFile();
        for (int i = 0; i < mydocs.size(); i++) {
            // System.out.println(mydocs.get(i));
            String id = doc.docnames.get(mydocs.get(i));
            String docVal = rdk.getDocumentKey(id);
            // System.out.println("Retrieving:");
            System.out.println(id + " --- " + docVal);
        }
    }
}
