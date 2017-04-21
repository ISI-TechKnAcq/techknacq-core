package edu.isi.techknacq.topics.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Readresults {
    private Logger logger = Logger.getLogger(Readresults.class.getName());

    public void readD2topic(String filename, ArrayList<String> filenames,
                            BufferedWriter out) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int linenum = 0;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                out.write(filenames.get(linenum));
                int index = 0;
                while (sc.hasNext()) {
                    out.write("\ttopic" + index + ":" + sc.next());
                    index++;
                }
                out.write("\n");
                linenum++;
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void topicword(String fname) {
        FileWriter fstream = null;
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(fname);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int tnum = 0;
            BufferedWriter out = null;
            while ((strline = br.readLine()) != null) {
                if (strline.startsWith("topic")) {
                    tnum++;
                    String prefix = fname.substring(0, fname.lastIndexOf('.'));
                    fstream = new FileWriter(prefix + tnum + ".csv", false);
                    out = new BufferedWriter(fstream);
                    out.write("\"name\",\"count\"\n");
                    continue;
                } else if (strline.length() < 1) {
                    if (out != null)
                        out.close();
                } else {
                    out.write(strline + "\n");
                }
            }
            if (out != null)
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
        Readresults myreader = new Readresults();
        myreader.topicword("techtopic.txt");
    }
}
