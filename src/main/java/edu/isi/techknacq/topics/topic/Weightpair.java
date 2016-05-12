package edu.isi.techknacq.topics.topic;

import edu.isi.techknacq.topics.util.Pair;

/**
 *
 * @author Linhong
 */
public class Weightpair extends Pair implements Comparable{

    public Weightpair(Object key, Object value) {
        super(key, value);
    }
    public void setindex(int indexvalue){
        setValue(indexvalue);
    }
    public void setweight(double inputvalue){
        setKey(inputvalue);
    }
    public int getindex(){
        return ((Integer)this.getValue());
    }
    public double getweight(){
        return ((Double)this.getKey());
    }
    @Override
    public int compareTo(Object o) {
         return compareTo((Weightpair)o);
    }
    public int compareTo(Weightpair o) {
        return Double.compare(o.getweight(), getweight());
    }
    
    
}
