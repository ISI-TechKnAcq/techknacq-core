package edu.isi.techknacq.topics.util;

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;

public class TokenProcessor {
    private static Analyzer kStemStdAnalyzer;

    static {
        kStemStdAnalyzer = new KStemStandardAnalyzer();
    }

    public TokenStream getTokenStream(String inputStr) {
        TokenStream stream = kStemStdAnalyzer.tokenStream("contents", new StringReader(inputStr));
        return stream;
    }

    public String getTokenString(String inputStr) {
        StringBuffer sb = new StringBuffer();
        TokenStream stream = this.getTokenStream(inputStr);
        try {
            while (true) {
                Token token = stream.next();
                if (token == null)
                    break;
                sb.append(token.termBuffer(), 0, token.termLength()).append(" ");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return sb.toString().toLowerCase().trim();
    }

    public static void main(String[] arg) {
        String input = "www.ntu.edu.sg  ipods ipod ipod.s's iphone] [wii] Copyright � 2003, Center for Intelligent Information Retrieval,University of Massachusetts, Amherst. All rights reserved. University of Massachusetts must not be used to endorse or promote products derived from this software without prior written permission. To obtain permission, contact info@ciir.cs.umass.edu.";

        TokenProcessor tp = new TokenProcessor();
        System.out.println(tp.getTokenString(input));
    }
}
