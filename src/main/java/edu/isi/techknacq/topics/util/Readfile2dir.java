package edu.isi.techknacq.topics.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Readfile2dir {
    private Logger logger = Logger.getLogger(Readfile2dir.class.getName());

    public void parseTopicVector(String filename, int k) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String id;
            String file;
            int label;
            double va;
            FileWriter fstream = new FileWriter(filename + "_new.txt", false);
            BufferedWriter out = new BufferedWriter(fstream);
            double []temp = new double[k];
            br.readLine();
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t");
                id = sc.next();
                file = sc.next();
                while (sc.hasNext()) {
                    label = sc.nextInt();
                    va = sc.nextDouble();
                    temp[label] = va;
                }
                for (int i = 0; i < temp.length; i++) {
                    if (i == temp.length - 1)
                        out.write(temp[i] + "\n");
                    else
                        out.write(temp[i] + "\t");
                }
            }
            out.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void readFile(String filename, String dirname) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int id;
            String year;
            String content;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter(",");
                id = sc.nextInt();
                year = sc.next();
                sc.nextInt();
                content = sc.next();
                if (year.compareTo("2004") <= 0) {
                    String fname = dirname + "/" + id + ".txt";
                    FileWriter fstream = new FileWriter(fname, false);
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(content + "\n");
                    out.close();
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
        if (args.length < 1) {
            System.out.println("Usage: 1 [topic composition file] " +
                               "[words per topic]");
            System.out.println("Usage: 2 [text file] " +
                               "[prefix for output file]");
            System.exit(2);
        }
        Readfile2dir myreader = new Readfile2dir();
        int type = Integer.parseInt(args[0]);
        if (type == 1) {
            myreader.parseTopicVector(args[1], Integer.parseInt(args[2]));
            // Example:
            //myreader.parseTopicVector("doc2topic.txt", 20);
        } else {
            myreader.readFile(args[1], args[2]);
            // Example:
            //myreader.readFile("preprocessed_text.csv", "abstract2004");
        }
    }
}
