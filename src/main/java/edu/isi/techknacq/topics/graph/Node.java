package edu.isi.techknacq.topics.graph;

public class Node {
    public int vid;
    public int []nbv;
    public double []weights;
    public int key;

    public Node() {
        key = 0;
    }

    public Node(int deg) {
        this.nbv = new int[deg];
        this.weights = new double[deg];
        key = 0;
    }

    public void setvid(int id) {
        vid = id;
    }

    public void addNeighbor(int v, double w) {
        // Check whether v is in neighbors already
        int i = 0;
        while (i < key) {
            if (nbv[i] == v)
                break;
            else
                i++;
        }
        if (i < key)
            return;
        if (key < nbv.length) {
            nbv[key] = v;
            weights[key] = w;
            key++;
        } else {
            int l = nbv.length;
            int []temp = new int[2 * l];
            System.arraycopy(nbv, 0, temp, 0, l);
            nbv = null;
            nbv = temp;
            double []tmp = new double[2 * l];
            System.arraycopy(weights, 0, tmp, 0, l);
            weights = null;
            weights = tmp;
        }
    }
}
