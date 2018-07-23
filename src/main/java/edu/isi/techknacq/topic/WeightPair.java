package edu.isi.techknacq.topic;

import edu.isi.techknacq.util.Pair;

/**
 *
 * @author Linhong
 */
public class WeightPair extends Pair implements Comparable {
    public WeightPair(Object key, Object value) {
        super(key, value);
    }

    public void setIndex(int indexvalue) {
        setValue(indexvalue);
    }

    public void setWeight(double inputvalue) {
        setKey(inputvalue);
    }

    public int getIndex() {
        return ((Integer)this.getValue());
    }

    public double getWeight() {
        return ((Double)this.getKey());
    }

    @Override
    public int compareTo(Object o) {
         return compareTo((WeightPair)o);
    }

    public int compareTo(WeightPair o) {
        return Double.compare(o.getWeight(), getWeight());
    }
}
