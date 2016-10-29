/*
 * Created on 2004-11-4
 */

package edu.isi.techknacq.topics.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * @author sunax
 *
 * This is a simple HTML parser that can parse the text body and all the links.
 * Further enhancement needed to control the filter the parsed URLs.
 */
public class HTMLParser {

    private StringBuffer sb=null;

    class MiniParser extends HTMLEditorKit.ParserCallback {
        public void handleText(char[] data, int pos) {
            sb.append(data).append(' ');
        }
    }

    /**
     *
     * @param str
     * @return
     */
    public String parseHTML(String str) {
        sb = new StringBuffer();
        try {
            Reader r = new StringReader(str);
            new ParserDelegator().parse(r, new MiniParser(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString().trim();
    }

    /**
     *
     * @param htmlfile
     * @return
     */
    public String parseHTMLfromfile(File htmlfile) {
        sb = new StringBuffer();
        try {
            Reader r = new FileReader(htmlfile);
            new ParserDelegator().parse(r, new MiniParser(), true);
        } catch (IOException ex) {
            Logger.getLogger(HTMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString().trim();
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            HTMLParser htmlp = new HTMLParser();
            String ahtml="<strong class=\"keyword\"> s > < again </strong> end";
            String bhtml="1 this is a test<b type=\"keyword\" and> again </b> end";
            String chtml="2 this is a test<b type=\"keyword\" and> again </b> end";
            String dhtml="3 this is a test<b type=\"keyword\" and> again </b> end";

            System.out.println(htmlp.parseHTML(ahtml));
            System.out.println(htmlp.parseHTML(bhtml));
            System.out.println(htmlp.parseHTML(chtml));
            System.out.println(htmlp.parseHTML(dhtml));

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
