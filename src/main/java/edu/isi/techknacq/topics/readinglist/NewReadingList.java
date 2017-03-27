/*
  The new reading List Generation Algorithm
*/

package edu.isi.techknacq.topics.readinglist;

import edu.isi.techknacq.topics.graph.Conceptdepth;
import edu.isi.techknacq.topics.graph.Node;
import edu.isi.techknacq.topics.graph.ReadGraph;
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
import edu.isi.techknacq.topics.topic.Weightpair;
import edu.isi.techknacq.topics.topic.WordPair;

/**
 *
 * @author linhong
 * @since May 15 2015
 */
public class NewReadingList {
    Keyword2concept match1;
    //the function that matches a query to topics

    HashMap<String, Double> paperpagerank;
    //the map function that matches each paper to the pagerank score
    HashMap<String, String> paperPVtype;
    //the map function that matches each paper to the pedegogical role tyoe
    HashMap<String, Double> papercomplexity;
    //the map function that matches each paper to the text complexity score
    HashMap<String, Double> type2score;
    //the map function that assignes each pedegogical role type with a score

    ArrayList<ArrayList<WordPair>> wordintopic;
    ArrayList<String> topickeys;
    ArrayList<Integer> hittopic;
    Map<String, String> docmap;
    List []topic2docs;
    ArrayList<String> docfiles;
    int []ordertopic;
    HashSet<String> authorlists;
    int relevencek=10;
    double w1=0.6;
    double w2=0.2;
    double w3=0.1;
    double w4=0.001;

    public void NewReadingList() {

    }
    /**
     * @param keyname: the file name of word distribution for each topic
     * @param pagerankfile: the file name of page rank score
     * @param docfile: the file name of Meta-data information for each document
     * @param dnum: the number of documents for each topic
     * @param doc2conceptfile: the filename of topic composition for each document
     * @param filterfile: the filename of bad paper versus good paper
     @
    **/
    public void Readdata(String keyname, String pagerankfile, String docfile,
                         int dnum, String doc2conceptfile, String filterfile) {
        match1 = new Keyword2concept();
        match1.readKey(keyname);
        System.out.println("finish reading topic");
        this.wordintopic=match1.Getweighttopic();
        this.topickeys=match1.Gettopics();
        ReadPageRankscore(pagerankfile);
        System.out.println("finish reading pagerank");
        ReadDocumentkey rdk = new ReadDocumentkey(docfile);
        rdk.readFile();
        System.out.println("finish reading document");
        docmap=rdk.GetDocmap();
        Concept2doc Getdoc=new Concept2doc();
        Getdoc.Initnum(topickeys.size());
        Getdoc.addfiter(filterfile);
        Getdoc.GettopK(dnum*relevencek, doc2conceptfile);
        topic2docs=Getdoc.GetTopic2doc();
        docfiles=Getdoc.Getdocname();
        System.out.println("finish reading data");
    }

