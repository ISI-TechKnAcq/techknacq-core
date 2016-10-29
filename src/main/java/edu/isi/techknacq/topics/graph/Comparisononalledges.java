package edu.isi.techknacq.topics.graph;


import infodynamics.measures.continuous.kernel.EntropyCalculatorKernel;
//import infodynamics.measures.discrete.MutualInformationCalculator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.isi.techknacq.topics.readinglist.Concept2doc;
import edu.isi.techknacq.topics.readinglist.citationgraph;
import edu.isi.techknacq.topics.topic.Indexpair;
import edu.isi.techknacq.topics.topic.Weightpair;
import edu.isi.techknacq.topics.util.ReadWeightedTopicKey;


/**
 *
 * @author linhong
 */
public class Comparisononalledges {
    ArrayList<String> keynames;
    int tnum;
    List []conceptsindoc;
    List []conceptsinword;
    public double[] flowscores;
    public double [][]flowmatrics;
    EntropyCalculatorKernel entropy;
    ArrayList<Double> topicscores;

    public Comparisononalledges() {
        entropy=new EntropyCalculatorKernel();
        entropy.initialise();
    }

    public void Readkey(String filename) {
        ReadWeightedTopicKey myreader=new ReadWeightedTopicKey();
        myreader.read(filename,5);
        keynames=myreader.Getkeynames();
        myreader.Concepttowords(filename);
        conceptsinword=myreader.Getconceptinword();
        tnum=this.keynames.size();
        flowmatrics=new double[tnum][tnum];

        // Now that we know the number of topics, introduce default topic
        // scores in case they're not provided.
        topicscores = new ArrayList<Double>(tnum);
        for (int i = 0; i < tnum; i++) {
            topicscores.add(1.0);
        }
    }

