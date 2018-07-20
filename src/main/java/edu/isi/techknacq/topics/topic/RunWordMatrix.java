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
public class RunWordMatrix {
    public void run(String dirname, String prefix) {
        ArrayList<String> filenames = StrUtil.initFolder(dirname);
        List myfile = new ArrayList<StringPair> (filenames.size());
        for (int i = 0; i < filenames.size(); i++) {
            String name = filenames.get(i);
            String word = name.substring(name.lastIndexOf("\\") + 1,
                                         name.length() - 4);
            String year = word.substring(5,8);
            if (year.startsWith("0")) {
                year = "20" + year;
            } else
                year = "19" + year;
            System.out.println(year + word);
            StringPair o = new StringPair(year + word, filenames.get(i));
            myfile.add(o);
        }
        Collections.sort(myfile);
        ArrayList<String> posts = new ArrayList<String>(filenames.size());
        Readfile myreader = new Readfile();
        System.out.println(filenames.size());
        filenames.clear();
        for (int i = 0; i < myfile.size(); i++) {
            StringPair o = (StringPair)myfile.get(i);
            filenames.add(o.getWord().substring(4));
            String res = myreader.read(o.getname());
            posts.add(res);
            if (i % 1000 == 0)
                System.out.println(i);
        }
        System.out.println("Finish reading files");
        Wordmodel mymodel = new Wordmodel();
        mymodel.initPost(posts);
        mymodel.computeWordModel();
        mymodel.saveWordModel("./lib/wordmodel.txt");
        mymodel.saveWord("./lib/words.txt");
        mymodel.saveTopK(30, "./lib/" + prefix + "top.csv");
        String []words = mymodel.getWords();
        int[]df = mymodel.getCount();
        System.out.println("Finish computing dictionary");
        Wordmatrix mymatrix = new Wordmatrix();
        mymatrix.initWords(words);
        mymatrix.initWordFreq(df);
        mymatrix.initContent(posts);
        mymatrix.initmatrix("./lib/" + prefix + "wordmatrix.txt",
                            filenames);
        mymodel.clear();
        mymatrix.clear();
        System.out.println("Finished computing document-to-word " +
                           "representation.");
    }

    public static void main(String []args) {
        if (args.length < 1) {
            System.err.println("Usage: [foldername] [prefixname]\n");
            System.exit(2);
        }
        RunWordMatrix myrun = new RunWordMatrix();
        myrun.run(args[0], args[1]);
    }
}
