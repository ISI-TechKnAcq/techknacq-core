package edu.isi.techknacq.readinglist;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadDocumentKey {
    private Map <String, String> docMap;
    private String filename;
    private Logger logger = Logger.getLogger(ReadDocumentKey.class.getName());

    public ReadDocumentKey(String filename) {
        this.filename = filename;
        docMap = new HashMap<String, String>();
    }

    public void readFile() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject =
                (JSONObject) parser.parse(new FileReader(filename));
            for (Object key : jsonObject.keySet()) {
                JSONObject documentInfo = (JSONObject) jsonObject.get(key);
                String author = (String) documentInfo.get("author");
                String title = (String) documentInfo.get("title");
                docMap.put((String) key, "author: " + author + ", title: " + 
                           title);
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    public String getDocumentKey(String id) {
        if (this.docMap.containsKey(id))
            return docMap.get(id);
        else
            return "author: ??, title: ??";
    }

    public Map<String,String> getDocMap() {
        return this.docMap;
    }

    public static void main(String []args) {
        // Test reading index.json file
        ReadDocumentKey rdk = new ReadDocumentKey("meta.json");
        rdk.readFile();
        String id = "acl-X98-1030";
        String docVal = rdk.getDocumentKey(id);
        System.out.println("Retrieving:");
        System.out.println(id + " --- " + docVal);
    }
}
