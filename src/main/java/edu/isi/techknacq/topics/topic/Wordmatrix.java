package edu.isi.techknacq.topics.topic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.util.TokenProcessor;

/**
 *
 * @author linhong Zhu
 * @email: linhong.seba.zhu@gmail.com
 * @since: 19/April/2012
 * WordMatrix, given a list of documents, computer the document-word matrix
 * representation
 */

public class Wordmatrix {
    private String []words;
    private int []df;
    private ArrayList<String> posts;

    public void initWords(String []inputwords) {
        this.words = inputwords;
    }

    public void initWordFreq(int []inputdf) {
        this.df = inputdf;
    }

    public void initContent(ArrayList<String> inputcontent) {
        this.posts = inputcontent;
    }

    public void initmatrix(String filename, ArrayList<String> keyname) {
        try {
            int i = 0;
            int j = 0;
            ArrayList<Indexpair> features = new ArrayList<Indexpair>(100);
            ArrayList<String> uniquecontent = new ArrayList<String>(100);
            ArrayList<Integer> tf = new ArrayList<Integer>(100);
            int index = 0;
            int w;
            double v = 0;
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter(filename, false);
            out = new BufferedWriter(fstream);
            String word;
            TokenProcessor tp=new TokenProcessor();
            for (i=0;i<this.posts.size();i++) {
                Scanner sc=new Scanner(posts.get(i));
                while(sc.hasNext()) {
                    word=tp.getTokenString(sc.next());
                    if (word.length()<2) {
                        continue;
                    }
                    index = Collections.binarySearch(uniquecontent, word);
                    if (index < 0) {
                        uniquecontent.add(-index - 1, word);
                        tf.add(-index - 1, 1);
                    } else {
                        w = tf.get(index);
                        tf.set(index, w + 1);
                    }
                }
                for (j = 0; j < tf.size(); j++) {
                    if (tf.get(j) > 0) {
                        index = Arrays.binarySearch(words, uniquecontent.get(j));
                        if (index >=0) {
                            Indexpair o = new Indexpair(index,(double)tf.get(j)/this.df[j]);
                            features.add(o);
                        }
                    }
                }
                if (features.size()>0) {
                    out.write(keyname.get(i)+" ");
                    out.write(Integer.toString(features.size()));
                    for (j = 0; j < features.size(); j++) {
                        index=features.get(j).getindex();
                        v=features.get(j).getweight();
                        //out.write((i+1)+"\t"+(index+1)+"\t"+v+"\n");
                        out.write(" " +index + ":" + v);
                    }
                    out.write("\n");
                }
                tf.clear();
                uniquecontent.clear();
                features.clear();
            }
            if (out!=null)
                out.close();
            if (fstream!=null)
                fstream.close();
        } catch (IOException ex) {
            Logger.getLogger(Wordmatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initmatrix(String filename) {
        try {
            int i = 0;
            int j = 0;
            ArrayList<Indexpair> features = new ArrayList<Indexpair>(100);
            ArrayList<String> uniquecontent = new ArrayList<String>(100);
            ArrayList<Integer> tf = new ArrayList<Integer>(100);
            int index = 0;
            int w;
            double v = 0;
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter(filename, false);
            out = new BufferedWriter(fstream);
            String word;
            TokenProcessor tp=new TokenProcessor();
            for (i=0;i<this.posts.size();i++) {
                Scanner sc=new Scanner(posts.get(i));
                while(sc.hasNext()) {
                    word=tp.getTokenString(sc.next());
                    if (word.length()<2) {
                        continue;
                    }
                    index = Collections.binarySearch(uniquecontent, word);
                    if (index < 0) {
                        uniquecontent.add(-index - 1, word);
                        tf.add(-index - 1, 1);
                    } else {
                        w = tf.get(index);
                        tf.set(index, w + 1);
                    }
                }
                for (j = 0; j < tf.size(); j++) {
                    if (tf.get(j) > 0) {
                        index = Arrays.binarySearch(words, uniquecontent.get(j));
                        if (index >=0) {
                            Indexpair o = new Indexpair(index,tf.get(j));
                            features.add(o);
                        }
                    }
                }
                if (features.size()>0) {
                    out.write(Integer.toString(features.size()));
                    for (j = 0; j < features.size(); j++) {
                        index=features.get(j).getindex();
                        v=features.get(j).getweight();
                        out.write(" " +index + ":" + (int)v);
                    }
                    out.write("\n");
                }
                tf.clear();
                uniquecontent.clear();
                features.clear();
            }
            if (out!=null)
                out.close();
            if (fstream!=null)
                fstream.close();
        } catch (IOException ex) {
            Logger.getLogger(Wordmatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void clear() {
        this.posts.clear();
        this.posts.trimToSize();
        if (this.df!=null) {
            this.df=null;
        }
        if (this.words!=null) {
            this.words=null;
        }
    }
}
