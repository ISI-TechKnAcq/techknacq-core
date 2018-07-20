package edu.isi.techknacq.topics.readinglist;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.isi.techknacq.topics.topic.WeightPair;
import edu.isi.techknacq.topics.topic.WordPair;


public class Keyword2concept {
    private ArrayList<String> topics;
    private ArrayList<ArrayList<WordPair>> wordintopic;
    private ArrayList<WeightPair> hittopics;
    private int k = 8;
    private Logger logger = Logger.getLogger(Keyword2concept.class.getName());

    public void readKey(String filename) {
        try {
            this.topics = new ArrayList<String>(200);
            this.wordintopic = new ArrayList(200);
            FileInputStream fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int line = 0;
            while ((strline = br.readLine()) != null) {
                wordintopic.add(new ArrayList<WordPair>(20));
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t| ");
                sc.next();
                String name = "";
                String tempword;
                float value = 0.0f;
                while (sc.hasNext()) {
                    tempword = sc.next();
                    if ((!tempword.contains(name) && !name.contains(tempword))
                        || name.length() < 1) {
                        name += tempword;
                        name += " ";
                    }
                    if (sc.hasNext())
                        value = sc.nextFloat();
                    wordintopic.get(line).add(new WordPair(tempword, value));
                }
                this.topics.add(name);
                line++;
            }
            for (int i = 0; i < wordintopic.size(); i++) {
                float sum = 0;
                for (int j = 0; j < wordintopic.get(i).size(); j++) {
                    WordPair o = wordintopic.get(i).get(j);
                    sum += o.getprob();
                }
                for (int j = 0; j < wordintopic.get(i).size(); j++) {
                    WordPair o = wordintopic.get(i).get(j);
                    float oldv = o.getprob();
                    o.setProb(oldv / sum);
                    wordintopic.get(i).set(j, o);
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Integer> getMatch(String keyword) {
        hittopics = new ArrayList<WeightPair>(20);
        for (int i = 0; i < wordintopic.size(); i++) {
            double hitcount = 0;
            for (int j = 0; j < wordintopic.get(i).size() && j < k; j++) {
                WordPair o = wordintopic.get(i).get(j);
                if (o.getWord().length() < 3)
                    continue;
                if (keyword.toLowerCase().indexOf(o.getWord()) >= 0 ||
                    o.getWord().indexOf(keyword.toLowerCase()) >= 0) {
                    double lengthbonus = (double)Math.abs(keyword.length() -
                                                          o.getWord().length())
                        / Math.max(keyword.length(), o.getWord().length());
                    lengthbonus = 1 - lengthbonus;
                    hitcount += o.getprob() * lengthbonus;
                }
            }
            if (hitcount > 0) {
                hittopics.add(new WeightPair(hitcount, i));
            }
        }
        Collections.sort(hittopics);
        ArrayList<Integer> topicindex;
        topicindex = new ArrayList<Integer>(hittopics.size());
        for (WeightPair o : hittopics) {
            topicindex.add(o.getindex());
        }
        return topicindex;
    }

    public ArrayList<String> getTopics() {
        return topics;
    }

    public ArrayList<ArrayList<WordPair>> getWeightTopic() {
        return this.wordintopic;
    }

    public static void main(String []args) {
        Keyword2concept mykeyword = new Keyword2concept();
        mykeyword.readKey("mallet-weighted-key.txt");
        ArrayList<Integer> hits = mykeyword.getMatch("machine_translation");
        ArrayList<String> mytopic = mykeyword.getTopics();
        for (int i = 0; i < hits.size(); i++) {
            int index = hits.get(i);
            System.out.println(mytopic.get(index));
        }
    }
}
