package edu.isi.techknacq.topics.topic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.util.TokenProcessor;

/**
 *
 * @author linhong
 * Given a corpus, compute the dictionary and word frequency for it using
 * data structure SortedWordPairList
 *
 */
public class Wordmodel {
    private ArrayList<String> posts;
    private String []words;
    private int []df;
    private SortedWordPairList mywords;
    private Logger logger = Logger.getLogger(Wordmodel.class.getName());

    public Wordmodel() {
        mywords = new SortedWordPairList(10000);
    }

    public void initPost(ArrayList<String> inputpost) {
        posts = inputpost;
    }

    public void computeWordModel() {
        TokenProcessor tp = new TokenProcessor();
        String word;
        int i;
        int j;
        for (i = 0; i < this.posts.size(); i++) {
            if (i % 1000 == 0) {
                System.out.println(i);
            }
            Scanner sc = new Scanner(posts.get(i));
            while (sc.hasNext()) {
                word = tp.getTokenString(sc.next());
                if (word.length() <= 2)
                    continue;
                int length = 0;
                for (j = 0; j < word.length(); j++) {
                    if (word.charAt(j) != ' ')
                        length++;
                }
                if (length <= 2)
                    continue;
                mywords.add(word);
            }
            sc.close();
        }
        mywords.removeStopWord();
        mywords.prune(100);
        mywords.mergeSimilarWords();
    }

    public void computeWordModel(ArrayList<String> filenames) {
        // TokenProcessor tp = new TokenProcessor();
        // String word;
        // int i;
    }

    public void saveWordModel(String filename) {
        try {
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter(filename, false);
            out = new BufferedWriter(fstream);
            mywords.print(out);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void saveWord(String filename) {
        try {
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter(filename, false);
            out = new BufferedWriter(fstream);
            mywords.printWords(out);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public String []getWords() {
        return mywords.getWords();
    }

    public int []getCount() {
        return mywords.getCount();
    }

    public void saveTopK(int k, String filename) {
        try {
            mywords.mergeSimilarWords();
            String []kword = mywords.getTop(k);
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter(filename, false);
            out = new BufferedWriter(fstream);
            out.write("\"name\",\"word\",\"count\"\n");
            for (int i = 0; i < kword.length; i++) {
                out.write("\"" + kword[i] + "\",\"" + kword[i] + "\"," +
                          mywords.getCountOfWord(kword[i]) + "\n");
            }
            out.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void clear() {
        mywords.clear();
    }
}
