package edu.isi.techknacq.topics.graph;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.graph.Node;
import edu.isi.techknacq.topics.topic.Weightpair;

/**
 *
 * @author Linhong
 */
public class ReadGraph {
    Node []G;
  public ReadGraph(String filename){
        try {
            FileInputStream fstream1 = null;
            fstream1 = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in1 = new DataInputStream(fstream1);
            BufferedReader br = new BufferedReader(new InputStreamReader(in1));
            String strline;
            int index;
            int nodenum=Integer.parseInt(br.readLine());
            G=new Node[nodenum];
            while((strline=br.readLine())!=null){
                Scanner sc=new Scanner(strline);
                sc.useDelimiter(":");
                if(sc.hasNext()){
                    String vstr=sc.next();
                    index=vstr.indexOf(",");
                    int v=Integer.parseInt(vstr.substring(0,index));
                    int d=Integer.parseInt(vstr.substring(index+1,vstr.length()));
                    if(d>0){
                        G[v]=new Node(d);
                        G[v].setvid(v);
                         while(sc.hasNext()){
                             String neighstr=sc.next();
                             index=neighstr.indexOf(",");
                             int neigh=Integer.parseInt(neighstr.substring(0,index));
                             double weight=Double.parseDouble(neighstr.substring(index+1,neighstr.length()));
                             G[v].Addneighbore(neigh, weight);
                        }
                    }else{
                        G[v]=new Node();
                        G[v].setvid(v);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadGraph.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
  }
  public Node []Getgraph(){
      return G;
  }
  public int [] Ordernode(){
      List myorderlist=new ArrayList<Weightpair>(this.G.length);
      for(int i=0;i<G.length;i++){
          double wdeg=0;
           for(int j=0;j<G[i].key;j++){
               wdeg+=G[i].weights[j];
           }
           myorderlist.add(new Weightpair(wdeg,i));
      }
      Collections.sort(myorderlist);
      int [] topicorder=new int[this.G.length];
      for(int i=0;i<myorderlist.size();i++){
          Weightpair o=(Weightpair)myorderlist.get(i);
          topicorder[i]=o.getindex();
      }
      return topicorder;
  }
  public void Printgraph(){
      for(int i=0;i<G.length;i++){
          System.out.print(G[i].vid+",");
          System.out.print(G[i].key);
          for(int j=0;j<G[i].key;j++){
              System.out.print(":"+G[i].nbv[j]+","+G[i].weights[j]);
          }
          System.out.println();
      }
  }
  public static void main(String []args){
      ReadGraph myreader=new ReadGraph("concept-16696-IF.txt");
      myreader.Printgraph();
  }
}
