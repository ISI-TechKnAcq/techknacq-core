package edu.isi.techknacq.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author linhong
 */
public class TestPrint {
    private static Logger logger =
        Logger.getLogger(TestPrint.class.getName());

    public static void printMap(Map mp, BufferedWriter out) {
        try {
            Iterator it = mp.entrySet().iterator();
            while (it.hasNext()) {
                try {
                    Map.Entry pairs = (Map.Entry)it.next();
                    Integer w = (Integer)pairs.getValue();
                    if (w > 1)
                        out.write(pairs.getKey() + "\t"
                                  + pairs.getValue() + "\n");
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
            out.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void printcsvMap(Map mp, BufferedWriter out) {
        try {
            Iterator it = mp.entrySet().iterator();
            while (it.hasNext()) {
                try {
                    Map.Entry pairs = (Map.Entry)it.next();
                    out.write(pairs.getKey() + "," + pairs.getValue() + "\n");
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
            out.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            System.out.println(pairs.getKey() + "\t" + pairs.getValue());
        }
    }

    public static void printInArray(ArrayList a) {
        for (int i = 0; i < a.size(); i++) {
            System.out.println(a.get(i));
        }
    }

    public static void printTabArray(ArrayList a) {
        for (int i = 0; i < a.size(); i++) {
            System.out.print(a.get(i) + "\t");
        }
        System.out.println();
    }
}
