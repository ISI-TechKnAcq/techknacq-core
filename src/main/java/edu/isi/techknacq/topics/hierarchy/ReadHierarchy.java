/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.isi.techknacq.topics.hierarchy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.topic.Indexpair;
import edu.isi.techknacq.topics.topic.Weightpair;
import edu.isi.techknacq.topics.util.ReadWeightedTopicKey;

/**
 *
 * @author linhong
 */
public class ReadHierarchy {
    HashMap<Integer, String> cluster2topic;
    HashMap<Integer, String> clusternames;
    ArrayList<String> topickeynames;
    List []clusterinwords;
    List []topicinwords;
    ArrayList<String> wordlist;
    List l;
    int maxnode;
    public ReadHierarchy(){
        
    }
     public void Init(ArrayList<String> inputkey, List [] inputtopicword, ArrayList<String> inputwordlist){
        this.topickeynames=inputkey;
        this.topicinwords=inputtopicword;
        this.wordlist=inputwordlist;
        l=new ArrayList<Weightpair>(this.wordlist.size());
    }
    public ArrayList<Integer> Parsetopic(String s){
        ArrayList<Integer> topics=new ArrayList<Integer>(100);
        Scanner sc=new Scanner (s);
        sc.useDelimiter(",");
        while(sc.hasNext()){
            int id=sc.nextInt();
            topics.add(id);
        }
        return topics;
    }
    public void Updatewordname(double []temp, int sid, int topicnum){
        if(sid<=topicnum){
            for(int j=0;j<topicinwords[sid-1].size();j++){
                Indexpair o=(Indexpair)topicinwords[sid-1].get(j);
                temp[o.getindex()]+=o.getweight();
            }
        }else{
            for(int j=0;j<clusterinwords[sid-topicnum-1].size();j++){
                Indexpair o=(Indexpair)clusterinwords[sid-topicnum-1].get(j);
                temp[o.getindex()]+=o.getweight();
            }
        }
    }
    public void UpdateIndexword(double []temp, int sid, int topicnum, int k){
        if (sid<=topicnum)
            return;
        this.clusterinwords[sid-topicnum-1]=new ArrayList<Indexpair>(50);
        double sum=0;
        for(int i=0;i<temp.length;i++){
            sum+=temp[i];
        }
        l.clear();
        for(int i=0;i<temp.length;i++){
            if(temp[i]>0)
                l.add(new Weightpair(temp[i]/sum,i));
        }
        Collections.sort(l);
        for(int i=0;(i<l.size()&&i<k);i++){
            Weightpair o=(Weightpair)l.get(i);
            clusterinwords[sid-topicnum-1].add(new Indexpair(o.getindex(),o.getweight()));
        }
    }
    public void Generateclustername(String filename, int topicnum){
        try {
            //FileWriter fstream = new FileWriter("hierarchylink.net",false);
           // BufferedWriter out=new BufferedWriter(fstream);
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int linenum=1;
            int sid;
            int tid;
            int supernodeid;
            maxnode=0;
            clusterinwords=new ArrayList[topicnum];
            double []temp=new double[this.wordlist.size()+1];
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t");
                sid=sc.nextInt();
                tid=sc.nextInt();
                supernodeid=linenum+topicnum;
                if(supernodeid>maxnode)
                    maxnode=supernodeid;
                if(sid>maxnode)
                    maxnode=sid;
                if(tid>maxnode)
                    maxnode=tid;
                Arrays.fill(temp,0.0);
                this.Updatewordname(temp, sid, topicnum);
                this.Updatewordname(temp, tid, topicnum);
                this.UpdateIndexword(temp, supernodeid, topicnum, 50);
                linenum++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadHierarchy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadHierarchy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Print(List l, BufferedWriter out,int k){
        for(int i=0;(i<l.size()&&i<k);i++){
            try {
                Indexpair o=(Indexpair)l.get(i);
                out.write(this.wordlist.get(o.getindex())+" ");
                out.write(o.getweight()+" ");
            } catch (IOException ex) {
                Logger.getLogger(ReadHierarchy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void ReadandWritefile(String filename,int topicnum){
        try {
            FileWriter fstream = null;
            fstream = new FileWriter("hierarchylink200cosine1key.txt",false);
            BufferedWriter out=new BufferedWriter(fstream);
            out.write("*Vertices "+maxnode+"\n");
            for(int i=1;i<=maxnode;i++){
                out.write(i+" \"");
                if(i<=topicnum){
                    Print(topicinwords[i-1],out,30);
                }else{
                    Print(clusterinwords[i-topicnum-1],out,30);
                }
                out.write("\"\n");
            }
            out.write("*Arcs "+(maxnode-1)+"\n");
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int linenum=1;
            int sid;
            int tid;
            int supernodeid;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t");
                sid=sc.nextInt();
                tid=sc.nextInt();
                supernodeid=linenum+topicnum;
                out.write(sid+"\t"+supernodeid+"\n");
                out.write(tid+"\t"+supernodeid+"\n");
                linenum++;
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadHierarchy.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    public static void main(String []args){
        ReadWeightedTopicKey myreader=new ReadWeightedTopicKey();
        myreader.read("mallet-weighted-key.txt", 20);
        myreader.Concepttowords("mallet-weighted-key.txt");
        ReadHierarchy hiereader=new ReadHierarchy();
        hiereader.Init(myreader.Getkeynames(), myreader.Getconceptinword(), myreader.Getwordlist());
        hiereader.Generateclustername("C:\\Users\\linhong\\Documents\\linhong-work\\Industry_project\\TechKnacq\\hierarchytree-wordcosine.txt", 200);
        hiereader.ReadandWritefile("C:\\Users\\linhong\\Documents\\linhong-work\\Industry_project\\TechKnacq\\hierarchytree-wordcosine.txt", 200);
    }
}