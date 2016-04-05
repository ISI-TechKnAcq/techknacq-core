package edu.isi.techknacq.topics.topic;

import edu.isi.techknacq.topics.util.Pair;
/*
Wordpair
key word
value: wordfrequency

*/
public class WordPair extends Pair
    implements Comparable
{
    public WordPair(String word, float value)
    {
        super(word, new Float(value));
    }

    public String getWord()
    {
        return (String)getKey();
    }

    public void setWord(String word)
    {
        setKey(word);
    }

    public float getprob()
    {
        return ((Float)getValue()).floatValue();
    }

    public void setProb(float prob)
    {
        setValue(new Float(prob));
    }

    public int compareTo(Object o)
    {
        return compareTo((WordPair)o);
    }

    private int compareTo(WordPair other)
    {
        return getWord().compareToIgnoreCase(other.getWord());
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this)
            return true;
        if (other == null)
            return false;
        if (this.getClass() != other.getClass())
            return false;
        return this.getWord().equalsIgnoreCase(((WordPair)other).getWord());
    }

    @Override
    public int hashCode()
    {
        return getWord().hashCode();
    }

    @Override
    public String toString()
    {
        String result = "[" + getWord() + ", " + getprob() + "]";
        return result;
    }
}