    public void Readtopicscore(String filename) {
        try {
            Scanner sc=new Scanner(new File(filename));
            while(sc.hasNext()){
                //System.out.println(sc.next());
                topicscores.add(sc.nextDouble());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Comparisononalledges.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Extract(List a1, double []v1) {
        Arrays.fill(v1,0.0);
        for(int i=0;i<a1.size();i++){
            Weightpair o=(Weightpair)a1.get(i);
            v1[o.getindex()]=o.getweight();
        }
    }

    public void Extract2(List a1, double []v1) {
        Arrays.fill(v1,0.0);
        for(int i=0;i<a1.size();i++){
            Indexpair o=(Indexpair)a1.get(i);
            v1[o.getindex()]=o.getweight();
        }
    }

    public int Getoccu(double []v1, double []v2) {
        int c = 0;
        int i = 0;
        int j = 0;
        while (i < v1.length && j < v2.length){
            if (v1[i] > 0.0000000001 && v2[j] > 0.00000000001) {
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

    public double klDivergence(double[] p1, double[] p2) {
      double klDiv = 0.0;

      for (int i = 0; i < p1.length; ++i) {
        if (p1[i] < 0.000001) { continue; }
        if (p2[i] <0.0000001) { continue; } // Limin

      klDiv += p1[i] * Math.log( p1[i] / p2[i] );
      }

      return klDiv / Math.log(2); // moved this division out of the loop -DM
    }
    public double GetDiffSim(double []v1, double []v2){
        double res=0.0;
         int nzero1=0;
        int nzero2=0;
        int a=0;
        int b=0;
        int i=0;
        int j=0;
        while(i<v1.length&&j<v2.length){
            if(v1[i]>0.0000000001)
                nzero1++;
            if(v2[j]>0.0000000001)
                nzero2++;
            if(v1[i]>0.0000000001&&v2[j]>0.00000000001){
               if(v1[i]>v2[j]){
                   a++;
               }else
                   b++;
            }
            i++;
            j++;
        }
        while(i<v1.length){
            if(v1[i]>0.0000000001)
                nzero1++;
            i++;
        }
        while(j<v2.length){
            if(v2[j]>0.0000000001)
                nzero2++;
            j++;
        }
        res=(double)((double)(a-b)*this.Getoccu(v1, v2)/(nzero1+nzero2));
        return res;
    }
    public double Getentropy(double []v1, double []v2){
        double ce;
        //System.out.println("res "+res);
        entropy.setObservations(v1);
        ce=entropy.computeAverageLocalOfObservations();
        entropy.setObservations(v2);
        ce-=entropy.computeAverageLocalOfObservations();
        //System.out.println();
        ce+=this.klDivergence(v1, v2);
        ce-=this.klDivergence(v2, v1);
        //System.out.println("ce "+ce);
        return ce;
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
    public void Run(String filename, int K, String citationfile, int maxfilewordnum){
        try {
            Concept2doc doc = new Concept2doc();
            doc.Initnum(tnum);
            doc.GettopK(K, filename);
            conceptsindoc=doc.GetTopic2doc();
            int cooccount;
            double informationflow;
            //double CEdoc;
            double CEword;
            double topicsim;
            double wordsim;
            double cocite;
            double hierdoc;
            double hierword;
            double citewang;
            double []v1=new double[maxfilewordnum];
            double []v2=new double[maxfilewordnum];
            double []v3=new double[maxfilewordnum];
            double []v4=new double[maxfilewordnum];
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter("alledge.tsv", false);
            out = new BufferedWriter(fstream);
            out.write("sid\ts_topic\ttid\tt_topic\tsim_doc\tsim_word\tinformation_flow\tCE\tcitation\tHier\tCitation_Wang\n");
            citationgraph mycite=new citationgraph();
            mycite.settopicnum(tnum);
            mycite.setmaxfilenum(maxfilewordnum);
           // mycite.Readcitation("acl.txt");
            mycite.Readcitation(citationfile);
            mycite.Readc2d(filename);
            double [][]t2t=mycite.Computecitationlinks();
            FileWriter fstream2=new FileWriter("entropy1.txt",false);
            BufferedWriter out2=new BufferedWriter(fstream2);
            FileWriter fstream3=new FileWriter("entropy2.txt",false);
            BufferedWriter out3=new BufferedWriter(fstream3);

            for (int i = 0; i < tnum; i++) {
                if (this.topicscores.get(i) < 0.42)
                    continue;
                Extract(conceptsindoc[i],v1);
                Extract2(conceptsinword[i],v3);
                for(int j=0;j<tnum;j++){
                    if (this.topicscores.get(j) < 0.42)
                        continue;
                    if(j==i)
                        continue;
                    Extract(conceptsindoc[j],v2);
                    Extract2(conceptsinword[j],v4);
                    cooccount=this.Getoccu(v1, v2);
                    if(cooccount>2){
                        topicsim=this.Topsim(v1, v2);
                        wordsim=this.Topsim(v3, v4);
                        //System.out.println(flowmatrics[i][j]);
                        if(flowscores[i]>flowscores[j])
                            informationflow=-this.flowmatrics[i][j];
                        else
                            informationflow=this.flowmatrics[i][j];
                        //CEdoc=this.Getentropy(v1, v2);
                        CEword=this.Getentropy(v3, v4);
                        hierdoc=this.GetDiffSim(v1, v2);
                        hierword=this.GetDiffSim(v3, v4);
                        cocite=(t2t[i][j]-t2t[j][i])/this.Getoccu(v1, v2);
                        CEword=CEword/1.9514+hierword/1.1667;
                        citewang=t2t[i][j];
                        if(Math.abs(CEword)>0.007){
                            if(CEword>0){
                                out2.write(i+"\t"+j+"\t"+CEword+"\n");
                            }else{
                                out2.write(j+"\t"+i+"\t"+(0-CEword)+"\n");
                            }
                        }
                        if(Math.abs(cocite)>10){
                            if(cocite>0){
                                out3.write(i+"\t"+j+"\t"+cocite+"\n");
                            }else{
                                out3.write(j+"\t"+i+"\t"+(0-cocite)+"\n");
                            }
                        }
                        out.write(i+"\t"+this.keynames.get(i)+"\t"+j+"\t"+this.keynames.get(j)+"\t"+topicsim+"\t"+wordsim+"\t"+informationflow+"\t");
                        out.write(CEword+"\t"+cocite+"\t");
                        out.write(hierdoc+"\t"+citewang+"\n");
                        if(i==55||i==59||i==82||i==172){
                            System.out.print(i+"\t"+j+"\t"+wordsim+"\t"+informationflow+"\t");
                            System.out.println(CEword+"\t"+cocite+"\t"+hierdoc+"\t"+citewang);
                        }
                    }
                }
            }
            out2.close();
            out3.close();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Comparisononalledges.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String []args) {
        if (args.length < 1){
            System.out.println("Usage [keyfile] [tree file] [topic composition file] [# topics] [citation file] [flow file] ([topicscorefile] [maxfilewordnum])");
            System.exit(2);
        }
        Comparisononalledges alledge = new Comparisononalledges();
        alledge.Readkey(args[0]);
        ReadflowNetwork myreader = new ReadflowNetwork();
        myreader.Readkey(args[0]);
        alledge.flowscores = myreader.Readflowscore(args[1]);
        myreader.Readflowtomatrix(args[5], alledge.flowmatrics);

        if (args.length > 6)
            alledge.Readtopicscore(args[6]);

        int maxfilewordnum = 400000;
        if (args.length > 7)
            maxfilewordnum = Integer.parseInt(args[7]);

        alledge.Run(args[2], Integer.parseInt(args[3]), args[4],
                    maxfilewordnum);

//        alledge.Readkey("mallet-keys-2gm-200.txt");
//        alledge.ReadInformationflowScore("mallet0702.tree");
//        alledge.Run("concept2doc.txt", 200,"acl.txt");
    }
}
