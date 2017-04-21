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

public class BaselineReadingListJson {
    private Map<String, Double> paperpagerank;
    private List<String> topickeys;
    private Logger logger =
        Logger.getLogger(BaselineReadingListJson.class.getName());

    public void readPageRankScore(String filename) {
        this.paperpagerank = new HashMap<String,Double>(this.topickeys.size());
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            br.readLine(); // Skip node vertices line
            br.readLine(); // Skip column name line
            String keyname;
            double value;
            String sr;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter("\t| ");
                sr = sc.next();
                if (sr.contains("*Edge") || sr.contains("*Arc"))
                    break;
                keyname = sc.next();
                keyname = keyname.substring(1, keyname.length()-1);
                value = sc.nextDouble();
                if (!this.paperpagerank.containsKey(keyname)) {
                    this.paperpagerank.put(keyname, value);
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public String run(String keyname, int K, String docfile,
                      String pagerankfile, String keyword,
                      String doc2conceptfilename) {
        try {
            Keyword2concept match1 = new Keyword2concept();
            match1.readKey(keyname);
            ArrayList<Integer> hittopic = match1.getMatch(keyword);
            this.topickeys = match1.getTopics();
            Concept2doc doc = new Concept2doc();
            doc.initNum(this.topickeys.size());
            doc.getTopK(K*4, doc2conceptfilename);
            // doc.prune();
            ArrayList<String> docnames = doc.getDocName();
            List mylist = new ArrayList<Weightpair>(100);
            double value;
            boolean []isvisit = new boolean[docnames.size()];
            for (int i = 0; i < isvisit.length; i++) {
                isvisit[i] = false;
            }
            this.readPageRankScore(pagerankfile);
            for (Integer hittopic1 : hittopic) {
                int tindex = hittopic1;
                ArrayList<Integer> mydocs = doc.getDocs(tindex);
                for (Integer mydoc : mydocs) {
                    int Did = mydoc;
                    if (!isvisit[Did])
                        isvisit[Did] = true;
                    else
                        continue;
                    String dockey = docnames.get(Did);
                    if (this.paperpagerank.containsKey(dockey))
                        value = this.paperpagerank.get(dockey);
                    else
                        value = -1;
                    if (value > -1)
                        mylist.add(new Weightpair(value,Did));
                }
            }
            StringWriter writer = new StringWriter();
            JsonWriter s = new JsonWriter(writer);
            s.beginObject();
            s.name("keyword");
            s.value(keyword);
            Collections.sort(mylist);
            ReadDocumentkey rdk = new ReadDocumentkey(docfile);
            rdk.readFile();
            s.name("documents");
            s.beginArray();
            for (int i = 0; i < K; i++) {
                Weightpair o = (Weightpair)mylist.get(i);
                int Did = o.getindex();
                String id = docnames.get(Did);
                s.value("id " + id + ", weight: " + o.getweight());
            }
            s.endArray();
            s.endObject();
            return writer.toString();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main(String []args) {
        if (args.length < 1) {
            System.err.println("Usage: [keyword][k] [topic-key-file] " +
                               "[doc-topic-composition] [document-meta-file] " +
                               "[page-rank-score]");
            System.exit(2);
        }
        BaselineReadingListJson myreader = new BaselineReadingListJson();
        String s = myreader.run(args[2], Integer.parseInt(args[1]), args[4],
                                args[5], args[0], args[3]);
        System.out.println(s);
        // String keyname, int K, String docfile, String pagerankfile,
        // String keyword, String doc2conceptfilename
        // String s = myreader.run("mallet-weighted-key.txt", 10,
        //                         "acl-meta.json", "Paperpagerank.txt",
        //                         "machine_learning", "concept2doc.txt");
        // System.out.println(s);
        // myreader.run("mallet-21185-weightedkey.txt", 10, "acl-meta.json",
        //              "Paperpagerank.txt", args[0], "mallet-comp.txt");
    }
}
