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
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadflowNetwork {
    private ArrayList<String> keynames;
    // Help find the topic index with a hashmap structure
    private HashMap<String,Integer> keysearch;
    private double []keyvalues;
    private Logger logger = Logger.getLogger(ReadflowNetwork.class.getName());

    public void readKey(String filename) {
        try {
            this.keynames = new ArrayList<String>(200);
            this.keysearch = new HashMap<String,Integer>(200);
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int index;
            int linenum = 0;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t| ");
                sc.next();
                sc.next();
                index = 0;
                String name = "";
                String tempword;
                while (sc.hasNext() && index < 20) {
                    tempword = sc.next();
                    if ((!tempword.contains(name) && !name.contains(tempword))
                        || name.length() < 1) {
                        name += tempword;
                        name += "-";
                        index++;
                    }
                }
                this.keynames.add(name.substring(0, name.length() - 1));
                this.keysearch.put(name, linenum);
                linenum++;
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public int findTopicIndex(String str) {
        if (this.keysearch.containsKey(str))
            return this.keysearch.get(str);
        else
            return -1;
    }

    public void readNodeFlow(String filename) {
        try {
            this.keyvalues = new double[keynames.size()];
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            br.readLine();
            double score;
            String topicname;
            int index;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.next();
                score = Double.parseDouble(sc.next());
                topicname = sc.next();
                index = this.findTopicIndex(topicname);
                if (index != -1)
                    this.keyvalues[index] = score;
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public double [] readFlowScore(String filename) {
        try {
            this.keyvalues = new double[keynames.size()];
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            double score;
            String topicname;
            int index;
            while ((strline = br.readLine()) != null) {
                if (strline.startsWith("#"))
                    continue;
                Scanner sc = new Scanner(strline);
                sc.next();
                score = Double.parseDouble(sc.next());
                topicname = sc.next();
                index = this.findTopicIndex(topicname);
                if (index != -1)
                    this.keyvalues[index] = score;
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return keyvalues;
    }

    public void readFlowToMatrix(String filename, double[][]matrices) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int src = 0;
            int target;
            String value;
            int index1;
            int index2;
            int index3;
            while ((strline = br.readLine()) != null) {
                if (Character.isDigit(strline.charAt(0))) {
                    Scanner sc = new Scanner(strline);
                    src = sc.nextInt();
                } else {
                    index1 = strline.indexOf("-->");
                    if (index1 >= 0) {
                        index2 = strline.indexOf("(");
                        index3 = strline.indexOf(")");
                        target = Integer.parseInt(strline.substring(index1 + 4,
                                                                    index2 - 1));
                        value = strline.substring(index2+1, index3);
                        double v = Double.parseDouble(value);
                        if (v < 1.933e-005)
                            continue;
                        matrices[src][target] = v;
                    }
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void readFlowGraph(String filename, String outfilename) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int src = 0;
            int target;
            String value;
            FileWriter fstream = new FileWriter(outfilename, false);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("source_id\tsource_topic\ttarget_id\t" +
                      "target_topic\tedge_weight\n");
            FileWriter fstream2 = new FileWriter("digraph.txt", false);
            BufferedWriter out2 = new BufferedWriter(fstream2);
            // out2.write(keynames.size() + "\n");
            // for (int i = 0; i < keynames.size(); i++) {
            //     out2.write(keynames.get(i) + "\n");
            // }
            int index1;
            int index2;
            int index3;
            while ((strline = br.readLine()) != null) {
                if (Character.isDigit(strline.charAt(0))) {
                    Scanner sc = new Scanner(strline);
                    src = sc.nextInt();
                } else {
                    index1 = strline.indexOf("-->");
                    if (index1 >= 0) {
                        index2 = strline.indexOf("(");
                        index3 = strline.indexOf(")");
                        target = Integer.parseInt(strline.substring(index1 + 4,
                                                                    index2 - 1));
                        value = strline.substring(index2 + 1, index3);
                        System.out.println(src + "\t" + target + "\t" + value);
                        double v = Double.parseDouble(value);
                        if (v < 1.933e-005)
                            continue;
                        if (this.keyvalues[src] < this.keyvalues[target]) {
                            out.write(src + "\t" + keynames.get(src) + "\t" +
                                      target + "\t" + keynames.get(target) +
                                      "\t" + value + "\n");
                            out2.write(src + "\t" + target + "\t" + value +
                                       "\n");
                        } else {
                            out.write(target + "\t" + keynames.get(target) +
                                      "\t" + src + "\t" + keynames.get(src) +
                                      "\t" + value + "\n");
                            out2.write(target + "\t" + src + "\t" + value +
                                       "\n");
                        }
                    }
                    // else {
                    //     index1 = strline.indexOf("<--");
                    //     index2 = strline.indexOf("(");
                    //     index3 = strline.indexOf(")");
                    //     target = Integer.parseInt(strline.substring(index1+4,
                    //                               index2-1));
                    //     value = strline.substring(index2+1, index3);
                    //     out.write(keynames.get(target) + "\t" +
                    //               keynames.get(src)+"\t"+value+"\n");
                    // }
                }
            }
            out.close();
            out2.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String []args) {
        if (args.length < 1) {
            System.out.println("Usage: [keyfilename] [treefilename] " +
                               "[flowfilename] [outputfilename]");
            System.exit(2);
        }
        ReadflowNetwork myreader = new ReadflowNetwork();
        myreader.readKey(args[0]);
        myreader.readNodeFlow(args[1]);
        myreader.readFlowGraph(args[2], args[3]);
        // ReadflowNetwork myreader = new ReadflowNetwork();
        // myreader.readKey("mallet-keys.txt");
        // myreader.readNodeFlow("Topicmallet12.tree");
        // myreader.readFlowGraph("Topicmallet12flow.txt");
    }
}
