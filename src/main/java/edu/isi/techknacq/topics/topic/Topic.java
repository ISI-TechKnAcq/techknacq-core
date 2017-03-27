package edu.isi.techknacq.topics.topic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.util.Readfile;
import edu.isi.techknacq.topics.util.Readresults;
import edu.isi.techknacq.topics.util.StrUtil;

/**
 *
 * @author linhong
 */
public class Topic {
    public Topic() {

    }

    public void Runtopic(String dirname, int topicnum, int wordnum,
            double alpha, String prefix) {
        try {
            ArrayList<String> filenames=StrUtil.initFolder(dirname);
            ArrayList<String> posts=new ArrayList<String>(filenames.size());
            Readfile myreader = new Readfile();
            System.out.println(filenames.size());
            for(int i=0;i<filenames.size();i++){
                String res = myreader.read(filenames.get(i));
                posts.add(res);
                if(i%1000==0)
                    System.out.println(i);
            }
            System.out.println("finish reading files");
            Wordmodel mymodel=new Wordmodel();
            mymodel.InitPost(posts);
            mymodel.Computerwordmodel();
            mymodel.Savewordmodel("./lib/wordmodel.txt");
            mymodel.Saveword("./lib/words.txt");
            mymodel.SavetopK(30, "./lib/"+prefix+"top.csv");
            String []words=mymodel.Getwords();
            int[]df=mymodel.Getcount();
            System.out.println("finish computing dictionary");
            Wordmatrix mymatrix=new Wordmatrix();
            mymatrix.Initwords(words);
            mymatrix.Initwordfreq(df);
            mymatrix.Initcontent(posts);
            mymatrix.initmatrix("./lib/wordmatrix.txt");
            mymodel.clear();
            mymatrix.clear();
            System.out.println("finish document to word representation computation");
            Process p;
            int i;
            String command="./lib/lda est "+alpha+" "
                    +topicnum+" ./lib/settings.txt ./lib/wordmatrix.txt"+
                    " random ./lib/output";
            //String normalized = new URI(command).normalize().getPath();
            System.out.println(command);
            p=Runtime.getRuntime().exec(command);
            BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line=reader.readLine();
            while(line!=null)
            {
                System.out.println(line);
                line=reader.readLine();
            }
            i=p.waitFor();
            System.out.println ("waitFor = " + i );
            System.out.println("Estimation Done!");
            command="./lib/lda inf ./lib/inf-settings.txt "
                    + "./lib/output/final ./lib/wordmatrix.txt"+" ./lib/ACL";
            //normalized=new URI(command).normalize().getPath();
            System.out.println(command);
            p=Runtime.getRuntime().exec(command);
            reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
            line=reader.readLine();
            while(line!=null){
                System.out.println(line);
                line=reader.readLine();
            }
            i=p.waitFor();
            System.out.println ("waitFor = " + i );
            System.out.println("Inference Done!");
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter("./lib/"+prefix+"document2topic.txt", false);
            out = new BufferedWriter(fstream);
            Readresults myRreader=new Readresults();
            myRreader.readD2topic("./lib/output/final.gamma", filenames, out);
            if(out!=null)
                out.close();
            command="python ./lib/topics.py ./lib/output/final.beta ./lib/words.txt " +wordnum;
            System.out.println(command);
            p=Runtime.getRuntime().exec(command);
            fstream = new FileWriter("./lib/"+prefix+"topic.txt", false);
            out = new BufferedWriter(fstream);
            reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
            line=reader.readLine();
            while(line!=null)
            {
                out.write(line+"\n");
                System.out.println(line);
                line=reader.readLine();
            }
            if(out!=null)
                out.close();
            i=p.waitFor();
            System.out.println ("waitFor = " + i );
            System.out.println("Done!");
        } catch (InterruptedException ex) {
            Logger.getLogger(Topic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Topic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
