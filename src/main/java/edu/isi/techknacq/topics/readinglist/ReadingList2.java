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
import java.util.Set;
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
public class ReadingList2 {
    Map<String, Double> paperpagerank;
    ArrayList<ArrayList<WordPair>> wordintopic;
    List<String> topickeys;
    List<Integer> hittopic;
    Map<String, String> docmap;
    List []topic2docs;
    ArrayList<String> docfiles;
    int []ordertopic;
    Set<String> authorlists;
    public ReadingList2(){
        
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
    public String floattoString(double val,double max){
        int color_dec=(int)(255*(1-val/max));
        String str=Integer.toHexString(color_dec);
        if(str.length()>1)
            str="#"+str+str+str;
        else
            str="#"+0+str+0+str+0+str;
        return str;
    }
//    def invert_hex(hex_number):
//        inverse = hex(abs(int(hex_number, 16) - 255))[2:]
//        # If the number is a single digit add a preceding zero
//        if len(inverse) == 1:
//            inverse = '0'+inverse
//        return inverse
//
//    def float_to_color(f):
//        val = '%x' % int(f * 255)
//        val = invert_hex(val)
//        return '#%s%s%s' % (val, val, val)
    public String Printtopics(int tindex){
        String topicname;
        topicname="<blockquote><p>";
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
            if(word.startsWith("#")&&word.endsWith("#")){
                word=word.replace("#", "");
                topicname+="<a href=\"http://wikipedia.org/w/index.php?search="+word.replace("_", "+");
                topicname+="\">"+"<span style=\"color: "+floattoString(value,maxvalue)+"\" title=\""+(int)(value*100)+"% relevant\">"+word.replace("_", "&nbsp;")+"</span>";
                topicname+="</a>, ";
            }else{
                topicname+="<span style=\"color:"+floattoString(value,maxvalue)+"\" title=\""+(int)(value*100)+"% relevant\">"+word.replace("_", "&nbsp;")+"</span>, ";
            }
        }
        topicname=topicname.substring(0, topicname.length()-2);
        topicname+="</p></blockquote>";
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
        name = author+": " + "<a href=\"http://www.aclweb.org/anthology/" + did.charAt(0) + "/" +did.substring(0, 3) + "/" + did + ".pdf\">" +title+"</a>"+" ("+score+")";
        name = name.replace(" A ", " a ");
        name = name.replace(" Of ", " of ");
        name = name.replace(" As ", " as ");
        name = name.replace(" The ", " the ");
        name = name.replace(" To ",  " to ");
        name = name.replace(" And ", " and ");
        name = name.replace(" For ",  " for ");
        name = name.replace(" In ",  " in ");
        name = name.replace(" With ", " with ");
        name = name.replace(" By ", " by ");
        name = name.replace(" On ", " on ");
        name = name.replace(" - ", " &ndash; ");
        name = name.replace(" -- ", " &ndash; ");
        return name;
    }
    public void Run(String keyword, String graphfile, int maxtopic, int dnum){
        try {
            FileWriter fstream = new FileWriter(keyword+"_readinglist.html",false);
            BufferedWriter out=new BufferedWriter(fstream);
            ReadGraph myreader=new ReadGraph(graphfile);
            Node []G=myreader.Getgraph();
            this.ordertopic=myreader.Ordernode();
            Conceptdepth Dependency=new Conceptdepth();
            Dependency.InitGraph(G);
            Dependency.InitTopics(this.topickeys);
//            List mylist=new ArrayList<>(100);
//            boolean []isvisit=new boolean[this.docfiles.size()];
//            for(int i=0;i<isvisit.length;i++){
//                isvisit[i]=false;
//            }
//            double value;
//            for (Integer tindex : this.hittopic) {
//                ArrayList<Integer> mydocs=this.Getdocs(tindex);
//                for (Integer mydoc : mydocs) {
//                    int Did = mydoc;
//                    if(isvisit[Did]==false)
//                        isvisit[Did]=true;
//                    else
//                        continue;
//                    String dockey=this.docfiles.get(Did);
//                    if(this.paperpagerank.containsKey(dockey)==true)
//                        value=this.paperpagerank.get(dockey);
//                    else
//                        value=-1;
//                    if(value>-1)
//                        mylist.add(new Weightpair(value,Did));
//                }
//                ArrayList<Integer> deptopics=Dependency.Gettopnode(maxtopic, tindex);
//                for (Integer ddtopic : deptopics) {
//                    ArrayList<Integer> dddocs=this.Getdocs(ddtopic);
//                        for (Integer mydoc : dddocs) {
//                           if(isvisit[mydoc]==false)
//                                isvisit[mydoc]=true;
//                            else
//                                continue;
//                            String dockey=this.docfiles.get(mydoc);
//                            if(this.paperpagerank.containsKey(dockey)==true)
//                                value=this.paperpagerank.get(dockey);
//                            else
//                                value=-1;
//                            if(value>-1)
//                                mylist.add(new Weightpair(value,mydoc));
//                    }
//                }
//            }
//            Collections.sort(mylist);
//            for(int i=0;i<isvisit.length;i++){
//                isvisit[i]=false;
//            }
//            int maxdoc=maxtopic*dnum*3;
//            maxdoc+=this.hittopic.size()*dnum*4;
//            for (int i=0;i<maxdoc&&i<mylist.size();i++){
//                 Weightpair o= (Weightpair)mylist.get(i);
//                int Did=o.getindex();
//                isvisit[Did]=true;
//            }
            out.write("<html>\n" +
"<head>\n" +
"<title>Reading List</title>\n" +
"<style type=\"text/css\">\n" +
"body {\n" +
"    margin: 2em auto;\n" +
"    font-family: 'Univers LT Std', 'Helvetica', sans-serif;\n" +
"    max-width: 900px;\n" +
"    width: 90%;\n" +
"}\n" +
"\n" +
"article {\n" +
"    border-top: 4px solid #888;\n" +
"    padding-top: 3em;\n" +
"    margin-top: 3em;\n" +
"}\n" +
"\n" +
"section {\n" +
"    padding-bottom: 3em;\n" +
"    border-bottom: 4px solid #888;\n" +
"    margin-bottom: 4em;\n" +
"}\n" +
"\n" +
"section section {\n" +
"    border: 0px;\n" +
"    padding: 0px;\n" +
"    margin: 0em 0em 3em 0em;\n" +
"}\n" +
"\n" +
"h1 { font-size: 18pt; }\n" +
"\n" +
"h2 { font-size: 14pt; }\n" +
"\n" +
"label { margin-right: 6px; }\n" +
"\n" +
"input { margin-left: 6px; }\n" +
"\n" +
"div.topic {\n" +
"    padding: 1em;\n" +
"}\n" +
"\n" +
"p.rate { font-weight: bold; margin-left: 2em; }\n" +
"\n" +
"blockquote { margin-left: 40px; }\n" +
"\n" +
"a { text-decoration: none; font-style: italic; border-bottom: 1px dotted grey; }\n" +
"\n" +
"a:hover { color: blue !important; }\n" +
"a:hover span { color: blue !important; }\n" +
"\n" +
"</style>\n" +
"</head>\n" +
"<body>\n" +
"<h1>Reading List for "+keyword+" </h1>");
            
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
            
            for(int i=0;i<hittopic.size();i++){
                 int tindex=hittopic.get(i);
                 istopicvisit[tindex]='m';
                 if(i==0){
                     istopicvisit[tindex]='v';
                     //put the topic tindex at the very begining of the order list;
                     out.write("<section>");
                     out.write("<h2>"+"Overall topic: "+"</h2>");
                     out.write("<div class=\"topic\">");
                     out.write(this.Printtopics(tindex));
                    /*
                    Start retrival high quality papers;
                    */
                    ArrayList<Integer> mydocs=this.Getdocs(tindex);

                    mylist.clear();
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
                    int dcount=Math.min(dnum, mylist.size());
                    out.write("<p>The best relevant "+dcount+" documents: </p>");
                    int j=0;
                    dcount=0;
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
                            out.write("<li>"+name+"</li>");
                            this.authorlists.add(author);
                            dcount++;
                          }
                          j++;
                    }
                    out.write("</div>");
                    out.write("</section>");  
                }
                 ArrayList<Integer> deptopics=Dependency.Gettopnode(maxtopic, tindex);
                 for (Integer deptopic : deptopics) {
                    int ddtindex = deptopic;
                    if(istopicvisit[ddtindex]!='m')
                        istopicvisit[ddtindex]='d';
                 }
            }
            Dependency.Getsubgraph(keyword);
            //order topics by knowledge complexity
            for(int i=0;i<this.ordertopic.length;i++){
               int tindex=ordertopic[i];
               if(istopicvisit[tindex]=='v')
                   continue;
               out.write("<section>");
               if(istopicvisit[tindex]=='m'){
                   out.write("<h2>"+"Matched topic: "+"</h2>");
               }else{
                    out.write("<h2>"+"Dependency topic: "+"</h2>");
               }
                out.write("<div class=\"topic\">");
                out.write(this.Printtopics(tindex));
                /*
                Start retrival high quality papers;
                */
                ArrayList<Integer> mydocs=this.Getdocs(tindex);
                mylist.clear();
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
                int dcount=Math.min(dnum, mylist.size());
                out.write("<p>The best relevant "+dcount+" documents: </p>");
                int j=0;
                dcount=0;
                Collections.sort(mylist);
                while(dcount<dnum&&j<mylist.size()&&dcount<mylist.size()){
                      Weightpair o= (Weightpair)mylist.get(j);
                      int Did=o.getindex();
                      isvisit[Did]=true;
                      String dfile=docfiles.get(Did);
                      String metavalue=this.Getdocmeda(dfile);
                      String author=this.ExtractAuthor(metavalue);
                      if(this.authorlists.contains(author)==false){
                        String name=this.Printdocname(metavalue, dfile,o.getweight());
                        out.write("<li>"+name+"</li>");
                        this.authorlists.add(author);
                        dcount++;
                      }
                      j++;
                }
                out.write("</div>");
                out.write("</section>");  
            }
            out.write("</form>\n" +
            "</article>\n" +
            "</body>\n" +
            "</html>");
            out.close();
            Dependency.Getsubgraph(keyword);
        } catch (IOException ex) {
            Logger.getLogger(ReadingList2.class.getName()).log(Level.SEVERE, null, ex);
        } 
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
     public static void main(String []args){
         //args[0]: keyword;
            //args[1]: doc2topicfilename;
            //args[2]: topicweightedkeyname;
            //args[3]: topicgraphfilename;
            //args[4]: dockeyname;
            //args[5]: number of docs per topic
            //args[6]: number of maximum dependence topics;
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
        ReadingList2 myreadinglist=new ReadingList2();
        //String keyword, String keyname, String pagerankfile, String docfile, int dnum, String doc2conceptfile
        myreadinglist.Readdata(args[0],args[2] ,args[5], args[4], dnum, args[1],filterfile);
        //String keyword, String graphfile, int maxtopic, int dnum
        myreadinglist.Run(args[0], args[3], maxtnum, dnum);
    }
}
