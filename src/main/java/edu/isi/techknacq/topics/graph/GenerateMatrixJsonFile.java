package edu.isi.techknacq.topics.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GenerateMatrixJsonFile {
    private ArrayList<String> User_id;
    private ArrayList<String> group;
    private Logger logger =
        Logger.getLogger(GenerateMatrixJsonFile.class.getName());

    public GenerateMatrixJsonFile() {
        User_id = new ArrayList<String> (8000);
        group = new ArrayList<String> (8000);
    }

    public void readNodeFile(String filename) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            //br.readLine();
            String id;
            String attr;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t");
                sc.next();
                id = sc.next();
                attr = sc.next();
                User_id.add(id);
                group.add(attr);
            }
            in1.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void readEdges(String filename, String output) {
        try {
            FileWriter fstream1 = new FileWriter(output,false);
            BufferedWriter out1 = new BufferedWriter(fstream1);
            out1.write("{\"directed\": false, \"graph\": [],");
            out1.write("\t\"nodes\":[\n");
            for (int i = 0; i < User_id.size(); i++) {
                out1.write("\t\t{");
                out1.write("\"group\":");
                out1.write(group.get(i) + ",");
                out1.write("\"name\":");
                out1.write("\"" + User_id.get(i) + "\"");
                out1.write("}");
                if (i < this.User_id.size() - 1)
                    out1.write(",\n");
                else
                    out1.write("\n],\n");
            }
            out1.write("\t\"links\":[\n");
            FileInputStream fstream = null;
            fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t");
                String i = sc.next();
                String j = sc.next();
                String value = sc.next();
                out1.write("\t\t{");
                out1.write("\"source\":");
                out1.write(i + ",");
                out1.write("\"target\":");
                out1.write(j + ",");
                out1.write("\"value\":");
                out1.write(value);
                out1.write("},\n");
            }
            out1.write("\t]\n}");
            out1.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String []args) {
        GenerateMatrixJsonFile myrun = new GenerateMatrixJsonFile();
        // keyword file for nodes
        myrun.readNodeFile(args[0]);
        // args[1]: edge file, args[2]: outputfile
        myrun.readEdges(args[1], args[2]);
    }
}
