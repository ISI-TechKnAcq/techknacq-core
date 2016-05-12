/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.isi.techknacq.topics.topic;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.util.MathUtil;
import edu.isi.techknacq.topics.util.ReadTopicKey;
import edu.isi.techknacq.topics.util.ReadWeightedTopicKey;

/**
 *
 * @author linhong
 */
public class Evaluation {
    ArrayList<String> words;
    ArrayList<Double> []wordvec;
    public Evaluation(){
        
    }
    public void Readwords(String filename){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            words=new ArrayList<String>(10000);
            while((strline=br.readLine())!=null){
                words.add(strline);
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Readvector(String filename){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            wordvec=new ArrayList[this.words.size()];
            for(int i=0;i<this.wordvec.length;i++){
                wordvec[i]=new ArrayList<Double>(6);
            }
            int line=0;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t");
                while(sc.hasNext()){
                    wordvec[line].add(sc.nextDouble());
                }
                line++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void addlist(ArrayList<Double> tar, ArrayList<Double> src){
        if (tar.size()!=src.size())
            return;
        for (int i=0;i<tar.size();i++){
            tar.set(i, (tar.get(i)+src.get(i))/2);
        }
    }
    public ArrayList<Double> word2vec(String word){
        word= word.replace("#", "");
        int newindex=Collections.binarySearch(this.words, word);
        if (newindex>=0)
                return this.wordvec[newindex];
        else{
            if(word.contains("_")){
                String word2=word.replaceAll("_", " ");
                int index=Collections.binarySearch(this.words, word2);
                if(index>=0)
                    return this.wordvec[index];
                else{
                    ArrayList<Double> res=null;
                    Scanner sc=new Scanner(word);
                    sc.useDelimiter("_");
                    while(sc.hasNext()){
                        int index2=Collections.binarySearch(this.words, sc.next());
                        if (index2>=0){
                            if (res==null)
                                res=this.wordvec[index2];
                            else{
                                addlist(res,wordvec[index2]);
                            }
                        }
                    }
                    return res;
                }     
            }
            else
                return null;
        }
        
    }
    public void Runevaluate(String filename){
        ReadWeightedTopicKey myreader=new ReadWeightedTopicKey();
        //ReadTopicKey myreader=new ReadTopicKey();
        myreader.read(filename, 20);
        myreader.Concepttowords(filename);
        List []l=myreader.Getconceptinword();
        ArrayList<String> topicwords=myreader.Getwordlist();
        double totalscore=0;
        for(int i=0;i<l.length;i++){
            List temp=l[i];
            double avgscore=0;
            for(int j=0;j<temp.size();j++){
                Indexpair o=(Indexpair)temp.get(j);
                int windex=o.getindex();
                String word=topicwords.get(windex);
                ArrayList<Double> l1=this.word2vec(word);
                if (l1==null)
                    continue;
                for(int k=(j+1);k<temp.size();k++){
                    Indexpair o2=(Indexpair)temp.get(k);
                    int windex2=o2.getindex();
                    String word2=topicwords.get(windex2);
                    ArrayList<Double> l2=this.word2vec(word2);
                    if(l2==null)
                        continue;
                    //avgscore+=MathUtil.Cosinsimilarity(l1, l2)*(o.getweight()+o2.getweight());
                    avgscore+=MathUtil.Cosinsimilarity(l1, l2);
                }
                
            }
            avgscore/=temp.size();
            avgscore/=(temp.size()-1);
            avgscore*=2;
            System.out.println(i+"\t"+avgscore);
            totalscore+=avgscore;
        }
        totalscore/=l.length;
        System.out.println("Average coherence score is "+totalscore);
    }
    public static void main(String []args){
        Evaluation myevl=new Evaluation();
        myevl.Readwords("Z:\\Data\\Automatic topic evaluation\\learned dictionary for topic coherence\\words.txt");
        myevl.Readvector("Z:\\Data\\Automatic topic evaluation\\learned dictionary for topic coherence\\word2vec-6.txt");
        myevl.Runevaluate(args[0]);
        
    }
}