package edu.isi.techknacq.topics.readinglist;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.stream.JsonWriter;

import edu.isi.techknacq.topics.topic.Weightpair;

/**
 *
 * @author linhong
 */
public class BaselineReadingListJson {
    Map<String, Double> paperpagerank;
    List<String> topickeys;
    public BaselineReadingListJson(){

    }
        public void ReadPageRankscore(String filename){
        try {
            this.paperpagerank=new HashMap<String,Double>(this.topickeys.size());
            FileInputStream fstream1 = null;
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
                if(sr.contains("*Edge")||sr.contains("*Arc"))
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
    public String Run(String keyname, int K, String docfile, String pagerankfile, String keyword, String doc2conceptfilename){
        try {
            Keyword2concept match1=new Keyword2concept();
            match1.Readkey(keyname);
            ArrayList<Integer> hittopic=match1.Getmatch(keyword);
            this.topickeys=match1.Gettopics();
            Concept2doc doc = new Concept2doc();
            doc.Initnum(this.topickeys.size());
            doc.GettopK(K*4, doc2conceptfilename);
            //doc.Prune();
            ArrayList<String> docnames=doc.Getdocname();
            List mylist=new ArrayList<Weightpair>(100);
            double value;
            boolean []isvisit=new boolean[docnames.size()];
            for(int i=0;i<isvisit.length;i++){
                isvisit[i]=false;
            }
            this.ReadPageRankscore(pagerankfile);
            for (Integer hittopic1 : hittopic) {
                int tindex = hittopic1;
                ArrayList<Integer> mydocs=doc.Getdocs(tindex);
                for (Integer mydoc : mydocs) {
                    int Did = mydoc;
                    if(isvisit[Did]==false)
                        isvisit[Did]=true;
                    else
                        continue;
                    String dockey=docnames.get(Did);
                    if(this.paperpagerank.containsKey(dockey)==true)
                        value=this.paperpagerank.get(dockey);
                    else
                        value=-1;
                    if(value>-1)
                        mylist.add(new Weightpair(value,Did));
                }
            }
            StringWriter writer=new StringWriter();
            JsonWriter s = new JsonWriter(writer);
            s.beginObject();
            s.name("keyword");
            s.value(keyword);
            Collections.sort(mylist);
            ReadDocumentkey rdk = new ReadDocumentkey(docfile);
            rdk.readFile();
            s.name("documents");
            s.beginArray();
            for(int i=0;i<K;i++){
                Weightpair o= (Weightpair)mylist.get(i);
                int Did=o.getindex();
                String id=docnames.get(Did);
                s.value("id "+id+", weight: "+o.getweight());
            }
           s.endArray();
           s.endObject();
           return writer.toString();
        } catch (IOException ex) {
            Logger.getLogger(BaselineReadingList.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static void main(String []args){
        if(args.length<1){
            System.err.println("Usage: [keyword][k] [topic-key-file] [doc-topic-composition] [document-meta-file] [page-rank-score]");
            System.exit(2);
        }
        BaselineReadingListJson myreader=new BaselineReadingListJson();
        String s=myreader.Run(args[2], Integer.parseInt(args[1]), args[4], args[5], args[0], args[3]);
        System.out.println(s);
        //String keyname, int K, String docfile, String pagerankfile, String keyword, String doc2conceptfilename
       //String s=myreader.Run("./old topic/mallet-weighted-key.txt", 10, "acl-meta.json", "Paperpagerank.txt", "machine_learning", "./old topic/concept2doc.txt");
       //System.out.println(s);
        //myreader.Run("mallet-21185-weightedkey.txt", 10, "acl-meta.json", "Paperpagerank.txt", args[0], "mallet-comp.txt");
    }
}
