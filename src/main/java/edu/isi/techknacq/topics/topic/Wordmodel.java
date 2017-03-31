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
             while(sc.hasNext()) {
                 word=tp.getTokenString(sc.next());
                 if (word.length()<=2)
                     continue;
                 int length=0;
                 for(j=0;j<word.length();j++) {
                     if (word.charAt(j)!=' ')
                         length++;
                 }
                 if (length<=2)
                     continue;
                 mywords.add(word);
             }
             sc.close();
         }
         mywords.RemoveStopWord();
         mywords.Prune(100);
         mywords.MergeSimilarWords();
    }

    public void computeWordModel(ArrayList<String> filenames) {
         TokenProcessor tp = new TokenProcessor();
         String word;
         int i;
    }

    public void saveWordModel(String filename) {
        try {
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter(filename, false);
            out = new BufferedWriter(fstream);
            mywords.Print(out);
        } catch (IOException ex) {
            Logger.getLogger(Wordmodel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveWord(String filename) {
        try {
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter(filename, false);
            out = new BufferedWriter(fstream);
            mywords.Printwords(out);
        } catch (IOException ex) {
            Logger.getLogger(Wordmodel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String []Getwords() {
        return mywords.getWords();
    }

    public int []Getcount() {
        return mywords.Getcount();
    }

    public void SavetopK(int k, String filename) {
        try {
            mywords.MergeSimilarWords();
            String []kword=mywords.Gettop(k);
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter(filename, false);
            out = new BufferedWriter(fstream);
            out.write("\"name\",\"word\",\"count\"\n");
            for(int i=0;i<kword.length;i++) {
                out.write("\""+kword[i]+"\",\""+kword[i]+"\","+mywords.getCountOfWord(kword[i])+"\n");
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Wordmodel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void clear() {
        mywords.clear();
    }
}
