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
public class Generatematrixjsonfile {
    ArrayList<String> User_id;
    ArrayList<String> group;
    public Generatematrixjsonfile(){
        User_id=new ArrayList<String> (8000);
        group=new ArrayList<String> (8000);
    }
    public void ReadNodefile(String filename){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            //br.readLine();
            String id;
            String attr;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t");
                sc.next();
                id=sc.next();
                attr=sc.next();
                User_id.add(id);
                group.add(attr);
            }
            in1.close();
        } catch (IOException ex) {
            Logger.getLogger(Generatematrixjsonfile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Readedges(String filename, String output){
        try {
            FileWriter fstream1 = new FileWriter(output,false);
            BufferedWriter out1=new BufferedWriter(fstream1);
            out1.write("{\"directed\": false, \"graph\": [],");
            out1.write("\t\"nodes\":[\n");
            for(int i=0;i<User_id.size();i++){
                
                out1.write("\t\t{");
                out1.write("\"group\":");
                out1.write(group.get(i)+",");
                out1.write("\"name\":");
                out1.write("\""+User_id.get(i)+"\"");
                out1.write("}");
                if(i<this.User_id.size()-1)
                    out1.write(",\n");
                else
                    out1.write("\n],\n");
            }
            out1.write("\t\"links\":[\n");
            FileInputStream fstream = null;
            fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter("\t");
                String i=sc.next();
                String j=sc.next();
                String value=sc.next();
                out1.write("\t\t{");
                        out1.write("\"source\":");
                        out1.write(i+",");
                        out1.write("\"target\":");
                        out1.write(j+",");
                        out1.write("\"value\":");
                        out1.write(value);
                        out1.write("},\n");
            }
            out1.write("\t]\n}");
            out1.close();
        } catch (IOException ex) {
            Logger.getLogger(Generatematrixjsonfile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String []args){
        Generatematrixjsonfile myrun=new Generatematrixjsonfile();
        myrun.ReadNodefile(args[0]); //keyword file for nodes
        myrun.Readedges(args[1], args[2]); //args[1]: edge file, args[2]: outputfile
    }
}