package edu.isi.techknacq.topics.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.readinglist.Concept2doc;
import edu.isi.techknacq.topics.topic.Indexpair;
import edu.isi.techknacq.topics.topic.Weightpair;
import edu.isi.techknacq.topics.util.ReadTopicKey;


/**
 *
 * @author linhong
 */
public class Cograph {
    ArrayList<String> keynames;
    int tnum;
    List []conceptsindoc;
    public Cograph(){
        
    }
    /*
    read the topic key for each topic from topic key file
    @para: filename (in String)
    */
    public void Readkey(String filename){
        ReadTopicKey myreader=new ReadTopicKey();
        myreader.read(filename,20);
        keynames=myreader.Getkeynames();
        myreader.Concepttowords(filename);
        tnum=this.keynames.size();
    }
    
    /*
    Turn a WeightedPair list into a vector representation
    */
    public void Extract(List a1, double []v1){
        Arrays.fill(v1,0.0);
        for(int i=0;i<a1.size();i++){
            Weightpair o=(Weightpair)a1.get(i);
            v1[o.getindex()]=o.getweight();
        }
    }
    
    /*
    Turn a Indexpair list into a vector representation
    */
     public void Extract2(List a1, double []v1){
        Arrays.fill(v1,0.0);
        for(int i=0;i<a1.size();i++){
            Indexpair o=(Indexpair)a1.get(i);
            v1[o.getindex()]=o.getweight();
        }
    }
     
     /*
     Compute the topic-to-topic cooccurrenes between documents
     */
    public int Getoccu(double []v1, double []v2){
        int c=0;
        int i=0;
        int j=0;
        while(i<v1.length&&j<v2.length){
            if(v1[i]>0.0000000001&&v2[j]>0.00000000001){
                c++;
            }
            i++;
            j++;
        }
        return c;
    }
    public double entropy(double p){
        if(p>0.0)
            return -p*Math.log(p);
        else
            return 0.0;
    }
    public double Topsim(double []v1, double []v2){
        double res;
        int i;
        int a=0;
        int b=0;
        for(i=0;i<v1.length;i++){
            if(v1[i]>0.0000000001)
                a++;
        }
        for(i=0;i<v2.length;i++){
            if(v2[i]>0.0000000001){
                b++;
            }
        }
        int cooc=this.Getoccu(v1, v2);
        if(a+b-cooc>0)
            res=(double)cooc/(a+b-cooc);
        else
            res=0.0;
        return res;
    }
    /*
    The main function to compute topic-to-topic cooccurrence graph
    @parameter: 
    K: Top-K documents (Integer)
    filename: the topic composition file (String)
    outfilename: the graph file name (String)
    */
    public void Run(int K, String filename, String outfilename){
        try {
            Concept2doc doc = new Concept2doc();
            doc.Initnum(tnum);
            doc.GettopK(K, filename);
            conceptsindoc=doc.GetTopic2doc();
            double []v1=new double[30000];
            double []v2=new double[30000];
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter(outfilename, false);
            out = new BufferedWriter(fstream);
            out.write("*Vertices "+keynames.size()+"\n");
            for(int i=0;i<keynames.size();i++){
                out.write((i+1)+" \""+keynames.get(i)+"\"\n");
            }
            out.write("*Edges ");
            int edgenum=0;
            for(int i=0;i<tnum;i++){
                Extract(conceptsindoc[i],v1);
                for(int j=i+1;j<tnum;j++){
                    Extract(conceptsindoc[j],v2);
                    double w=this.Topsim(v1, v2);
                    if(w>0.01){
                        edgenum++;
                    }
                }
            }
            out.write(edgenum+"\n");
            for(int i=0;i<tnum;i++){
                Extract(conceptsindoc[i],v1);
                for(int j=i+1;j<tnum;j++){
                    Extract(conceptsindoc[j],v2);
                    double w=this.Topsim(v1, v2);
                    if(w>0.01){
                        out.write((i+1)+" "+(j+1)+" "+w+"\n");
                    }
                }
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Cograph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String []args){
        if(args.length<1){
            System.out.println("Usage: [topic key file] [topic composition file] [output graph file]");
            System.exit(2);
        }
        Cograph mygraph=new Cograph();
        mygraph.Readkey(args[0]);
        mygraph.Run(100, args[1], args[2]);
    }
}
