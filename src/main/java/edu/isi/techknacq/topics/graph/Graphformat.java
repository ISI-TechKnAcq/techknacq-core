/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.isi.techknacq.topics.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author linhong
 */
public class Graphformat {
    ArrayList<String> keyname;
    public Graphformat(){
        
    }
    public void Readkey(String keyfilename){
        keyname=new ArrayList<String>(200);
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(keyfilename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int index=0;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t| ");
                sc.next();
                sc.next();
                index=0;
                String name="";
                String tempword;
                while(sc.hasNext()&&index<5){
                    tempword=sc.next();
                    if((!tempword.contains(name)&&!name.contains(tempword))||name.length()<1){
                        name+=tempword;
                        name+="-";
                        index++;
                    }
                }
                this.keyname.add(name);
            }
            in1.close();
        } catch (IOException ex) {
            Logger.getLogger(Graphformat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Readmatrix(String matrixfilename, String outfilename){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(matrixfilename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int lineindex;
            int columnindex;
            int weight;
            BufferedWriter out = null;
            FileWriter fstream = null;
            fstream = new FileWriter(outfilename, false);
            out = new BufferedWriter(fstream);
            out.write("*Vertices "+keyname.size()+"\n");
            for(int i=0;i<keyname.size();i++){
                out.write(i+1+" \""+keyname.get(i)+"\"\n");
            }
            out.write("*Edges ");
            int edgenum=0;
            lineindex=1;
            while((strline=br.readLine())!=null){
                 Scanner sc=new Scanner(strline);
                 sc.useDelimiter("\t| ");
                 columnindex=1;
                 while(sc.hasNext()){
                     weight=sc.nextInt();
                     if(weight>0&&columnindex>lineindex){
                         edgenum++;
                     }
                     columnindex++;
                 }
                 lineindex++;
            }
            out.write(edgenum+"\n");
            in1.close();
            
            //reopen the file to read
            fstream1 = new FileInputStream(matrixfilename);
            // Get the object of DataInputStream
            in1 = new DataInputStream(fstream1);
            br = new BufferedReader(new InputStreamReader(in1));
            lineindex=1;
            while((strline=br.readLine())!=null){
                 Scanner sc=new Scanner(strline);
                 sc.useDelimiter("\t| ");
                 columnindex=1;
                 while(sc.hasNext()){
                     weight=sc.nextInt();
                     if(weight>0&&columnindex>lineindex){
                         out.write(lineindex+" "+columnindex+" "+weight+"\n");
                     }
                     columnindex++;
                 }
                 lineindex++;
            }
            in1.close();
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Graphformat.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Graphformat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String []args){
        if(args.length<1){
            System.out.println("Usage: [keyfilename] [matrixfilename] [outputfilename]");
            System.exit(2);
        }
        Graphformat mygraph=new Graphformat();
        //args[0]:keyfilename
        //args[1]: co-occurrence matrix file name
        //args[2]:output file name
        mygraph.Readkey(args[0]);
        mygraph.Readmatrix(args[1],args[2]);
       // Graphformat mygraph=new Graphformat();
        //mygraph.Readkey("C:\\Users\\linhong\\Documents\\linhong-work\\Industry_project\\TechKnacq\\mallet-keys.txt");
        //mygraph.Readmatrix("C:\\Users\\linhong\\Documents\\linhong-work\\Industry_project\\TechKnacq\\co-occurrence.txt", "C:\\Users\\linhong\\Documents\\linhong-work\\Industry_project\\TechKnacq\\Topicmallet12.net");
    }
}
