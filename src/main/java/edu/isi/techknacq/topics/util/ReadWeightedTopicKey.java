/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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

import edu.isi.techknacq.topics.topic.Indexpair;

/**
 *
 * @author linhong
 */
public class ReadWeightedTopicKey {
    ArrayList<String> keynames;
    HashMap<String, Integer> words;
    List []topicinwords;
    int wordcount;
    ArrayList<String> wordlist;
    public ReadWeightedTopicKey(){
        
    }
        public void read(String filename, int maxcount){
        try {
            wordcount=0;
            if(this.words==null){
                this.words=new HashMap<String,Integer>(4000);
                this.wordlist=new ArrayList<String>(4000);
            }
            else
            {
                this.words.clear();
                this.wordlist.clear();
            }
            if(this.keynames==null)
                this.keynames=new ArrayList<String>(200);
            else
                this.keynames.clear();
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int index;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t| ");
                sc.next();
                index=0;
                String name="";
                String tempword;
                while(sc.hasNext()&&index<maxcount){
                    tempword=sc.next();
                    if((!tempword.contains(name)&&!name.contains(tempword))||name.length()<1){
                        name+=tempword;
                        name+="-";
                        index++;
                    }
                    if(words.containsKey(tempword)==false){
                        words.put(tempword, wordcount);
                        this.wordlist.add(tempword);
                        wordcount++;
                    }
                    sc.next();
                }
                while(sc.hasNext()){
                    tempword=sc.next();
                    if(words.containsKey(tempword)==false){
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
            Logger.getLogger(ReadTopicKey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadTopicKey.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void Concepttowords(String filename){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int index=0;
            int conceptnum=this.keynames.size();
             topicinwords=new ArrayList[conceptnum];
            for(int i=0;i<conceptnum;i++){
                topicinwords[i]=new ArrayList<Indexpair>(21);
            }
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t| ");
                sc.next();
                String tempword;
                double value;
                while(sc.hasNext()){
                    tempword=sc.next();
                    value=sc.nextDouble();
                    int windex=this.words.get(tempword);
                    this.topicinwords[index].add(new Indexpair(windex,value));
                }
                index++;
            }
            in1.close();
            for(int i=0;i<conceptnum;i++){
                double sum=0;
                for(int j=0;j<topicinwords[i].size();j++){
                    Indexpair o=(Indexpair)topicinwords[i].get(j);
                    sum+=o.getweight();
                }
                for(int j=0;j<topicinwords[i].size();j++){
                     Indexpair o=(Indexpair)topicinwords[i].get(j);
                     o.setvalue(o.getweight()/sum);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ReadTopicKey.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public List[]Getconceptinword(){
        return this.topicinwords;
    }
    public ArrayList<String> Getkeynames(){
        return this.keynames;
    }
    public HashMap<String, Integer> Getallwords(){
        return this.words;
    }
    public ArrayList<String> Getwordlist(){
        return this.wordlist;
    }
    public static void main(String []args){
        ReadWeightedTopicKey myreader=new ReadWeightedTopicKey();
        myreader.read("mallet-weighted-key.txt", 20);
        myreader.Concepttowords("mallet-weighted-key.txt");
        List []l=myreader.Getconceptinword();
        for(int i=0;i<l.length;i++){
            List temp=l[i];
            for(int j=0;j<temp.size();j++){
                Indexpair o=(Indexpair)temp.get(j);
                System.out.println((i+1)+"\t"+(o.getindex()+1)+"\t"+o.getweight());
            }
        }
    }
}
