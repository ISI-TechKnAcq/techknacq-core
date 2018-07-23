package edu.isi.techknacq.topics.entity2topic;

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
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.util.ReadTopicKey;

public class ReadEntity {
    private ArrayList<String> entitylists;
    private HashMap<String, Integer> mywords;
    private Logger logger = Logger.getLogger(ReadEntity.class.getName());

    public void initDict(HashMap<String, Integer> dictionaries) {
        mywords = new HashMap<String,Integer>(2000);
        Iterator it = dictionaries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Integer w = (Integer)pairs.getValue();
            Scanner sc = new Scanner((String)pairs.getKey());
            sc.useDelimiter("_");
            while (sc.hasNext()) {
                mywords.put(sc.next(), w);
            }
        }
    }

    public void readWikiFile(String filename) {
        try {
            entitylists = new ArrayList<String>(1000);
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String word;
            String term;
            int count;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                count = sc.nextInt();
                if (count < 100)
                    continue;
                boolean flag = false;
                word = "";
                while (sc.hasNext()) {
                    term = sc.next();
                    word += term;
                    if (this.mywords.containsKey(term)) {
                        flag = true;
                    }
                }
                if (flag && word.indexOf("et_al") < 0 &&
                    word.indexOf("university") < 0 &&
                    word.indexOf("dept") < 0) {
                    entitylists.add(word);
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void readFile(String filename) throws IOException {
        try {
            entitylists = new ArrayList<String>(1000);
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String word;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t");
                word = sc.next();
                Scanner sc2 = new Scanner(word);
                sc2.useDelimiter("_");
                boolean flag = false;
                while (sc2.hasNext()) {
                    if (this.mywords.containsKey(sc2.next())) {
                        flag = true;
                    }
                }
                if (flag && word.indexOf("et_al") < 0 &&
                    word.indexOf("seg_l") < 0 &&
                    word.indexOf("dept") < 0) {
                    entitylists.add(word);
                }
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void printResults() {
        System.out.println(entitylists.size());
        for (int i = 0; i < this.entitylists.size(); i++) {
            System.out.println(entitylists.get(i));
        }
    }

    public void getMatch(ArrayList<String> topickeys) {
        FileWriter fstream = null;
        try {
            fstream = new FileWriter("wikientity2topic_thre100.txt", false);
            BufferedWriter out = new BufferedWriter(fstream);
            System.out.println(entitylists.size());
            int entitytopic = 0;
            for (int i = 0; i < this.entitylists.size(); i++) {
                entitytopic = 0;
                for (int j = 0; j < topickeys.size(); j++) {
                    String word = entitylists.get(i);
                    Scanner sc = new Scanner(word);
                    sc.useDelimiter("_|| ");
                    int count = 0;
                    int desc = 0;
                    double score = 0;
                    while (sc.hasNext()) {
                        String term = sc.next();
                        if (topickeys.get(j).indexOf(term) >= 0) {
                            count++;
                            desc++;
                            score += 1.0 / desc;
                        }
                    }
                    if (count > 0 && score >= 3.3) {
                        out.write(this.entitylists.get(i) + "," +
                                  topickeys.get(j) + "," + score + "\n");
                        entitytopic++;
                    }
                }
                if (entitytopic > 0)
                    System.out.println(this.entitylists.get(i) + "\t" +
                                       entitytopic);
            }
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
        ReadTopicKey mytopic = new ReadTopicKey();
        mytopic.read("mallet-keys-2gm-200.txt", 20);
        mytopic.conceptToWords("mallet-keys-2gm-200.txt");
        ReadEntity myreader = new ReadEntity();
        myreader.initDict(mytopic.getAllWords());
        myreader.readWikiFile("wikipedia-entity-counts.txt");
        // myreader.readFile("A00-1002_0.t2s");
        // myreader.printResults();
        mytopic.read("mallet-keys-2gm-200.txt", 5);
        ArrayList<String> keys = mytopic.getKeyNames();
        myreader.getMatch(keys);
    }
}
