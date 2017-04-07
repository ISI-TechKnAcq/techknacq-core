package edu.isi.techknacq.topics.readinglist;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author linhong
 */
public class CitationGraph {
    private HashMap<String, Integer> paperids;
    private ArrayList<String> papernames;
    private ArrayList<Integer> []mycited;
    private double [][] paper2topic;
    private int pnum = 0;
    private int tnum = 300;
    private Logger logger = Logger.getLogger(CitationGraph.class);

    public CitationGraph() {
        this.paperids = new HashMap<String, Integer>(100000);
        this.papernames = new ArrayList<String>(100000);
    }

    public void settopicnum(int _tnum) {
        tnum = _tnum;
    }

    public void setmaxfilenum(int _fnum) {
        mycited = new ArrayList[_fnum];
        for (int i = 0; i < mycited.length; i++)
            mycited[i] = new ArrayList<Integer>(10);
    }

    public void readCitation(String filename) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String source;
            String target;
            int sid;
            int tid;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter(" ==> ");
                source = sc.next();
                target = sc.next();
                if (!this.paperids.containsKey(source)) {
                    this.paperids.put(source, pnum);
                    sid = pnum;
                    pnum++;
                } else {
                    sid = this.paperids.get(source);
                }
                if (!this.paperids.containsKey(target)) {
                    this.paperids.put(target, pnum);
                    tid = pnum;
                    pnum++;
                } else {
                    tid = this.paperids.get(target);
                }
                if (sid >= mycited.length) {
                    System.out.println("SID is greater than limit");
                    System.out.println(sid);
                    System.exit(2);
                }
                mycited[sid].add(tid);
            }
            System.out.println("Finished:");
            System.out.println(pnum);
            in1.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    // Read concept composition for each document.
    public void readC2D(String filename) {
        try {
            FileInputStream fstream1 = new FileInputStream(filename);
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
            paper2topic = new double[pnum][tnum];
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                docname = sc.next();
                // change '\\'(windows file) to '/' (Linux file)
                docname = docname.substring(docname.lastIndexOf('/') + 1,
                                            docname.length()-4);
                // System.out.println(docname);
                if (!this.paperids.containsKey(docname))
                    continue;
                did = this.paperids.get(docname);
                // Getthelastname
                while (sc.hasNext()) {
                    topicname = sc.next();
                    int namelen = topicname.length();
                    index1 = topicname.indexOf("topic");
                    index2 = topicname.indexOf(":");
                    if (index1 >= 0 && index2 >= 0) {
                        tindex = Integer.parseInt(topicname.substring(index1+5,
                                                                      index2));
                        String tweight = topicname.substring(index2+1,
                                                             namelen);
                        paper2topic[did][tindex] = Double.parseDouble(tweight);
                    }
                }
            }
            in1.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public double [][] computeCitationLinks() {
        double [][]topic2topic = new double[tnum][tnum];
        for (int i = 0; i < pnum; i++) {
            // Paper id
            for (int j = 0; j < this.mycited[i].size(); j++) {
                int citedid = mycited[i].get(j);
                for (int k1 = 0; k1 < this.paper2topic[i].length; k1++) {
                    for (int k2 = 0; k2 < this.paper2topic[citedid].length;
                         k2++) {
                        topic2topic[k1][k2] += paper2topic[i][k1] +
                            paper2topic[citedid][k2];
                    }
                }
            }
        }
        this.paperids.clear();
        this.papernames.clear();
        for (int i = 0; i < this.mycited.length; i++) {
            this.mycited[i].clear();
        }
        return topic2topic;
    }
}
