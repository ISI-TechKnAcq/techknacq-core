package edu.isi.techknacq.topics.util;

import java.io.FileReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;


public class FvEntityResolver implements EntityResolver {
    public InputSource resolveEntity (String publicId, String systemId) {
        if ("http://api.technorati.com/dtd/tapi-002.xml".equals(systemId)) {
            FileReader myreader = null;
            try {
                myreader = new FileReader("tapi-002.xml");
            } catch(Exception e) {
            }
            return new InputSource(myreader);
        } else {
            return null;
        }
    }
}
