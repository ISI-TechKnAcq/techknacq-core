package edu.isi.techknacq.topics.graph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.stream.JsonWriter;

import edu.isi.techknacq.topics.topic.Weightpair;


public class Conceptdepth {
    private Node []G;
    private char []isvisit;
    private List<String> topics;
    private Logger logger = Logger.getLogger(Conceptdepth.class.getName());

    public void initTopics(List<String> inputtopics) {
        topics = inputtopics;
    }

    public void initGraph(Node []inputG) {
        G = inputG;
        isvisit = new char[G.length];
    }

    public void resetVisit() {
        for (int i = 0; i < isvisit.length; i++) {
            isvisit[i]='w';
        }
    }

    public void BFS(ArrayList<Integer> res, int v, int m) {
        PriorityQueue<Weightpair> queue2 =
            new PriorityQueue<Weightpair>();
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(v); // Adds to end of queue
        while (!queue.isEmpty()) {
            if (res.size() >= m)
                break;
            // Removes from front of queue
            int u = queue.remove();
            isvisit[u] = 'g';
            queue2.clear();
            for (int i = 0; i < G[u].key; i++) {
                int w = G[u].nbv[i];
                double weight = G[u].weights[i];
                if (isvisit[w] != 'g') {
                    queue2.add(new Weightpair(weight, w));
                }
            }
            while (!queue2.isEmpty()) {
                Weightpair e = queue2.remove();
                queue.add(e.getindex());
                res.add(e.getindex());
                isvisit[e.getindex()] = 'g';
                System.out.println(u + "\t" + e.getindex() +
                                   "\t" + e.getweight());
                if (res.size() >= m)
                    break;
            }
        }
    }

    public ArrayList<Integer> getTopNode(int m, int v) {
        //Get top m dependent topics;
        ArrayList<Integer> relatedtopic = new ArrayList<Integer>(m);
        BFS(relatedtopic, v, m);
        return relatedtopic;
    }

    public String getSubgraphInString(String keyword) throws IOException {
        StringWriter writer = new StringWriter();
        JsonWriter s = new JsonWriter(writer);
        s.beginObject();
        s.name("keyword");
        s.value(keyword);
        int []mapindex = new int[topics.size()];
        for (int i = 0; i < topics.size(); i++) {
            mapindex[i] = -1;
        }

        int subcount = 0;
        for (int i = 0; i < G.length; i++) {
            if (isvisit[i] == 'g') {
                mapindex[i]=subcount;
                subcount++;
            }
        }
        s.name("#nodes");
        s.value(subcount);
        int subedge = 0;
        for (int i = 0; i < G.length; i++) {
            if (isvisit[i] == 'g') {
                for (int j = 0; j < G[i].key; j++) {
                    int nb = G[i].nbv[j];
                    if (isvisit[nb]=='g')
                        subedge++;
                }
            }
        }
        s.name("#edges");
        s.value(subedge);
        s.name("topic pairs");
        s.beginArray();
        for (int i = 0; i < G.length; i++) {
            if (isvisit[i]=='g') {
                for (int j = 0; j < G[i].key; j++) {
                    int nb = G[i].nbv[j];
                    if (isvisit[nb] == 'g') {
                        s.value(i+"-->"+nb+" weight:"+G[i].weights[j]);
                    }
                }
            }
        }
        s.endArray();
        s.endObject();
        return writer.toString();
    }

    public void getSubgraph(String keyword) {
        FileWriter fstream = null;
        try {
            fstream = new FileWriter(keyword+"_graph.net",false);
            BufferedWriter out = new BufferedWriter(fstream);
            int []mapindex = new int[topics.size()];
            for (int i = 0; i < topics.size(); i++) {
                mapindex[i] = -1;
            }
            int subcount = 0;
            for (int i = 0; i < G.length ;i++) {
                if (isvisit[i] == 'g') {
                    mapindex[i] = subcount;
                    subcount++;
                }
            }
            out.write("*Vertices " + subcount + "\n");
            int subedge = 0;
            for (int i = 0; i < G.length; i++) {
                if (isvisit[i] == 'g') {
                    out.write((mapindex[i] + 1) +
                              " \"" + topics.get(i) + "\"\n");
                    for (int j = 0; j < G[i].key; j++) {
                        int nb = G[i].nbv[j];
                        if (isvisit[nb] == 'g')
                            subedge++;
                    }
                }
            }
            out.write("*Arcs "+subedge+"\n");
            for (int i = 0; i < G.length; i++) {
                if (isvisit[i] == 'g') {
                    for (int j = 0; j < G[i].key; j++) {
                        int nb = G[i].nbv[j];
                        if (isvisit[nb] == 'g') {
                            out.write((mapindex[i] + 1) + " " +
                                      (mapindex[nb] + 1) + "\n");
                        }
                    }
                }
            }
            out.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }
}
