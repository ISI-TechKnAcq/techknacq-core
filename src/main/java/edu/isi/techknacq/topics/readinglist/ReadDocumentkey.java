/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isi.techknacq.topics.readinglist;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 *
 * @author Linhong
 */
public class ReadDocumentkey {
    private Map <String, String> docMap;
    private String filename;
   
    public ReadDocumentkey(String filename){
        this.filename = filename;
        docMap = new HashMap<String, String>();
    }
    
    public void Readfile() throws JsonParseException, IOException{
        JsonFactory f = new JsonFactory();
        JsonParser jp = f.createParser(new File(filename));
        jp.nextToken();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = jp.getCurrentName();
            jp.nextToken(); // move to value
            String fieldValue = "";
            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String nameField = jp.getCurrentName(); 
                jp.nextToken(); // move to value
                if ("author".equals(nameField)) {
                    String names = jp.getText();
                    fieldValue += "author: "+names;
                } 
                if ("title".equals(nameField)) {
                    String titleName = jp.getText();
                    fieldValue += ", title: "+titleName;
                }
            }
            docMap.put(fieldname, fieldValue);
        }
        jp.close();
    }
    
    public String Getdocumentkey(String id){
        if(this.docMap.containsKey(id)==true)
            return docMap.get(id);
        else
            return null;
    }
    public Map<String,String> GetDocmap(){
        return this.docMap;
    }
    
    public static void main(String []args) throws JsonParseException, IOException{
        //test reading index.json file
        ReadDocumentkey rdk = new ReadDocumentkey("index.json");
        rdk.Readfile();
        String id = "X98-1030";
        String docVal = rdk.Getdocumentkey(id);
        System.out.println("Retrieving:");
        System.out.println(id + " --- " + docVal);
    }
}
