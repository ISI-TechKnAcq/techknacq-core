/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.isi.techknacq.topics.readinglist;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.graph.Conceptdepth;
import edu.isi.techknacq.topics.graph.Node;
import edu.isi.techknacq.topics.graph.ReadGraph;
import edu.isi.techknacq.topics.topic.Weightpair;
import edu.isi.techknacq.topics.topic.WordPair;

/**
 *
 * @author linhong
 */
public class ReadingList2Json {
    Map<String, Double> paperpagerank;
    List<List<WordPair>> wordintopic;
    List<String> topickeys;
    List<Integer> hittopic;
    Map<String, String> docmap;
    List []topic2docs;
    ArrayList<String> docfiles;
    int []ordertopic;
    HashSet<String> authorlists;
    public ReadingList2Json(){
        
    }
     public void Readdata(String keyword, String keyname, String pagerankfile, String docfile, int dnum, String doc2conceptfile, String filterfile){
        try {
            Keyword2concept match1=new Keyword2concept();
            match1.Readkey(keyname);
            hittopic=match1.Getmatch(keyword);
            this.wordintopic=match1.Getweighttopic();
            this.topickeys=match1.Gettopics();
            ReadPageRankscore(pagerankfile);
            ReadDocumentkey rdk = new ReadDocumentkey(docfile);
            rdk.Readfile();
            docmap=rdk.GetDocmap();
            Concept2doc Getdoc=new Concept2doc();
            Getdoc.Initnum(topickeys.size());
            Getdoc.addfiter(filterfile);
            Getdoc.GettopK(dnum*10, doc2conceptfile);
            topic2docs=Getdoc.GetTopic2doc();
            docfiles=Getdoc.Getdocname();
            //docfiles: The filename of each document
        } catch (IOException ex) {
            Logger.getLogger(ReadingList2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String Getdocmeda(String id){
        if(this.docmap.containsKey(id)==true)
            return this.docmap.get(id);
        else
            return null;
    }
    public ArrayList<Integer> Getdocs(int tindex){
        ArrayList<Integer> mydocs=new ArrayList(topic2docs[tindex].size());
        for(int i=0;i<topic2docs[tindex].size();i++){
            Weightpair o=(Weightpair)topic2docs[tindex].get(i);
            mydocs.add(o.getindex());
        }
        return mydocs;
    }
     public void ReadPageRankscore(String filename){
        try {
            this.paperpagerank=new HashMap(this.topickeys.size());
            FileInputStream fstream1;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            br.readLine(); //skip node vertices line
            br.readLine(); //skip column name line
            String keyname;
            double value;
            String sr;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t| ");
                sr=sc.next();
                if(sr.contains("Edge"))
                    break;
                keyname=sc.next();
                keyname=keyname.substring(1, keyname.length()-1);
                value=sc.nextDouble();
                if(this.paperpagerank.containsKey(keyname)==false){
                    this.paperpagerank.put(keyname, value);
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BaselineReadingList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BaselineReadingList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String Printtopics(int tindex){
        String topicname;
        topicname="\"topic\": \n[";
        double minvalue=1;
        double maxvalue=0;
        for(int i=0;i<this.wordintopic.get(tindex).size();i++){
             WordPair w=wordintopic.get(tindex).get(i);
             double value=w.getprob();
             if(value>maxvalue)
                 maxvalue=value;
             if(value<minvalue){
                 minvalue=value;
             }
        }
        for(int i=0;i<this.wordintopic.get(tindex).size();i++){
            WordPair w=wordintopic.get(tindex).get(i);
            String word=w.getWord();
            double value=w.getprob();
            topicname+="{";
            topicname+="\"word\": \""+word+"\",";
            topicname+="\"value\": "+value+"}";
            if(i<this.wordintopic.get(tindex).size()-1)
                topicname+=",";
        }
        topicname+="],";
        return topicname;
    }
    public String ExtractAuthor(String metadata){
        int index1=metadata.indexOf("author:");
        int index2=metadata.indexOf("title:");
        String author;
        if(index1>=0&&index2>=0){
            author=metadata.substring(index1+8,index2);
        }else
            author=null;
        return author;
    }
    public String Printdocname(String metadata, String did, double score){
        String name;
        int index1=metadata.indexOf("author:");
        int index2=metadata.indexOf("title:");
        String author;
        String title;
        if(index1>=0&&index2>=0){
            author=metadata.substring(index1+8,index2);
        }else
            author=null;
        if(index2>=0){
            title=metadata.substring(index2+7, metadata.length());
        }else
            title=null;
        name="\n\t\t{";
        name =name+"\"author\": \""+author+"\", \"title\": \""+title+"\",\"ID\": \""+did+"\"},";
        return name;
    }
    public String Gettopdoc(int tindex, int dnum, List mylist, boolean [] isvisit){
            ArrayList<Integer> mydocs=this.Getdocs(tindex);
            mylist.clear();
            String docstring="";
            for (Integer mydoc : mydocs) {
                int Did = mydoc;
                if(isvisit[Did]==true)
                    continue;
                String dockey=this.docfiles.get(Did);
                double value;
                if(this.paperpagerank.containsKey(dockey)==true)
                    value=this.paperpagerank.get(dockey);
                else
                    value=-1;
                if(value>-1)
                    mylist.add(new Weightpair(value,Did));
            }
            docstring+="\n\"documents\": [";
            int j=0;
            int dcount=0;
            Collections.sort(mylist);
            while(dcount<dnum&&j<mylist.size()&&dcount<mylist.size()){
                Weightpair o= (Weightpair)mylist.get(j);
                int Did=o.getindex();
                isvisit[Did]=true;
                String dfile=docfiles.get(Did);
                String metavalue=this.Getdocmeda(dfile);
                String author=this.ExtractAuthor(metavalue);
                if(this.authorlists.contains(author)==false){
                    String name=this.Printdocname(metavalue, dfile, o.getweight());
                    docstring+=name;
                    this.authorlists.add(author);
                    dcount++;  
                }
                j++;
            }
            docstring=docstring.substring(0, docstring.length()-1);
            docstring+="],";
            return docstring;
    }
    public void Run(String keyword, String graphfile, int maxtopic, int dnum){
        try {
            FileWriter fstream = new FileWriter(keyword+"_readinglist.json",false);
            BufferedWriter out=new BufferedWriter(fstream);
            ReadGraph myreader=new ReadGraph(graphfile);
            Node []G=myreader.Getgraph();
            this.ordertopic=myreader.Ordernode();
            Conceptdepth Dependency=new Conceptdepth();
            Dependency.InitGraph(G);
            Dependency.InitTopics(this.topickeys);
            boolean []isvisit=new boolean[this.docfiles.size()];
            for(int i=0;i<isvisit.length;i++){
                isvisit[i]=false;
            }
            /*
            Get matched topic and dependent topics
            */
            char []istopicvisit=new char[this.topickeys.size()];
            Arrays.fill(istopicvisit, 'v');
            List mylist=new ArrayList(100);
            this.authorlists=new HashSet();
            out.write("{");
            out.write("\"keyword\": \""+keyword+"\",\n");
            for(int i=0;i<hittopic.size();i++){
                 out.write("\"Match topics\": {\n\t");
                 int tindex=hittopic.get(i);
                 istopicvisit[tindex]='m';
                 out.write(this.Printtopics(tindex));
                 out.write(this.Gettopdoc(tindex, dnum, mylist, isvisit));
                 ArrayList<Integer> deptopics=Dependency.Gettopnode(maxtopic, tindex);
                 out.write("\n\t\t\"Dependency topics\": \n[");
                 for(int j=0;j<deptopics.size();j++){
                     out.write("{");
                    int ddtindex = deptopics.get(j);
                    if(istopicvisit[ddtindex]!='m')
                        istopicvisit[ddtindex]='d';
                    out.write(this.Printtopics(ddtindex));
                    String docstrs=this.Gettopdoc(ddtindex, dnum, mylist, isvisit);
                    docstrs=docstrs.substring(0,docstrs.length()-1);
                    if(j<deptopics.size()-1){
                        out.write(docstrs);
                        out.write("},\n");
                    }
                    else{
                        out.write(docstrs);
                        out.write("}\n");
                    }
                 }
                 out.write("]\n");
                 if(i<hittopic.size()-1)
                    out.write("},\n");
                 else
                     out.write("}\n");
            }
            String s=Dependency.GetsubgraphinString(keyword);
            System.out.println(s);
            out.write("}");
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadingList2.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    public static void main(String []args){
         if (args.length<6){
             System.out.println("Usage [keyword] [doc2topic] [topickey] [topicgraph] [dockey] [pagerankfile] [docs/topic] [max_topic] [filterfile]");
             System.exit(2);
          }   
         int dnum=3;
         int maxtnum=10;
         String filterfile="yes-no.csv";
         if(args.length>6)
             dnum=Integer.parseInt(args[6]);
         if(args.length>7)
             maxtnum=Integer.parseInt(args[7]);
        if(args.length>8)
            filterfile=args[8];
        ReadingList2Json myreadinglist=new ReadingList2Json();
        //String keyword, String keyname, String pagerankfile, String docfile, int dnum, String doc2conceptfile
        myreadinglist.Readdata(args[0],args[2] ,args[5], args[4], dnum, args[1],filterfile);
        //String keyword, String graphfile, int maxtopic, int dnum
        myreadinglist.Run(args[0], args[3], maxtnum, dnum);
    }
}
