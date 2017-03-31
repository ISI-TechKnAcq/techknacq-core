package edu.isi.techknacq.topics.graph;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EdgeEval {
    private double [][]CEdoc;
    private double [][]CEword;
    private double [][]IF;
    private double [][]simword;
    private double [][]simdoc;
    private double [][]cite;
    private double [][]citewang;
    private double [][]hierword;
    private Logger logger = Logger.getLogger(EdgeEval.class);

    public void settopicnum(int _tnum) {
        int topicnum = _tnum;
        this.CEdoc = new double[topicnum][topicnum];
        this.IF = new double[topicnum][topicnum];
        this.simword = new double[topicnum][topicnum];
        this.simdoc = new double[topicnum][topicnum];
        this.CEword = new double[topicnum][topicnum];
        this.citewang = new double[topicnum][topicnum];
        this.hierword = new double[topicnum][topicnum];
        this.cite = new double[topicnum][topicnum];
    }

    public void readScores(String filename) {
        try {
            FileInputStream fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int id;
            int tid;
            // double cedoc;
            double ifscore;
            double simwordscore;
            double simdocscore;
            double cecite;
            double ceword;
            double hword;
            double hdoc;
            br.readLine();
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t");
                id = sc.nextInt();
                sc.next();
                tid = sc.nextInt();
                sc.next();
                simdocscore = sc.nextDouble(); // simdoc
                simwordscore = sc.nextDouble(); // simword
                ifscore = sc.nextDouble(); // if
                // System.out.println(ifscore);
                // cedoc = sc.nextDouble(); // CEdoc
                ceword = sc.nextDouble(); // CEword
                cecite = sc.nextDouble(); // citation
                hword = sc.nextDouble(); // hierdoc
                hdoc = sc.nextDouble();
                // hdoc = sc.nextDouble();
                // CEdoc[id][tid] = cedoc;
                // CEdoc[tid][id] = -cedoc;
                CEword[id][tid] = ceword;
                CEword[tid][id] = -ceword;
                if (ifscore > 0.0) {
                    this.IF[id][tid] = ifscore;
                    this.IF[tid][id] = 0.0 - ifscore;
                }
                this.simword[id][tid] = simwordscore;
                this.simword[tid][id] = simwordscore;
                this.simdoc[id][tid] = simdocscore;
                this.simdoc[tid][id] = simdocscore;
                this.cite[id][tid] = cecite;
                this.hierword[id][tid] = hword;
                this.citewang[id][tid] = hdoc;
                // this.hierdoc[id][tid] = hdoc;
                // this.hierdoc[tid][id] = -hdoc;
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void readEvaluation(String filename) {
        try {
            FileInputStream fstream1;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int sid;
            int tid;
            System.out.println("id\ttid\tsimdoc\tsimword\tIF\tCE\tcite\t" +
                               "hier\tcite_wang\t" + br.readLine());
            while ((strline = br.readLine()) != null) {
                strline = strline.replaceAll(" ", "");
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t");
                // id = sc.next();
                // Scanner sc2 = new Scanner(id);
                // sc2.useDelimiter("-depend-on-");
                sid = sc.nextInt();
                tid = sc.nextInt();
                System.out.println(sid + "\t" + tid + "\t" +
                                   this.simdoc[sid][tid] + "\t" +
                                   this.simword[sid][tid] + "\t" +
                                   this.IF[sid][tid] + "\t" +
                                   this.CEword[sid][tid] + "\t" +
                                   this.cite[sid][tid] + "\t" +
                                   this.hierword[sid][tid] + "\t" +
                                   citewang[sid][tid] + "\t" + strline);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String []args) {
        if (args.length < 1) {
            System.out.println("Usage: [all-edge-files] [evaluation-file] " +
                               "[tnum]");
            System.exit(2);
        }
        EdgeEval myEval = new EdgeEval();
        myEval.settopicnum(Integer.parseInt(args[2]));
        myEval.readScores(args[0]);
        myEval.readEvaluation(args[1]);
        // myEval.readScores("alledge.tsv");
        // myEval.readEvaluation("2016-02-26.12-40.dependency.tsv");
    }
}
