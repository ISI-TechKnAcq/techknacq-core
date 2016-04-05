package edu.isi.techknacq.topics.parser;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import edu.isi.techknacq.topics.util.FvEntityResolver;
import edu.isi.techknacq.topics.util.StrUtil;

/**
 *
 * @author Admin
 */
public class SearchXMLParser {
    /**
     *
     */
    protected static DocumentBuilderFactory dbFactory;
	
	
        /**
         *
         */
        protected static final int MAX_NUM_HITS=50;
	
        /**
         *
         */
        public SearchXMLParser(){
		try{
			dbFactory = DocumentBuilderFactory.newInstance();
			
			//docBuilder.setEntityResolver(new FvEntityResolver());
			//htmlParser= new HTMLParser();
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
	
        /**
         *
         * @param XmlFile
         * @return
         */
        protected Document createDomObject(File XmlFile){
		Document doc = null;
		try {		
			InputSource inSource =  new InputSource(new InputStreamReader(new FileInputStream(XmlFile), "UTF-8"));
                       // System.out.println(inSource.toString());
			DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
			docBuilder.setEntityResolver(new FvEntityResolver());
			
			doc = docBuilder.parse(inSource);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return doc;
	}
	//this method is not used, replaced by getTextContent() from Node class
        /**
         *
         * @param aNode
         * @return
         */
        protected  String getStringFromNode(Node aNode){
		String value=new String();
		if(aNode.getChildNodes().getLength()>0){
			value=aNode.getFirstChild().getNodeValue();
		}
		return value.trim();
	}
	
	
        /**
         *
         * @param aNode
         * @return
         */
        protected static int getIntFromNode(Node aNode){
		return StrUtil.parseInt(aNode.getTextContent());
	}
	
        /**
         *
         * @param aNode
         * @return
         */
        protected static long getLongValue(Node aNode){
		return StrUtil.parseLong(aNode.getTextContent());
	}


}
