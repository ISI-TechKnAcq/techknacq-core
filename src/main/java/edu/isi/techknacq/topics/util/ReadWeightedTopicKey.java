package edu.isi.techknacq.topics.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.topic.IndexPair;

public class ReadWeightedTopicKey {
    private ArrayList<String> keynames;
    private HashMap<String, Integer> words;
    private List []topicinwords;
    private ArrayList<String> wordlist;
    private Logger logger = Logger.getLogger(ReadTopicKey.class.getName());

    public void read(String filename, int maxcount) {
        int wordcount = 0;
        try {
            if (this.words == null) {
                this.words = new HashMap<String,Integer>(4000);
                this.wordlist = new ArrayList<String>(4000);
            } else {
                this.words.clear();
                this.wordlist.clear();
            }
            if (this.keynames == null)
                this.keynames = new ArrayList<String>(200);
            else
                this.keynames.clear();
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int index;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t| ");
                sc.next();
                index = 0;
                String name = "";
                String tempword;
                while (sc.hasNext() && index < maxcount) {
                    tempword = sc.next();
                    if ((!tempword.contains(name) &&
                         !name.contains(tempword)) || name.length() < 1) {
                        name += tempword;
                        name += "-";
                        index++;
                    }
                    if (!words.containsKey(tempword)) {
                        words.put(tempword, wordcount);
                        this.wordlist.add(tempword);
                        wordcount++;
                    }
                    sc.next();
                }
                while (sc.hasNext()) {
                    tempword = sc.next();
                    if (!words.containsKey(tempword)) {
                        words.put(tempword, wordcount);
                        this.wordlist.add(tempword);
                        wordcount++;
                    }
                    sc.next();
                }
                this.keynames.add(name);
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void conceptToWords(String filename) {
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int index = 0;
            int conceptnum = this.keynames.size();
            topicinwords = new ArrayList[conceptnum];
            for (int i = 0; i < conceptnum; i++) {
                topicinwords[i] = new ArrayList<IndexPair>(21);
            }
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t| ");
                sc.next();
                String tempword;
                double value;
                while (sc.hasNext()) {
                    tempword = sc.next();
                    value = sc.nextDouble();
                    int windex = this.words.get(tempword);
                    this.topicinwords[index].add(new IndexPair(windex, value));
                }
                index++;
            }
            in1.close();
            for (int i = 0; i < conceptnum; i++) {
                double sum = 0;
                for (int j = 0; j < topicinwords[i].size(); j++) {
                    IndexPair o = (IndexPair)topicinwords[i].get(j);
                    sum += o.getWeight();
                }
                for (int j = 0; j < topicinwords[i].size(); j++) {
                    IndexPair o = (IndexPair)topicinwords[i].get(j);
                    o.setValue(o.getWeight() / sum);
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public List[]getConceptInWord() {
        return this.topicinwords;
    }

    public ArrayList<String> getKeyNames() {
        return this.keynames;
    }

    public HashMap<String, Integer> getAllWords() {
        return this.words;
    }

    public ArrayList<String> getWordList() {
        return this.wordlist;
    }

    public static void main(String []args) {
        ReadWeightedTopicKey myreader = new ReadWeightedTopicKey();
        myreader.read("mallet-weighted-key.txt", 20);
        myreader.conceptToWords("mallet-weighted-key.txt");
        List []l = myreader.getConceptInWord();
        for (int i = 0; i < l.length; i++) {
            List temp = l[i];
            for (int j = 0; j < temp.size(); j++) {
                IndexPair o = (IndexPair)temp.get(j);
                System.out.println((i + 1) + "\t" +
                                   (o.getIndex() + 1) + "\t" +
                                   o.getWeight());
            }
        }
    }
}
