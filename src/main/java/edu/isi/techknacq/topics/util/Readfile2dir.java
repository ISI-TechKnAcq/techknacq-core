package edu.isi.techknacq.topics.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author linhong
 */
public class Readfile2dir {
    public Readfile2dir(){
        
    }
    public void Parsetopicvector(String filename, int k){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            String id;
            String file;
            int label;
            double va;
            FileWriter fstream = new FileWriter(filename+"_new.txt", false);
            BufferedWriter out = new BufferedWriter(fstream);
            double []temp=new double[k];
            br.readLine();
             while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t");
                id=sc.next();
                file=sc.next();
                while(sc.hasNext()){
                    label=sc.nextInt();
                    va=sc.nextDouble();
                    temp[label]=va;
                }
                for(int i=0;i<temp.length;i++){
                    if(i==temp.length-1)
                    out.write(temp[i]+"\n");
                    else
                        out.write(temp[i]+"\t");
                }
            }
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Readfile2dir.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Readfile2dir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Readfile(String filename, String dirname){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int id;
            String year;
            String content;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter(",");
                id=sc.nextInt();
                year=sc.next();
                sc.nextInt();
                content=sc.next();
                if(year.compareTo("2004")<=0){
                    FileWriter fstream = new FileWriter(dirname+"//"+id+".txt", false);
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(content+"\n");
                    out.close();
                }
            }
            in1.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Readfile2dir.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Readfile2dir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String []args){
        if(args.length<1){
            System.out.println("Usage: [(function type)Integer 1/2] [arg1] [arg2]");
            System.out.println("if function type==1, arg1=topic composition file (string) arg2=k (integer) top k words in each topic");
            System.out.println("if type==2, arg1=raw text file name (string), arg2=prefix for output file");
            System.exit(2);
        }
        Readfile2dir myreader=new Readfile2dir();
        int type=Integer.parseInt(args[0]);
        if(type==1){
            myreader.Parsetopicvector(args[1], Integer.parseInt(args[2]));
            //example:
            //myreader.Parsetopicvector("C:\\Users\\linhong\\Documents\\linhong-work\\Data\\mallet-2.0.8RC2\\doc2topic.txt", 20);
        }else{
            myreader.Readfile(args[1], args[2]);
            //example:
            //myreader.Readfile("C:\\Users\\linhong\\Documents\\linhong-work\\Data\\preprocessed_text.csv", "C:\\Users\\linhong\\Documents\\linhong-work\\Data\\abstract2004");
        }
        
        
    }
}
