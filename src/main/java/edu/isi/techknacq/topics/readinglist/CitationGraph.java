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
    private HashMap<String, Integer> paperIds;
    private ArrayList<String> paperNames;
    private ArrayList<Integer> []mycited;
    private double [][] paperToTopic;
    private int pnum = 0;
    private int tnum = 300;
    private Logger logger = Logger.getLogger(CitationGraph.class.getName());

    public CitationGraph() {
        this.paperIds = new HashMap<String, Integer>(100000);
        this.paperNames = new ArrayList<String>(100000);
    }

    public void setTopicNum(int _tnum) {
        tnum = _tnum;
    }

    public void setMaxFileNum(int _fnum) {
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
                if (!this.paperIds.containsKey(source)) {
                    this.paperIds.put(source, pnum);
                    sid = pnum;
                    pnum++;
                } else {
                    sid = this.paperIds.get(source);
                }
                if (!this.paperIds.containsKey(target)) {
                    this.paperIds.put(target, pnum);
                    tid = pnum;
                    pnum++;
                } else {
                    tid = this.paperIds.get(target);
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
            paperToTopic = new double[pnum][tnum];
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                docname = sc.next();
                // change '\\'(windows file) to '/' (Linux file)
                docname = docname.substring(docname.lastIndexOf('/') + 1,
                                            docname.length() - 4);
                // System.out.println(docname);
                if (!this.paperIds.containsKey(docname))
                    continue;
                did = this.paperIds.get(docname);
                // Getthelastname
                while (sc.hasNext()) {
                    topicname = sc.next();
                    int namelen = topicname.length();
                    index1 = topicname.indexOf("topic");
                    index2 = topicname.indexOf(":");
                    if (index1 >= 0 && index2 >= 0) {
                        tindex = Integer.parseInt(topicname.substring(index1 + 5,
                                                                      index2));
                        String tweight = topicname.substring(index2 + 1,
                                                             namelen);
                        paperToTopic[did][tindex] = Double.parseDouble(tweight);
                    }
                }
            }
            in1.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public double [][] computeCitationLinks() {
        double [][]topicToTopic = new double[tnum][tnum];
        for (int i = 0; i < pnum; i++) {
            // Paper id
            for (int j = 0; j < this.mycited[i].size(); j++) {
                int citedId = mycited[i].get(j);
                for (int k1 = 0; k1 < this.paperToTopic[i].length; k1++) {
                    for (int k2 = 0; k2 < this.paperToTopic[citedId].length;
                         k2++) {
                        topicToTopic[k1][k2] += paperToTopic[i][k1] +
                            paperToTopic[citedId][k2];
                    }
                }
            }
        }
        this.paperIds.clear();
        this.paperNames.clear();
        for (int i = 0; i < this.mycited.length; i++) {
            this.mycited[i].clear();
        }
        return topicToTopic;
    }
}
