package edu.isi.techknacq.topics.readinglist;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.util.StrUtil;

/**
 *
 * @author linhong
 */
public class Readability {
    // syllables = used to store dictionary of words and syllable counts
    private Map<String, Integer> syllables = new HashMap<String, Integer>();						// are flabby.
    public Readability(){
        
    }
    public double FKscore(String filename){
        try {
            double score=0;
            File file = new File(filename);
            Scanner sc=new Scanner(file);
            int sentencenum=0;
            int wordCount=0;
            int syllableCount=0;
            while(sc.hasNext()){
                String sentence=sc.nextLine();//sentence.
                if(sentence.length()<1)
                    continue;
                sentencenum++;
                String[] words = removeJunk(sentence2Words(sentence));
                wordCount += words.length;
                for (String word : words) {
                    if (isWordInDict(word)) {
                        // Get syllable count for each word and add it to syllableCount
                        int syllable = syllables.get(word);
                        syllableCount += syllable;
                        
                    } else {
                        
                        // If word not in dict, count syllables with dodgy method.
                        int syllable = countSyllablesLight(word);
                        syllableCount += syllable;
                    }
                }
            }
            score=0.39*wordCount/sentencenum+11.8*syllableCount/wordCount-15.59;
            return score;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Readability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    public double fleschKincaidSentence(String sentence) {
        /**
         * Calculates Kincaid score for any given sentence
         */
        String[] words = removeJunk(sentence2Words(sentence));
        int wordCount = words.length;
        int syllableCount = 0;
        for (String word : words) {

                if (isWordInDict(word)) {

                        // Get syllable count for each word and add it to syllableCount
                        int syllable = syllables.get(word);
                        syllableCount += syllable;

                } else {

                        // If word not in dict, count syllables with dodgy method.
                        int syllable = countSyllablesLight(word);
                        syllableCount += syllable;
                }
        }
        // Calculate Flesch Kincaid for single sentence.
        double kincaidScore = fleschKincaid(wordCount, syllableCount);
        return kincaidScore;
    }

	public String cleanWord(String word) {
		/**
		 * A quick function to get rid of any junk around a word. This is useful
		 * for cleaning a word before checking it's syllable count.
		 */
		word = word.replaceAll("[^a-zA-Z]", "");
		word = word.toLowerCase();
		word = word.trim();
		return word;
	}

	public int getSyllable(String word) {
		/**
		 * Get syllable count whether it be in the dictionary or the dodgy
		 * method.
		 */

		if (syllables.get(word) != null)
			return syllables.get(word);
		else
			return countSyllablesLight(word);
	}

	public void loadSyllableDict(String libfile) {   
            try {
                FileInputStream fs=null;
                fs = new FileInputStream(libfile);
                DataInputStream in=new DataInputStream(fs);
                BufferedReader br=new BufferedReader(new InputStreamReader(in));
                String strline;
                String word;
                int count;
                while((strline=br.readLine())!=null){
                    Scanner sc=new Scanner(strline);
                    sc.useDelimiter(",");
                    word=sc.next();
                    count=sc.nextInt();
                    if(this.syllables.containsKey(word)==false){
                        this.syllables.put(word, count);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Readability.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) { 
            Logger.getLogger(Readability.class.getName()).log(Level.SEVERE, null, ex);
        } 
                
	}

	public boolean isWordInDict(String word) {
		/**
		 * Check if a word is in the syllable dictionary. If it is, return true,
		 * else false.
		 */
		if (syllables.get(word) != null)
			return true;
		else
			return false;
	}

	public double fleschKincaid(int wordCount, int syllableCount) {
		/**
		 * Calculates Flesch-Kincaid algorithm. This is the raw score not the
		 * grade version. For single sentences only.
		 */
		double flesch = (206.835 - ((1.015 * wordCount) / 1)) - (((84.6 * syllableCount) / wordCount));
		return flesch;
	}

	private String[] sentence2Words(String sentence) {
		/**
		 * Takes a sentence string and returns an array of words.
		 */
		String[] words = sentence.split(" ");
		return words;
	}

	private String[] removeJunk(String[] line) {
		/**
		 * Takes an array of words (sentence) and returns an array of words
		 * minus non-alphabet characters.
		 */
		for (int i = 0; i < line.length; i++) {
			line[i] = line[i].replaceAll("[^a-zA-Z]", "");
			line[i] = line[i].toLowerCase();
		}
		return line;
	}

	public int countSyllablesLight(String s) {
		/** 
		 * A simple syllable counter.
		 * Use this as the plan B for when the dictionary method fails. 
		 */
		int syllables = s.length() - s.toLowerCase().replaceAll("a|e|i|o|u|", "").length();
		if (syllables < 1) return 1;
		else return syllables;
	}
        public void Run(String libfile, String datafile){
            loadSyllableDict(libfile);
            ArrayList<String> files=StrUtil.Initfolder(datafile);
            for(int i=0;i<files.size();i++){
                double score=this.FKscore(files.get(i));
                System.out.println(files.get(i)+"\t"+score);
            }
        }
        public static void main(String []args){
//            if(args.length<1){
//                System.out.println("Usage: [libfile] [datafilefolder]");
//                //eg:
//                //libfile: syllables.txt
//                System.exit(2);
//            }
            Readability myreader=new Readability();
            //myreader.Run(args[0], args[1]);
            myreader.Run("syllables.txt", "C:\\Users\\linhong\\Documents\\linhong-work\\Data\\acl-full-1.0-text\\acl-full-1.0-text");
        }
}
