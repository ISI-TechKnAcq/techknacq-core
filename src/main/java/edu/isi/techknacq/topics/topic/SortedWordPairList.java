package edu.isi.techknacq.topics.topic;

/*
 * SortedWordPairList
 * Computer an ordered dictionary
 * and the document frequency of each word in the dictionary
 * support a list of functions towards SortedWordPairList
 * Stop word removal
 * prun average
 * merge similar word
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SortedWordPairList
{
    private List wordPairs;
    private WordPair nullWordPair;

    private static final String NULL_WORD = null;
    private static final int NULL_COUNT = -1;
    private static final int DEFAULT_INIT_CAPACITY = 10;
    public static final String[] ENGLISH_STOP_WORDS = {
        "##", "%","+", "\\*", "a", "aa", "ab", "about", "above", "ac",
        "across", "act", "after", "again", "against", "al", "all", "allah",
        "almost", "alone", "along", "already", "also", "although", "always",
        "amazon", "amazon.com", "among", "an", "and", "anissaestk", "another",
        "any", "anybody", "anyone", "anything", "anywhere", "are", "area",
        "areas", "around", "as", "ask", "asked", "asking", "asks", "at",
        "august", "away", "b", "b b", "b c", "b s", "b x", "back", "backed",
        "be", "became", "because", "become", "been", "before", "began",
        "behind", "being", "beings", "between", "bit", "both", "boy", "brief",
        "bro", "btw", "but", "bw", "by", "c", "c b", "came", "can", "cannot",
        "come", "comment", "comments", "computer", "con", "cont", "cool",
        "could", "d", "day", "detail", "dfpconf", "did", "differ", "do",
        "does", "done", "dont", "down", "downed", "downing", "downs", "Dr.",
        "during", "e", "each", "early", "eg", "either", "end", "ended",
        "ending", "ends", "et", "even", "evenly", "ever", "every",
        "everybody", "everyone", "everything", "everywhere", "example",
        "examples", "f", "fact", "family", "far", "favorite", "felt", "few",
        "film", "films", "find", "finds", "first", "following", "for", "four",
        "friend", "from", "full", "fully", "fun", "further", "g", "gave",
        "general", "generally", "get", "gets", "girl", "girls", "give",
        "given", "gives", "go", "god", "going", "gonna", "goods", "got",
        "guy", "h", "ha", "had", "haha", "hahaha", "has", "have", "having",
        "he", "heart", "hehe", "her", "here", "herself", "hey", "hi", "him",
        "himself", "his", "home", "hope", "hour", "hours", "house", "how",
        "however", "http", "hug", "i", "idea", "ie", "if", "im", "in", "into",
        "is", "issue", "it", "item", "its", "itself", "j", "jasondchen",
        "jay", "job", "just", "k", "kakaazraff", "keep", "keeps", "kid",
        "kind", "knew", "know", "known", "knows", "l", "la", "last", "later",
        "latest", "least", "less", "let", "lets", "life", "like", "likely",
        "live", "lol", "lot", "luck", "m", "made", "make", "making",
        "man", "may", "me", "men", "might", "mine", "minute", "month", "more",
        "morning", "most", "mostly", "mother", "movie", "movies", "mr", "mrs",
        "much", "must", "my", "myself", "n", "name", "need", "never", "new",
        "news", "next", "night", "no", "NO.", "nobody", "non", "noon", "not",
        "note", "nothing", "now", "nowhere", "number", "numbers", "o", "of",
        "off", "often", "ok", "old", "omg", "on", "once", "one", "only",
        "open", "opened", "opening", "opens", "or", "other", "others", "our",
        "out", "over", "p", "parent", "part", "parted", "parting", "parts",
        "past", "people", "per", "perhaps", "person", "piece", "pl",
        "place", "places", "please", "pls", "plz", "point", "pointed",
        "pointing", "points", "possible", "ppl", "present", "presented",
        "presenting", "presents", "problem", "problems", "product",
        "products", "put", "puts", "q", "questions", "quite", "r", "rather",
        "really", "reason", "review", "reviews", "right", "room", "rooms",
        "rt", "said", "same", "saturday", "saw", "say", "says", "second",
        "seconds", "see", "seem", "seemed", "seeming", "seems", "sees",
        "self", "selve", "several", "sg", "shall", "share", "she", "shit",
        "shock", "should", "show", "showed", "showing", "shows", "side",
        "sides", "since", "so", "some", "somebody", "someone", "something",
        "somewhere", "state", "states", "still", "story", "such", "sure",
        "t", "take", "taken", "te", "than", "thank", "that", "the", "their",
        "them", "then", "there", "therefore", "these", "they", "thing",
        "things", "think", "thinks", "this", "those", "though", "thought",
        "thoughts", "three", "through", "thus", "tiffanyalvord", "time",
        "tmr", "to", "today", "together", "tomorrow", "tonight", "too",
        "took", "toward", "turn", "turned", "turning", "turns", "tweet",
        "twitter", "two", "u", "under", "until", "up", "upon", "ur", "us",
        "use", "used", "uses", "v", "very", "w", "wanna", "want", "wanted",
        "wanting", "wants", "was", "way", "ways", "we", "week", "weeks",
        "well", "wells", "went", "were", "what", "when", "where", "whether",
        "which", "while", "who", "whole", "whose", "why", "wife", "will",
        "with", "within", "without", "women", "word", "work", "worked",
        "working", "works", "world", "worlds", "would", "wow", "x", "xd",
        "y", "yeah", "year", "years", "yes", "yesterday", "yet", "you",
        "young", "younger", "youngest", "your", "yours", "z", "zhai",
        "zhang", "zhao", "zhou", "zhu", "zizan"};

    private Logger logger =
        Logger.getLogger(SortedWordPairList.class.getName());

    public SortedWordPairList(int initialCapacity)
    {
        wordPairs = new ArrayList(initialCapacity);
        nullWordPair = new WordPair(NULL_WORD, NULL_COUNT);
    }

    public SortedWordPairList()
    {
        this(DEFAULT_INIT_CAPACITY);
    }

    /*
     * Add a word with frequency count into worted word list
     */
    public void add(String word, int count)
    {
        nullWordPair.setWord(word);
        int index = Collections.binarySearch(wordPairs, nullWordPair);
        if (index >= 0) {
            WordPair pair = (WordPair)wordPairs.get(index);
            pair.setProb(pair.getprob() + (float)count);
        } else {
            wordPairs.add(-index - 1, new WordPair(word, count));
        }
    }

    // Add a word into the sorted word list
    public void add(String word)
    {
        add(word, 1);
    }

    // Remove a word into the sorted word list
    public void remove(String word)
    {
        nullWordPair.setWord(word);
        int index = Collections.binarySearch(wordPairs, nullWordPair);
        if (index >= 0) {
            wordPairs.remove(index);
        }
    }

    /*
     * Return a dictionary wor word in the sorted word list
     */
    public String[] getWords()
    {
        String[] result = new String[wordPairs.size()];
        for (int i = 0; i < wordPairs.size(); ++i) {
            result[i] = ((WordPair) wordPairs.get(i)).getWord();
        }
        return result;
    }

    /*
     * Return the word count
     */
    public int []getCount() {
        int[] result = new int[wordPairs.size()];
        for (int i = 0; i < wordPairs.size(); ++i) {
            result[i] = (int)((WordPair)wordPairs.get(i)).getprob();
        }
        return result;
    }

    /*
     * Update the count
     */
    public void updatecount(int [] wf) {
        for (int i = 0; i < wordPairs.size(); ++i) {
            ((WordPair)wordPairs.get(i)).setProb(wf[i]);
        }
    }

    /*
     * Update the words
     */
    public void updatecontent(String []wordlists) {
        for (int i = 0; i < wordPairs.size(); ++i)
            ((WordPair)wordPairs.get(i)).setWord(wordlists[i]);
    }

    public void sort() {
        Collections.sort(wordPairs);
    }

    public void enumeration() {
        for (int i = 0; i < wordPairs.size(); i++) {
            System.out.println(((WordPair) wordPairs.get(i)).getWord() + " " +
                               ((WordPair) wordPairs.get(i)).getprob());
        }
    }

    public void print(BufferedWriter out) {
        try {
            for (int i = 0; i < wordPairs.size(); i++) {
                double v = ((WordPair) wordPairs.get(i)).getprob();
                out.write(((WordPair) wordPairs.get(i)).getWord() + "\t" + v +
                          "\n");
            }
            out.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void printWords(BufferedWriter out) {
        try {
            for (int i = 0; i < wordPairs.size(); i++) {
                out.write(((WordPair)wordPairs.get(i)).getWord() + "\n");
            }
            out.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public int getCountOfWord(String word)
    {
        nullWordPair.setWord(word);
        int index = Collections.binarySearch(wordPairs, nullWordPair);
        if (index >= 0) {
            return (int)((WordPair) wordPairs.get(index)).getprob();
        } else {
            return 0;
        }
    }

    public int getTotalCount()
    {
        int result = 0;
        for (int i = 0; i < wordPairs.size(); ++i) {
            result += ((WordPair) wordPairs.get(i)).getprob();
        }
        return result;
    }

    public void clear()
    {
        nullWordPair.setWord(NULL_WORD);
        wordPairs.clear();
    }

    public int indexOf(String word)
    {
        nullWordPair.setWord(word);
        int index = Collections.binarySearch(wordPairs, nullWordPair);
        return index >= 0 ? index : -1;
    }

    public boolean contains(String word)
    {
        return indexOf(word) >= 0;
    }

    // Prune count
    public void prune(int thre) {
        int i = 0;
        while (i < this.wordPairs.size()) {
            WordPair pair1 = (WordPair)wordPairs.get(i);
            if ((int)pair1.getprob() <= thre) {
                wordPairs.remove(i);
            } else
                i++;
        }
    }

    // Prune average
    public void pruneAverage() {
        float avg = 0;
        for (int i = 0; i < this.wordPairs.size(); i++)
            avg += ((WordPair)this.wordPairs.get(i)).getprob();
        avg /= this.wordPairs.size();
        this.prune((int)avg);
    }

    public String[] getTop(int k) {
        float [] result = new float[wordPairs.size()];
        int i = 0;
        for (i = 0; i < wordPairs.size(); ++i) {
            result[i] = ((WordPair) wordPairs.get(i)).getprob();
        }
        Arrays.sort(result);
        float threshold = 1;
        if (result.length >= k)
            threshold = result[result.length - k];
        else
            threshold = result[0];
        ArrayList<String> words = new ArrayList<String>(k);
        int counter = 0;
        for (i = 0; i < this.wordPairs.size(); i++) {
            WordPair pair1 = (WordPair)wordPairs.get(i);
            if (pair1.getprob() > threshold) {
                words.add(pair1.getWord());
                counter++;
            }
        }
        for (i = 0; i < this.wordPairs.size(); i++) {
            WordPair pair1 = (WordPair)wordPairs.get(i);
            if (pair1.getprob() == threshold) {
                words.add(pair1.getWord());
                counter++;
                if (counter == k)
                    break;
            }
        }
        String []nuns = (String [])words.toArray(new String[0]);
        Arrays.sort(nuns);
        return nuns;
    }

    public void removeStopWord() {
        int i = 0;
        int j = 0;
        String word;
        int val;
        while (i < ENGLISH_STOP_WORDS.length &&
               j < this.wordPairs.size()) {
            word = ((WordPair)this.wordPairs.get(j)).getWord();
            val = ENGLISH_STOP_WORDS[i].compareToIgnoreCase(word);
            if (val > 0) {
                j++;
            } else if (val < 0) {
                i++;
            } else {
                i++;
                this.wordPairs.remove(j);
            }
        }
    }

    public void mergeSimilarWords() {
        int i = 1;
        float count = 0;
        while (i < this.wordPairs.size()) {
            WordPair pair1 = (WordPair)wordPairs.get(i);
            WordPair pair2 = (WordPair)wordPairs.get(i - 1);
            String word = pair1.getWord();
            String preword = pair2.getWord();
            if (word.length() <= 1) {
                i++;
                continue;
            }
            String word1 = word.substring(0, word.length() - 1);
            // System.out.println(word + " " + word1);
            if (word1.equalsIgnoreCase(preword)) {
                count = pair1.getprob();
                pair2.setProb(count + pair2.getprob());
                wordPairs.remove(i);
            } else
                i++;
        }
    }

    public int getCountofWord() {
        return this.wordPairs.size();
    }

    public void removeproduct(String productname) {
        int i = 0;
        while (i < this.wordPairs.size()) {
            String attri = ((WordPair)wordPairs.get(i)).getWord();
            String attri2 = attri.toLowerCase();
            String pcopy = productname.toLowerCase();
            if (pcopy.indexOf(attri2) != -1) {
                wordPairs.remove(i);
            } else
                i++;
        }
    }
}
