package edu.isi.techknacq.topics.graph;

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


public class Graphformat {
    private ArrayList<String> keyname;
    private Logger logger = Logger.getLogger(Graphformat.class);

    public void readKey(String keyfilename) {
        keyname = new ArrayList<String>(200);
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(keyfilename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int index = 0;
            while ((strline = br.readLine())!=null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t| ");
                sc.next();
                sc.next();
                index = 0;
                String name = "";
                String tempword;
                while (sc.hasNext() && index < 5) {
                    tempword = sc.next();
                    if ((!tempword.contains(name) &&
                         !name.contains(tempword)) || name.length() < 1) {
                        name += tempword;
                        name += "-";
                        index++;
                    }
                }
                this.keyname.add(name);
            }
            in1.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void readMatrix(String matrixfilename, String outfilename) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(matrixfilename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int lineindex;
            int columnindex;
            int weight;
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter(outfilename, false);
            out = new BufferedWriter(fstream);
            out.write("*Vertices " + keyname.size() + "\n");
            for (int i = 0; i < keyname.size(); i++) {
                out.write(i+1+" \""+keyname.get(i)+"\"\n");
            }
            out.write("*Edges ");
            int edgenum = 0;
            lineindex = 1;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t| ");
                columnindex = 1;
                while (sc.hasNext()) {
                    weight = sc.nextInt();
                    if (weight > 0 && columnindex > lineindex) {
                        edgenum++;
                    }
                    columnindex++;
                }
                lineindex++;
            }
            out.write(edgenum+"\n");
            in1.close();

            // Reopen the file to read
            fstream1 = new FileInputStream(matrixfilename);
            // Get the object of DataInputStream
            in1 = new DataInputStream(fstream1);
            br = new BufferedReader(new InputStreamReader(in1));
            lineindex=1;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t| ");
                columnindex = 1;
                while (sc.hasNext()) {
                    weight = sc.nextInt();
                    if (weight > 0 && columnindex > lineindex) {
                        out.write(lineindex+" "+columnindex+" "+weight+"\n");
                    }
                    columnindex++;
                }
                lineindex++;
            }
            in1.close();
            out.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String []args) {
        if (args.length < 1) {
            System.out.println("Usage: [keyfilename] [matrixfilename] " +
                               "[outputfilename]");
            System.exit(2);
        }
        Graphformat mygraph = new Graphformat();
        // args[0]:keyfilename
        // args[1]: co-occurrence matrix file name
        // args[2]:output file name
        mygraph.readKey(args[0]);
        mygraph.readMatrix(args[1],args[2]);
        // Graphformat mygraph = new Graphformat();
        // mygraph.readKey("mallet-keys.txt");
        // mygraph.readMatrix("co-occurrence.txt", "Topicmallet12.net");
    }
}
