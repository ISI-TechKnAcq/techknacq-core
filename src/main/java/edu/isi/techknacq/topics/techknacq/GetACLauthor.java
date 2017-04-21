package edu.isi.techknacq.topics.techknacq;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.util.StrUtil;

/**
 *
 * @author linhong
 */
public class GetACLauthor {
    private HashMap<String, Integer> authorname;
    private static Logger logger =
        Logger.getLogger(GetACLauthor.class.getName());

    public void readfile(String filename) throws IOException {
        authorname = new HashMap<String,Integer>(10000);
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String conf;
            String author;
            while ((strline = br.readLine()) != null) {
                Scanner sc = new Scanner(strline);
                sc.useDelimiter(":");
                conf = sc.next();
                author = sc.next();
                if (conf.indexOf("conf/acl") >= 0 ||
                    conf.indexOf("conf/emnlp") >= 0 ||
                    conf.indexOf("conf/naacl") >= 0 ||
                    conf.indexOf("conf/eacl") >= 0 ||
                    conf.indexOf("journals/coling") >= 0) {
                    if (this.authorname.containsKey(author)) {
                        int count = authorname.get(author);
                        this.authorname.put(author, count + 1);
                    } else {
                        this.authorname.put(author, 1);
                    }
                }
            }
            FileOutputStream fos = new FileOutputStream("ACLauthor.txt");
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter out = new BufferedWriter(osw);
            StrUtil.printMap(authorname, out);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String []args) {
        try {
            GetACLauthor myauthor = new GetACLauthor();
            myauthor.readfile("confauthor");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
