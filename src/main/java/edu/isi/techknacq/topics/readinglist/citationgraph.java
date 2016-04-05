/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.isi.techknacq.topics.readinglist;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author linhong
 */
public class citationgraph {
    HashMap<String, Integer> paperids;
    ArrayList<String> papernames;
    ArrayList<Integer> []mycited;
    double [][] paper2topic;
    int pnum=0;
    int tnum=300;
    
    public citationgraph(){
        this.paperids=new HashMap<String, Integer>(18161);
        this.papernames=new ArrayList<String>(18161);
        mycited=new ArrayList[18161];
        for(int i=0;i<mycited.length;i++)
            mycited[i]=new ArrayList<Integer>(10);
    }
    public void settopicnum(int _tnum){
        tnum=_tnum;
    }
    public void Readcitation(String filename){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String source;
            String target;
            int sid;
            int tid;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter(" ==> ");
                source=sc.next();
                target=sc.next();
                if(this.paperids.containsKey(source)==false){
                    this.paperids.put(source, pnum);
                    sid=pnum;
                    pnum++;
                }else{
                    sid=this.paperids.get(source);
                }
                if(this.paperids.containsKey(target)==false){
                    this.paperids.put(target, pnum);
                    tid=pnum;
                    pnum++;
                }else{
                    tid=this.paperids.get(target);
                }
                if(sid>=mycited.length){
                    System.out.println(sid);
                    System.exit(2);
                }
                mycited[sid].add(tid);
            }
            System.out.println(pnum);
            in1.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(citationgraph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(citationgraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //read concept composition for each document
    public void Readc2d(String filename){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String docname;
            String topicname;
            int index1;
            int index2;
            int did;
            int tindex;
            double tweight;
            paper2topic=new double[pnum][tnum];
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                docname=sc.next();
                // change '\\'(windows file) to '/' (Linux file) 
                docname = docname.substring(docname.lastIndexOf('/') + 1,docname.length()-4);
                //System.out.println(docname);
                if(this.paperids.containsKey(docname)==false)
                    continue;
                did=this.paperids.get(docname);
                //Getthelastname
                while(sc.hasNext()){
                    topicname=sc.next();
                    index1=topicname.indexOf("topic");
                    index2=topicname.indexOf(":");
                    if(index1>=0&&index2>=0){
                        tindex=Integer.parseInt(topicname.substring(index1+5,index2));
                        tweight=Double.parseDouble(topicname.substring(index2+1,topicname.length()));
                        paper2topic[did][tindex]=tweight;
                    }
                } 
            }
            in1.close();
        } catch (IOException ex) {
            Logger.getLogger(citationgraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public double [][] Computecitationlinks(){
        double [][]topic2topic=new double[tnum][tnum];
        for(int i=0;i<pnum;i++){
            //paper id
            for(int j=0;j<this.mycited[i].size();j++){
                int citedid=mycited[i].get(j);
                for(int k1=0;k1<this.paper2topic[i].length;k1++){
                    for(int k2=0;k2<this.paper2topic[citedid].length;k2++){
                        topic2topic[k1][k2]+=(paper2topic[i][k1]+paper2topic[citedid][k2]);
                    }
                }
            }
        }
        this.paperids.clear();
        this.papernames.clear();
        for(int i=0;i<this.mycited.length;i++){
            this.mycited[i].clear();
        }
        return topic2topic;
    }
    public void print(){
        
    }
}
