/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isi.techknacq.topics.readinglist;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


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
    public void Readfile(){
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(filename));
            for (Object key : jsonObject.keySet()) {
                JSONObject documentInfo = (JSONObject) jsonObject.get(key);
                String author = (String) documentInfo.get("author");
                String title = (String) documentInfo.get("title");
                docMap.put((String) key, "author: "+author+", title: "+title);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadDocumentkey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadDocumentkey.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(ReadDocumentkey.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public String Getdocumentkey(String id){
        if(this.docMap.containsKey(id)==true)
            return docMap.get(id);
        else
            return "author: ??, title: ??";
    }
    public Map<String,String> GetDocmap(){
        return this.docMap;
    }
    
    public static void main(String []args){
        //test reading index.json file
        ReadDocumentkey rdk = new ReadDocumentkey("C:\\Users\\linhong\\Documents\\linhong-work\\Data\\NLP-full\\meta.json");
        rdk.Readfile();
        String id = "acl-X98-1030";
        String docVal = rdk.Getdocumentkey(id);
        System.out.println("Retrieving:");
        System.out.println(id + " --- " + docVal);
    }
}
