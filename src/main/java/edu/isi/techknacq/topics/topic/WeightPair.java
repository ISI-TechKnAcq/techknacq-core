package edu.isi.techknacq.topics.topic;

import edu.isi.techknacq.topics.util.Pair;

/**
 *
 * @author Linhong
 */
public class WeightPair extends Pair implements Comparable {
    public WeightPair(Object key, Object value) {
        super(key, value);
    }

    public void setindex(int indexvalue) {
        setValue(indexvalue);
    }

    public void setweight(double inputvalue) {
        setKey(inputvalue);
    }

    public int getindex() {
        return ((Integer)this.getValue());
    }

    public double getweight() {
        return ((Double)this.getKey());
    }

    @Override
    public int compareTo(Object o) {
         return compareTo((WeightPair)o);
    }

    public int compareTo(WeightPair o) {
        return Double.compare(o.getweight(), getweight());
    }
}
