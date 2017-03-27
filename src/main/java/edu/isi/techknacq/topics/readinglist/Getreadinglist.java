package edu.isi.techknacq.topics.readinglist;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.graph.Conceptdepth;
import edu.isi.techknacq.topics.graph.Node;
import edu.isi.techknacq.topics.graph.ReadGraph;

public class Getreadinglist {
    public static void main(String []args) {
        FileWriter fstream=null;
        try {
            if (args.length<6) {
                System.out.println("Usage [keyword] [doc2topic] [topickey] [topicgraph] [dockey] [docs/topic] [max_topic]");
                System.exit(2);
            }   //args[0]: keyword;
            //args[1]: doc2topicfilename;
            //args[2]: topicweightedkeyname;
            //args[3]: topicgraphfilename;
            //args[4]: dockeyname;
            //args[5]: number of docs per topic
            //args[6]: number of maximum dependence topics;
            int dnum=3;
            int maxtnum=10;
            String filterfile="yes-no.csv";
            if (args.length>5)
                dnum=Integer.parseInt(args[5]);
            if (args.length>6)
                maxtnum=Integer.parseInt(args[6]);
            if (args.length>7)
                filterfile=args[7];
            Keyword2concept match1=new Keyword2concept();
            match1.readKey(args[2]);
            List<Integer> hittopic=match1.Getmatch(args[0]);
            //hittopic: topics that matches the input keyword
            List<String> topics=match1.Gettopics();
            //topics: the topics with word distribution
            Concept2doc Getdoc=new Concept2doc();
            Getdoc.Initnum(topics.size());
            Getdoc.addfiter(filterfile);
            Getdoc.GettopK(dnum, args[1]);
            List<String> docfiles=Getdoc.Getdocname();
            //docfiles: The filename of each document
            ReadDocumentkey rdk = new ReadDocumentkey(args[4]);
            rdk.readFile();
            //rdk: read the title and author information of each
            ReadGraph myreader=new ReadGraph(args[3]);
            Node []G=myreader.Getgraph();
            Conceptdepth Dependency=new Conceptdepth();
            Dependency.InitGraph(G);
            Dependency.InitTopics(topics);
            fstream = new FileWriter(args[0]+"_readinglist.txt",false);
            BufferedWriter out=new BufferedWriter(fstream);
            for(int i=0;i<hittopic.size();i++) {
                out.write("Matched topic "+i+" : \n");
                int tindex=hittopic.get(i);
                out.write("```\n");
                out.write(topics.get(tindex)+"\n");
                out.write("```\n");
                out.write("\t\t==The best relevant "+dnum+" documents: \n");
                ArrayList<Integer> docindex=Getdoc.Getdocs(tindex);
                for(int j=0;j<docindex.size();j++) {
                    int dindex=docindex.get(j);
                    String dfile=docfiles.get(dindex);
                    out.write("\t\t- "+dfile);
                    String value=rdk.Getdocumentkey(dfile);
                    out.write(":"+value+"\n");
                }
                ArrayList<Integer> deptopics=Dependency.Gettopnode(maxtnum, tindex);
                for(int k=0;k<deptopics.size();k++) {
                    int ddtindex=deptopics.get(k);
                    out.write("\n\ndependency topic: \n");
                    out.write("```\n");
                    out.write(topics.get(ddtindex)+"\n");
                    out.write("```\n");
                    out.write("\t\t\t\t==The best relevant "+dnum+" documents: \n");
                    ArrayList<Integer> docindex2=Getdoc.Getdocs(ddtindex);
                    for(int j=0;j<docindex2.size();j++) {
                        int dindex=docindex2.get(j);
                        String dfile=docfiles.get(dindex);
                        out.write("\t\t\t\t- "+dfile);
                        String value=rdk.Getdocumentkey(dfile);
                        out.write(":"+value+"\n");
                    }
                }
                out.write("end of the matched topic "+i+"\n");
                out.write("==========================================\n");
            }
            out.close();
            Dependency.Getsubgraph(args[0]);
        } catch (IOException ex) {
            Logger.getLogger(Getreadinglist.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
