package edu.isi.techknacq.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ReadBipartite {
    private HashMap<String, Integer> entities;
    private HashMap<String, Integer> topics;
    private Logger logger = Logger.getLogger(ReadBipartite.class.getName());

    public ReadBipartite() {
        this.entities = new HashMap<String, Integer>(10000);
        this.topics = new HashMap<String, Integer>(10000);
    }

    public void readFile(String filename) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String pid;
            String kid;
            int pnum = 1;
            int knum = 1;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter(",");
                pid = sc.next();
                kid = sc.next();
                if (!this.entities.containsKey(pid)) {
                    this.entities.put(pid, pnum);
                    pnum++;
                }
                if (!this.topics.containsKey(kid)) {
                    this.topics.put(kid, knum);
                    knum++;
                }
            }
            in1.close();
            fstream1 = new FileInputStream(filename);
            in1 = new DataInputStream(fstream1);
            br = new BufferedReader(new InputStreamReader(in1));
            int index1;
            int index2;
            double value;
            FileWriter fstream = new FileWriter(filename + "bipartite.txt",
                                                false);
            BufferedWriter out = new BufferedWriter(fstream);
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter(",");
                pid = sc.next();
                kid = sc.next();
                value = sc.nextDouble();
                index1 = this.entities.get(pid);
                index2 = this.topics.get(kid);
                out.write(index1 + "\t" + index2 + "\t" + value + "\n");
            }
            out.close();
            fstream = new FileWriter(filename + "entity.txt", false);
            out = new BufferedWriter(fstream);
            StrUtil.printMap(entities, out);
            fstream = new FileWriter(filename + "keyword.txt", false);
            out = new BufferedWriter(fstream);
            StrUtil.printMap(topics, out);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String []args) {
        ReadBipartite myrun = new ReadBipartite();
        myrun.readFile(args[0]);
    }
}
