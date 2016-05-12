/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.isi.techknacq.topics.topic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.isi.techknacq.topics.util.Readfile;
import edu.isi.techknacq.topics.util.StrUtil;

/**
 *
 * @author linhong
 */
public class Runwordmatrix {
    public Runwordmatrix(){
        
    }
    public void Run(String dirname, String prefix){
            ArrayList<String> filenames=StrUtil.Initfolder(dirname);
            List myfile=new ArrayList<StringPair> (filenames.size());
            for(int i=0;i<filenames.size();i++){
                String name=filenames.get(i);
                String word=name.substring(name.lastIndexOf("\\")+1,name.length()-4);
                String year=word.substring(5,8);
                if(year.startsWith("0")==true){
                    year="20"+year;
                }else
                    year="19"+year;
                System.out.println(year+word);
                StringPair o=new StringPair(year+word, filenames.get(i));
                myfile.add(o);
            }
            Collections.sort(myfile);
            ArrayList<String> posts=new ArrayList<String>(filenames.size());
            Readfile myreader=new Readfile();
            System.out.println(filenames.size());
            filenames.clear();
            for(int i=0;i<myfile.size();i++){
                StringPair o=(StringPair)myfile.get(i);
                filenames.add(o.getWord().substring(4));
                String res=myreader.Readfile(o.getname());
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
            mymatrix.initmatrix("./lib/"+prefix+"wordmatrix.txt",filenames);
            mymodel.clear();
            mymatrix.clear();
            System.out.println("finish document to word representation computation");
    }
    public static void main(String []args){
        if(args.length<1){
            System.err.println("Usage: [foldername] [prefixname]\n");
            System.exit(2);
        }
        Runwordmatrix myrun=new Runwordmatrix();
        myrun.Run(args[0], args[1]);
    }
}