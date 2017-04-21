package edu.isi.techknacq.topics.graph;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.util.Pair;


public class Diffset {
    private Set<Pair> coverededges;
    private Logger logger = Logger.getLogger(Diffset.class.getName());

    public Diffset() {
        this.coverededges = new HashSet<Pair>(1000) {};
    }

    public void readCovered(String filename) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int sid;
            int tid;
            br.readLine();
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t");
                sid = sc.nextInt();
                sc.next();
                tid = sc.nextInt();
                Pair o = new Pair(sid,tid);
                this.coverededges.add(o);
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void readTopset(String filename) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int sid;
            int tid;
            br.readLine();
            while ((strline = br.readLine()) != null) {
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t");
                sid = sc.nextInt();
                sc.next();
                tid = sc.nextInt();
                Pair o = new Pair(sid,tid);
                Pair o1 = new Pair(tid,sid);
                if (!this.coverededges.contains(o) &&
                    !this.coverededges.contains(o1)) {
                    System.out.println(strline);
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String []args) {
        Diffset mydiff = new Diffset();
        mydiff.readCovered("evaluation-tsv.txt");
        mydiff.readTopset("topset.tsv");

    }
}
