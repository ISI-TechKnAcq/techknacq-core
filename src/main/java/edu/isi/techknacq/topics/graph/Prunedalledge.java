package edu.isi.techknacq.topics.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author linhong
 */
public class Prunedalledge {
    double [][]CEdoc;
    double [][]CEword;
    double [][]IF;
    double [][]simword;
    double [][]simdoc;
    double [][]cite;
    double [][]hierdoc;
    double [][]hierword;
    int topicnum;
    public Prunedalledge(){
        
    }
     public void settopicnum(int _tnum){
        topicnum=_tnum;
        this.CEdoc=new double[topicnum][topicnum];
        this.IF=new double[topicnum][topicnum];
        this.simword=new double[topicnum][topicnum];
        this.simdoc=new double[topicnum][topicnum];
        this.CEword=new double[topicnum][topicnum];
        this.hierdoc=new double[topicnum][topicnum];
        this.hierword=new double[topicnum][topicnum];
        this.cite=new double[topicnum][topicnum];
    }
    public void Readscores(String filename){
        try {
            FileInputStream fstream1;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int id;
            int tid;
           // double cedoc;
            double ifscore;
            double simwordscore;
            double simdocscore;
            double cecite;
            double ceword;
            double hword;
           // double hdoc;
            br.readLine();
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t");
                id=sc.nextInt();
                sc.next();
                tid=sc.nextInt();
                sc.next();
                simdocscore=sc.nextDouble();//simdoc
                simwordscore=sc.nextDouble();//simword
                ifscore=sc.nextDouble(); //if
                //System.out.println(ifscore);
                //cedoc=sc.nextDouble();//CEdoc
                ceword=sc.nextDouble();//CEword
                cecite=sc.nextDouble();//citation
                hword=sc.nextDouble();//hierdoc
                //hdoc=sc.nextDouble();
                //CEdoc[id][tid]=cedoc;
                //CEdoc[tid][id]=-cedoc;
                CEword[id][tid]=ceword;
                CEword[tid][id]=-ceword;
                if(ifscore>0.0){
                    this.IF[id][tid]=ifscore;
                    this.IF[tid][id]=(0.0-ifscore); 
                }
                this.simword[id][tid]=simwordscore;
                this.simword[tid][id]=simwordscore;
                this.simdoc[id][tid]=simdocscore;
                this.simdoc[tid][id]=simdocscore;
                this.cite[id][tid]=cecite;
                this.cite[tid][id]=-cecite;
                this.hierword[id][tid]=hword;
                this.hierword[tid][id]=-hword;
                //this.hierdoc[id][tid]=hdoc;
                //this.hierdoc[tid][id]=-hdoc;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EdgeEval.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EdgeEval.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Outputgraph(String outfile, double thres, int type){
        try {
            FileWriter fstream;
            fstream = new FileWriter(outfile,false);
            BufferedWriter out=new BufferedWriter(fstream);
            double [][]tempres;
            switch (type){
                case 1:
                    tempres=this.simword;
                    break;
                case 2:
                   tempres=this.IF;
                    break;
                case 3:
                    tempres=this.CEword;
                    break;
                case 4:
                    tempres=this.hierword;
                    break;
                default:
                    tempres=this.CEword;
            }
            for(int i=0;i<tempres.length;i++){
                for(int j=0;j<tempres.length;j++){
                    if(tempres[i][j]>thres){
                        out.write(i+"\t"+j+"\t"+tempres[i][j]+"\n");
                    }
                }
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Prunedalledge.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String []args){
        Prunedalledge myprune=new Prunedalledge();
        myprune.settopicnum(300);
        myprune.Readscores("alledge.tsv");
        //0.0001 for CE
        //0.000001 for IF
        myprune.Outputgraph("Conceptgraph_Hier-2016-01-18.txt", 0.00000001, 4);
    }
}
