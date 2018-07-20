package edu.isi.techknacq.topics.topic;

import edu.isi.techknacq.topics.util.Pair;

/**
 *
 * @author linhong
 * @email linhong.seba.zhu@gmail.com
 * @since April/19/2012
 */
public class IndexPair extends Pair implements Comparable {
    public IndexPair(int index, double value) {
         super(index,value);
    }

    public void setIndex(int indexvalue) {
        setKey(indexvalue);
    }

    public void setValue(double inputvalue) {
        setValue(inputvalue);
    }

    public int getIndex() {
        return ((Integer)this.getKey()).intValue();
    }

    public double getWeight() {
        return ((Double)this.getValue()).doubleValue();
    }

    public int compareTo(Object o) {
         return compareTo((IndexPair)o);
    }

    public int compareTo(IndexPair o) {
        if (getIndex() == o.getIndex())
            return 0;
        else
            if (getIndex() > o.getIndex())
                return 1;
            else
                return -1;
    }
}
