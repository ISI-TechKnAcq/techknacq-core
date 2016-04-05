/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.isi.techknacq.topics.hierarchy;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.topic.Indexpair;
import edu.isi.techknacq.topics.topic.Weightpair;
import edu.isi.techknacq.topics.util.ReadWeightedTopicKey;
import edu.isi.techknacq.topics.util.StrUtil;

/**
 *
 * @author linhong
 */
public class Hierarchytopic {
    ArrayList<Integer> topiccluster;
    ArrayList<String> keynames;
    HashMap<String,Integer> clustername;
    String []clustertopicname;
    List []topicinwords;
    ArrayList<Integer> []cluster2topic;
    ArrayList<String> wordlist;
    public Hierarchytopic(){
        
    }
    public void Init(ArrayList<String> inputkey, List [] inputtopicword, ArrayList<String> inputwordlist){
        this.keynames=inputkey;
        this.topicinwords=inputtopicword;
        this.wordlist=inputwordlist;
    }
    public void Readclusters(String filename, int clusternum){
        topiccluster=new ArrayList<Integer>(50);
        cluster2topic=new ArrayList[clusternum];
        for(int i=0;i<clusternum;i++){
            cluster2topic[i]=new ArrayList<Integer>(10);
        }
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int cid;
            int line=0;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t");
                sc.next();
                cid=sc.nextInt();
                this.topiccluster.add(cid-1);
                this.cluster2topic[cid-1].add(line);
                line++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Hierarchytopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Hierarchytopic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Readclustergraph(String filename){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String src;
            String tar;
            int nodenum=0;
            //int line=0;
            this.clustername=new HashMap<String,Integer>(100);
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t");
                src=sc.next();
                tar=sc.next();
                if(this.clustername.containsKey(src)==false){
                    this.clustername.put(src, nodenum);
                    nodenum++;
                }
                if(this.clustername.containsKey(tar)==false){
                    this.clustername.put(tar, nodenum);
                    nodenum++;
                }
                //line++;
                //System.out.println("linenum"+line);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Hierarchytopic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Hierarchytopic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String Gettopkword(int k, double []temp){
        double sum=0;
        for(int i=0;i<temp.length;i++){
            sum+=temp[i];
        }
        List l=new ArrayList<Weightpair>(this.wordlist.size());
        for(int i=0;i<temp.length;i++){
            l.add(new Weightpair(temp[i]/sum,i));
        }
        Collections.sort(l);
        String res="";
        for(int i=0;i<k;i++){
            Weightpair o=(Weightpair)l.get(i);
            res+=wordlist.get(o.getindex());
            res+="\t";
            res+=o.getweight();
            res+="\t";
        }
        return res;
    }
    public void Mapcluster2topicword(){
        clustertopicname=new String[clustername.size()];
        Iterator it = clustername.entrySet().iterator();
        double []temp=new double[this.wordlist.size()];
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            String cluster=(String)pairs.getKey();
            Integer index=(Integer)pairs.getValue();
            if(cluster.compareToIgnoreCase("all")==0){
                Arrays.fill(temp, 0.0);
                for(int i=0;i<topicinwords.length;i++){
                    for(int j=0;j<topicinwords[i].size();j++){
                        Indexpair o=(Indexpair)topicinwords[i].get(j);
                        temp[o.getindex()]+=o.getweight();
                    }
                }
                clustertopicname[index]=this.Gettopkword(30, temp);
            }else{
                Scanner sc=new Scanner (cluster);
                sc.useDelimiter(",");
                Arrays.fill(temp, 0.0);
                while(sc.hasNext()){
                    int cid=sc.nextInt();
                    for(int i=0;i<cluster2topic[cid-1].size();i++){
                        int t=cluster2topic[cid-1].get(i);
                        for(int j=0;j<topicinwords[t].size();j++){
                             Indexpair o=(Indexpair)topicinwords[t].get(j);
                             temp[o.getindex()]+=o.getweight();
                        }
                    }
                }
                clustertopicname[index]=this.Gettopkword(30, temp);
            }
        }
    }
    public void Printclustergraph(String filename){
        try {
            System.out.println("Vertices "+clustertopicname.length);
            for(int i=0;i<clustertopicname.length;i++){
                System.out.println((i+1)+" "+"\""+clustertopicname[i]+"\"");
            }
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String src;
            String tar;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t");
                src=sc.next();
                tar=sc.next();
                int sid=this.clustername.get(src);
                int tid=this.clustername.get(tar);
                System.out.println((sid+1)+" "+(tid+1));
            }
        } catch (IOException ex) {
            Logger.getLogger(Hierarchytopic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String []args){
        Hierarchytopic myhier=new Hierarchytopic();
        ReadWeightedTopicKey myreader=new ReadWeightedTopicKey();
        myreader.read("mallet-weighted-key.txt", 5);
        myreader.Concepttowords("mallet-weighted-key.txt");
        myhier.Init(myreader.Getkeynames(), myreader.Getconceptinword(), myreader.Getwordlist());
        myhier.Readclusters("topiccluster30.txt", 30);
        myhier.Readclustergraph("clustertree_30.txt");
        myhier.Mapcluster2topicword();
        myhier.Printclustergraph("clustertree_30.txt");
    }
}
