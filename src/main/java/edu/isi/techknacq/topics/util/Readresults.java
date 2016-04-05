/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.isi.techknacq.topics.util;

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
public class Readresults {
    public Readresults(){
        
    }
    public void ReadD2topic(String filename, ArrayList<String> filenames, BufferedWriter out){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int linenum=0;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                out.write(filenames.get(linenum));
                int index=0;
                while(sc.hasNext()){
                    out.write("\ttopic"+index+":"+sc.next());
                    index++;
                }
                out.write("\n");
                linenum++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Readresults.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Readresults.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void topicword(String filename){
        FileWriter fstream = null;
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int tnum=0;
            BufferedWriter out = null;
            while((strline=br.readLine())!=null){
                if(strline.startsWith("topic")==true){
                    tnum++;
                    fstream = new FileWriter(filename.substring(0,filename.length()-4)+tnum+".csv", false);
                    out = new BufferedWriter(fstream);
                    out.write("\"name\",\"count\"\n");
                    continue;
                }else
                    if(strline.length()<1){
                        if(out!=null)
                            out.close();
                }else{
                        out.write(strline+"\n");
                    }
            }
            if(out!=null)
                out.close();  
        } catch (IOException ex) {
            Logger.getLogger(Readresults.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(Readresults.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public static void main(String []args){
        Readresults myreader=new Readresults();
        myreader.topicword("C:\\Users\\linhong\\Documents\\linhong-work\\Coding\\NetBeansProjects\\TechKnacq\\lib\\techtopic.txt");
    }
}