    /*
     * Read the pedagogical type of each document
     */
    public void ReadPV(String PVtypefile) {
        this.paperPVtype=new HashMap<String,String>(30000);
        try {
            FileInputStream fstream1;
            fstream1 = new FileInputStream(PVtypefile);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String docid;
            String type;
            while((strline=br.readLine())!=null) {
                Scanner sc=new Scanner(strline);
                sc.next();
                docid=sc.next();
                type=sc.next();
                if(this.paperPVtype.containsKey(docid)==false) {
                    this.paperPVtype.put(docid, type);
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NewReadingList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NewReadingList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
      Read the configuration files that provides:
      1) the heuristic rules that combines different document features for ranking
      2) the heuristic rules that assignes different document types with different weight
      3) other parameters such as relevence threshold
      Those heuristics rules will be automatically learned with feedbacks from users
      who evaluate the reading list
    */
    public void ReadConfiguration(String filename) {
        try {
            this.type2score=new HashMap<String,Double>(5);
            FileInputStream fstream1;
            fstream1 = new FileInputStream(filename);
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            while((strline=br.readLine())!=null) {
                if(strline.contains("#PVvalue")==true) {
                    while((strline=br.readLine()).contains("#end")==false) {
                        Scanner sc=new Scanner(strline);
                        sc.useDelimiter("\t");
                        String type=sc.next();
                        double v=sc.nextDouble();
                        this.type2score.put(type, v);
                    }
                }
                if(strline.contains("#relevencethreshold")==true) {
                    strline=br.readLine();
                    this.relevencek=Integer.parseInt(strline);
                    br.readLine();
                }
                if (strline.contains("#weight")==true) {
                    while((strline=br.readLine()).contains("#end")==false) {
                        Scanner sc=new Scanner(strline);
                        sc.useDelimiter("\t");
                        int id=sc.nextInt();
                        double w=sc.nextDouble();
                        if(id==1)
                            this.w1=w;
                        if(id==2)
                            this.w2=w;
                        if(id==3)
                            this.w3=w;
                        if(id==4)
                            this.w4=w;
                    }
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NewReadingList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NewReadingList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String Getdocmeda(String id) {
        if(this.docmap.containsKey(id)==true)
            return this.docmap.get(id);
        else
            return "author: ??, title: ??";
    }
    public ArrayList<Weightpair> Getdocs(int tindex) {
        ArrayList<Weightpair> mydocs=new ArrayList<Weightpair>(topic2docs[tindex].size());
        for(int i=0;i<topic2docs[tindex].size();i++) {
            Weightpair o=(Weightpair)topic2docs[tindex].get(i);
            mydocs.add(o);
        }
        return mydocs;
    }
    public void ReadPageRankscore(String filename) {
        try {
            this.paperpagerank=new HashMap<String,Double>(this.topickeys.size());
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
            while((strline=br.readLine())!=null) {
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t| ");
                sr=sc.next();
                if(sr.contains("*Edge")||sr.contains("*Arc"))
                    break;
                keyname=sc.next();
                keyname=keyname.substring(1, keyname.length()-1);
                value=sc.nextDouble();
                if(this.paperpagerank.containsKey(keyname)==false) {
                    this.paperpagerank.put(keyname, value);
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NewReadingList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NewReadingList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String Printtopics(int tindex) {
        String topicname;
        topicname="\"topic\": [\n";
        double minvalue=1;
        double maxvalue=0;
        for(int i=0;i<this.wordintopic.get(tindex).size();i++) {
            WordPair w=wordintopic.get(tindex).get(i);
            double value=w.getprob();
            if(value>maxvalue)
                maxvalue=value;
            if(value<minvalue) {
                minvalue=value;
            }
        }
        for(int i=0;i<this.wordintopic.get(tindex).size();i++) {
            WordPair w=wordintopic.get(tindex).get(i);
            String word=w.getWord();
            double value=w.getprob();
            topicname+="{";
            topicname+="\"word\": \""+word+"\",";
            topicname+="\"value\": "+value+"}";
            if(i<this.wordintopic.get(tindex).size()-1)
                topicname+=",";
        }
        topicname+="],\n";
        topicname+="\"topicID\":";
        topicname+=" ";
        topicname+=tindex;
        topicname+=",";
        return topicname;
    }
    public String ExtractAuthor(String metadata) {
        int index1;
        int index2;
        if(metadata!=null) {
            index1=metadata.indexOf("author:");
            index2=metadata.indexOf("title:");
        }else{
            index1=-1;
            index2=-1;
        }
        String author;
        if(index1>=0&&index2>=0) {
            author=metadata.substring(index1+8,index2);
        }else
            author=null;
        return author;
    }
    public String Printdocname(String metadata, String did, double score) {
        String name;
        int index1;
        int index2;
        if(metadata!=null) {
            index1=metadata.indexOf("author:");
            index2=metadata.indexOf("title:");
        }else{
            index1=-1;
            index2=-1;
        }
        String author;
        String title;
        if(index1>=0&&index2>=0) {
            author=metadata.substring(index1+8,index2);
        }else
            author=null;
        if(index2>=0) {
            title=metadata.substring(index2+7, metadata.length());
        }else
            title=null;
        name="\n\t\t{";
        name =name+"\"author\": \""+author+"\", \"title\": \""+title+"\",\"ID\": \""+did+"\"},";
        return name;
    }
    /*
      Order documents that are relevent to topic tindx by the combination of document features:
      1. pagerank score;
      2. pedogocial type score;
      3. text complexity score;
      4. relevence score;
    */
    public String Gettopdoc(int tindex, int dnum, List mylist, boolean [] isvisit) {
        //===============================================
        ArrayList<Weightpair> mydocs=this.Getdocs(tindex);
        //===============================================
        mylist.clear();
        String docstring="";
        for (int i=0;i<mydocs.size();i++) {
            Weightpair o=mydocs.get(i);
            int Did=o.getindex();
            double value4=o.getweight(); //value4: relevence score;
            if(isvisit[Did]==true)
                continue;
            String dockey=this.docfiles.get(Did);
            double value1; //value1: pagerankscore;
            if(this.paperpagerank.containsKey(dockey)==true)
                value1=this.paperpagerank.get(dockey);
            else
                value1=0;
            double value2; //pedgogical value score;
            if(this.paperPVtype.containsKey(dockey)==true) {
                String type=this.paperPVtype.get(dockey);
                if(this.type2score.containsKey(type)==false) {
                    value2=0;
                }else
                    value2=this.type2score.get(type);
            }else
                value2=0;
            double value=value1*w1+value2*w2+value4*w4;
            mylist.add(new Weightpair(value,Did));
        }
        docstring+="\n\"documents\": [";
        int j=0;
        int dcount=0;
        Collections.sort(mylist);
        while(dcount<dnum&&j<mylist.size()&&dcount<mylist.size()) {
            Weightpair o= (Weightpair)mylist.get(j);
            int Did=o.getindex();
            isvisit[Did]=true;
            String dfile=docfiles.get(Did);
            String metavalue=this.Getdocmeda(dfile);
            if(metavalue==null) {
                System.out.println(Did);
            }
            String author=this.ExtractAuthor(metavalue);
            if(this.authorlists.contains(author)==false) {
                String name=this.Printdocname(metavalue, dfile, o.getweight());
                docstring+=name;
                this.authorlists.add(author);
                dcount++;
            }
            j++;
        }
        docstring=docstring.substring(0, docstring.length()-1);
        docstring+="]";
        return docstring;
    }
    public void Run(String keyword, String graphfile, int maxtopic, int dnum) {
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
            for(int i=0;i<isvisit.length;i++) {
                isvisit[i]=false;
            }
            /*
              Get matched topic and dependent topics
            */
            //Get matched topic (start)
            hittopic=match1.Getmatch(keyword);
            //Get matched topid (end)
            char []istopicvisit=new char[this.topickeys.size()];
            Arrays.fill(istopicvisit, 'v');
            List mylist=new ArrayList<Weightpair>(100);
            this.authorlists=new HashSet<String>();
            out.write("{");
            out.write("\"keyword\": \""+keyword+"\",\n");
            /*
              Get dependency topic (start)
            */
            out.write("\"Match documents\": [\n\t");
            for(int i=0;i<hittopic.size();i++) {
                int tindex=hittopic.get(i);
                istopicvisit[tindex]='m';
                ArrayList<Integer> deptopics=Dependency.Gettopnode(maxtopic, tindex);
                for(int j=0;j<deptopics.size();j++) {
                    int ddtindex = deptopics.get(j);
                    if(istopicvisit[ddtindex]!='m')
                        istopicvisit[ddtindex]='d';
                }
                out.write("{");
                out.write(this.Printtopics(tindex));

                out.write(this.Gettopdoc(tindex, dnum, mylist, isvisit));
                out.write("\n}");
                if(i<hittopic.size()-1)
                    out.write(",\n");
                else
                    out.write("],\n");
            }

            /*
              Get dependency topic (END)
            */

            //==================================================
            //Order dependency topic by topic complexity (Start)
            //===================================================
            int endindex=0;
            for(int i=0;i<this.ordertopic.length;i++) {
                int tindex=ordertopic[i];
                if(istopicvisit[tindex]=='v'||istopicvisit[tindex]=='m')
                    continue;
                if (istopicvisit[tindex]=='d'&&i>endindex)
                    endindex=i;
            }
            System.out.println(endindex);
            out.write("\"Dependency documents\": [\n\t");
            for(int i=0;i<this.ordertopic.length;i++) {
                int tindex=ordertopic[i];
                if(istopicvisit[tindex]=='v'||istopicvisit[tindex]=='m')
                    continue;
                out.write("{");
                out.write(this.Printtopics(tindex));
                out.write(this.Gettopdoc(tindex, dnum, mylist, isvisit));
                out.write("\n}");
                if(i<endindex)
                    out.write(",\n");
                else
                    out.write("]\n");
            }
            //==================================================
            //Order dependency topic by topic complexity (END)
            //===================================================
            String s=Dependency.GetsubgraphinString(keyword);
            System.out.println(s);
            out.write("}");
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(NewReadingList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String []args) {
        if (args.length<6) {
            System.out.print("Usage [keyword] [doc2topic] [topickey] [topicgraph] [dockey] [pagerankfile] [docs/topic] [max_topic] [filterfile]");
            System.out.println("[PVfile] [Configure file]");
            System.exit(2);
        }
        int dnum=3;
        int maxtnum=5;
        String filterfile="yes-no.csv";
        String configurefile="config.txt";
        String pvfile="doc_label_details_1390.txt";
        if (args.length>6)
            dnum=Integer.parseInt(args[6]);
        if (args.length>7)
            maxtnum=Integer.parseInt(args[7]);
        if (args.length>8)
            filterfile=args[8];
        if (args.length>9)
            pvfile=args[9];
        if (args.length>10)
            configurefile=args[10];
        NewReadingList myreadinglist=new NewReadingList();
        //String keyword, String keyname, String pagerankfile, String docfile, int dnum, String doc2conceptfile
        myreadinglist.Readdata(args[2] ,args[5], args[4], dnum, args[1],filterfile);
        myreadinglist.ReadPV(pvfile);
        myreadinglist.ReadConfiguration(configurefile);
        //String keyword, String graphfile, int maxtopic, int dnum
        myreadinglist.Run(args[0], args[3], maxtnum, dnum);
    }
}
