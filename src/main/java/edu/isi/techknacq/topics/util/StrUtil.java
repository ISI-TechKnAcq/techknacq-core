package edu.isi.techknacq.topics.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isi.techknacq.topics.topic.Weightpair;

public class StrUtil {
    public static int getMapMaxvalue(Map mp) {
        int max = 0;
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Integer w = (Integer)pairs.getValue();
            if (w > max)
                max = w;
        }
        return max;
    }

    public static void printMap(Map mp, BufferedWriter out) {
        try {
            Iterator it = mp.entrySet().iterator();
            while (it.hasNext()) {
                try {
                    Map.Entry pairs = (Map.Entry)it.next();
                    //Integer w = (Integer)pairs.getValue();
                    //if(w>1)
                    out.write(pairs.getKey() + "\t" + pairs.getValue()+"\n");
                } catch (IOException ex) {
                    Logger.getLogger(StrUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(StrUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void enumarateMap(Map mp, List l) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Integer windex = (Integer)pairs.getKey();
            Double weight = (Double)pairs.getValue();
            Weightpair o = new Weightpair(weight,windex);
            l.add(o);
        }
    }

    public static ArrayList<String> initFolder(String path) {
        ArrayList<String> filelists = new ArrayList<String>(100);
        try {
            LinkedList<String> Dir = new LinkedList<String>();
            File f = new File(path);
            Dir.add(f.getCanonicalPath());
            while (!Dir.isEmpty()) {
                f = new File(Dir.pop());
                if (f.isFile()) {
                    filelists.add(f.getAbsolutePath());
                } else {
                    String arr[] = f.list();
                    try {
                        for (int i = 0; i < arr.length; i++) {
                            Dir.add(f.getAbsolutePath()+"/"+arr[i]);
                        }
                    }
                    catch(NullPointerException exp) {
                        Dir.remove(f.getAbsoluteFile());
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(StrUtil.class.getName()).log(Level.SEVERE, null,
                                                          ex);
        }
        return filelists;
    }

    public static String fixEncoding(String latin1) {
        try {
            byte[] bytes = latin1.getBytes("ISO-8859-1");
            if (!validUTF8(bytes))
                return latin1;
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            // Impossible, throw unchecked
            throw new IllegalStateException("No Latin1 or UTF-8: " + e.getMessage());
        }
    }

    public static boolean validUTF8(byte[] input) {
        int i = 0;
        // Check for BOM
        if (input.length >= 3 && (input[0] & 0xFF) == 0xEF
            && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
            i = 3;
        }

        int end;
        for (int j = input.length; i < j; ++i) {
            int octet = input[i];
            if ((octet & 0x80) == 0) {
                continue; // ASCII
            }

            // Check for UTF-8 leading byte
            if ((octet & 0xE0) == 0xC0) {
                end = i + 1;
            } else if ((octet & 0xF0) == 0xE0) {
                end = i + 2;
            } else if ((octet & 0xF8) == 0xF0) {
                end = i + 3;
            } else {
                // Java only supports BMP so 3 is max
                return false;
            }

            while (i < end) {
                i++;
                octet = input[i];
                if ((octet & 0xC0) != 0x80) {
                    // Not a valid trailing byte
                    return false;
                }
            }
        }
        return true;
    }

    public static int parseInt(String str) {
        int value = 0;

        if (str == null) {
            return value;
        }

        if (str.trim().length() > 0) {
            try {
                value = Integer.parseInt(getDigitStr(str));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public static long parseLong(String str) {
        long value = 0;

        if (str == null) {
            return 0;
        }

        if (str.trim().length() > 0) {
            try {
                value = Long.parseLong(getDigitStr(str));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    public static String getDigitStr(String digStr) {
        if (digStr == null) {
            return "0";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digStr.length(); i++) {
            char c = digStr.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString().trim();
    }

    public static String getDocId(String searchTimeStr, int queryRank,
                                  int hitRank) {
        StringBuffer sb = new StringBuffer();
        sb.append(searchTimeStr).append(".");
        if (queryRank < 10) {
            sb.append(0);
        }
        sb.append(queryRank).append(".");
        if (hitRank < 10) {
            sb.append(0);
        }
        sb.append(hitRank);
        return sb.toString().trim();
    }

    public static long parseSearchTime(String timeStr) {
        long timeInSec = 0;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        try {
            Date date = (Date) formatter.parse(timeStr);
            timeInSec = date.getTime() / 1000 ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeInSec;
    }

    public static long parsetweettime(String timestr) {
        long timeIns = 0;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = (Date) formatter.parse(timestr);
            timeIns = date.getTime() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeIns;
    }

    public static String getTimestr2(long timeLong) {
        Date date = new Date(timeLong);
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        return timeStr;
    }

    public static String getTimeStr(long timeLong) {
        Date date = new Date(timeLong);
        String timeStr = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(date);
        return timeStr;
    }

    public static int getWinLen(String starttimestr, String endtimestr) {
        long start = StrUtil.parseSearchTime(starttimestr);
        long end = StrUtil.parseSearchTime(endtimestr);
        long pos = (end - start)/(3*3600*1000);
        return (int)pos;
    }

    public static String floattoString(double val) {
        int color_dec = (int)(255*val);
        System.out.println(color_dec);
        return Integer.toHexString(color_dec);
    }

    public static void main(String[] arg) {
//        System.out.println(StrUtil.getSearchTime("20070811110001"));
//        long start=StrUtil.parseSearchTime("20061108010001");
//        long end=StrUtil.parseSearchTime("20071130220001");
//        long number = (end - start)/(3*3600*1000);
//        System.out.println(number*15);
//        System.out.println((float)2978/number);
//        System.out.println((float)44594/number/15);
        long start = StrUtil.parsetweettime("2012-08-10 04:43:58");
        long end = StrUtil.parsetweettime("2012-08-10 05:43:58");
        long number = (end - start);
        System.out.println(number);
        String timestr = StrUtil.getTimestr2(1402365792700l);
        System.out.println(timestr);
        System.out.println(StrUtil.floattoString(0.9));
    }
}
