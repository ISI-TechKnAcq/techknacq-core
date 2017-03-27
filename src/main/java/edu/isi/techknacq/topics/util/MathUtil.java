package edu.isi.techknacq.topics.util;

import java.util.ArrayList;

public class MathUtil {
    public static double getIntAvg(ArrayList<Integer> a) {
        if (a.size() < 1)
            return 0;
        double res = 0;
        for (int i = 0; i < a.size(); i++) {
            res += (double)a.get(i)/a.size();
        }
        return res;
    }

    public static double getLongAvg(ArrayList<Long> a) {
        if (a.size() < 1)
            return 0;
        double res = 0;
        for (int i = 0; i < a.size(); i++) {
            res += (double)a.get(i)/a.size();
        }
        return res;
    }

    public static double getIntSD(ArrayList<Integer> a, double mean) {
        if (a.size() < 2)
            return 0;
        else {
            double res = 0;
            for (int i = 0; i < a.size(); i++) {
                res += ((a.get(i)-mean)*(a.get(i)-mean));
            }
            res /= (a.size()-1);
            res = Math.sqrt(res);
            return res;
        }
    }

    public static double getLongSD(ArrayList<Long> a, double mean) {
        if (a.size()<2)
            return 0;
        else {
            double res = 0;
            for(int i = 0;i<a.size();i++) {
                res+=((a.get(i)-mean)*(a.get(i)-mean));
            }
            res/=(a.size()-1);
            res=Math.sqrt(res);
            return res;
        }
    }

    public static double GetIntMax(ArrayList<Integer> a) {
        double max = 0;
        for(int i = 0;i<a.size();i++) {
            if (a.get(i)>max)
                max=a.get(i);
        }
        return max;
    }

    public static double GetLongMax(ArrayList<Long> a) {
        double max = 0;
        for(int i = 0;i<a.size();i++) {
            if (a.get(i)>max)
                max=a.get(i);
        }
        return max;
    }

    public static double GetIntMeannomax(ArrayList<Integer> a, double max) {
        if (a.size()<1)
            return 0;
        double res = 0;
        for(int i = 0;i<a.size();i++) {
            if (a.get(i)<max)
                res+=(double)a.get(i)/a.size();
        }
        return res;
    }

    public static double GetLongMeannomax(ArrayList<Long> a, double max) {
        double mean = 0;
        if (a.size()<1)
            return 0;
        double res = 0;
        for(int i = 0;i<a.size();i++) {
            if (a.get(i)<max)
                res+=(double)a.get(i)/a.size();
        }
        return res;
    }
    public static double GetIntSDnomax(ArrayList<Integer> a, double max,
                                       double mean) {
        if (a.size()<2)
            return 0;
        else {
            double res = 0;
            for(int i = 0;i<a.size();i++) {
                if (a.get(i)<max)
                    res+=((a.get(i)-mean)*(a.get(i)-mean));
            }
            res/=(a.size()-1);
            res=Math.sqrt(res);
            return res;
        }
    }
    public static double GetLongSDnomax(ArrayList<Long> a, double max,
                                        double mean) {
        if (a.size() < 2)
            return 0;
        else {
            double res = 0;
            for(int i = 0;i<a.size();i++) {
                if (a.get(i)<max)
                    res+=((a.get(i)-mean)*(a.get(i)-mean));
            }
            res/=(a.size()-1);
            res=Math.sqrt(res);
            return res;
        }
    }
    public static float hypot(float a, float b) {
        float r;
        if (Math.abs(a) > Math.abs(b)) {
            r = b/a;
            r = (float)Math.abs(a)*(float)Math.sqrt(1+r*r);
        }
        else
            if (b != 0) {
                r = a/b;
                r = (float)Math.abs(b)*(float)Math.sqrt(1+r*r);
            }
            else {
                r = 0.0F;
            }
        return r;
    }
    public static float average(float[]v) {
        float avg = 0;
        if (v==null)
            return 0;
        else {
            for(int i = 0;i<v.length;i++) {
                avg=avg+v[i];
            }
            avg=avg/v.length;
            return avg;
        }
    }
    public static float average(int []v) {
        float avg = 0;
        if (v==null)
            return 0;
        else {
            for(int i = 0;i<v.length;i++)
                avg=avg+v[i];
            avg=avg/v.length;
            return avg;
        }
    }
    public static int MAX(int []v) {
        int max = 0;
        for(int i = 0;i<v.length;i++) {
            if (max<v[i])
                max=v[i];
        }
        return max;
    }

    public static float MAX(float []v) {
        float max = 0.0F;
        for(int i = 0;i<v.length;i++) {
            if (max<v[i])
                max=v[i];
        }
        return max;
    }

    public static float SD(float []v) {
        float avg=average(v);
        float sd = 0;
        for(int i = 0;i<v.length;i++) {
            sd=sd+(v[i]-avg)*(v[i]-avg);
        }
        sd=(float)Math.sqrt(sd/v.length);
        return sd;
    }

    public static float SD(int []v) {
        float avg=average(v);
        float sd = 0;
        for(int i = 0;i<v.length;i++)
            sd=sd+(v[i]-avg)*(v[i]-avg);
        sd=(float)Math.sqrt(sd/v.length);
        return sd;
    }

    public static double Cosinsimilarity(ArrayList<Double> v1,
                                         ArrayList<Double> v2) {
        if (v1.size()!=v2.size()) {
            System.out.println("Error occurs in computing the word vector");
            return -1;
        }
        else {
            if (v1.size()= = 0||v2.size()= = 0||v1==null||v2==null)
                return 0;
            else {
                double dotproduct = 0;
                for (int i = 0;i<v1.size();i++)
                    dotproduct+=v1.get(i)*v2.get(i);
                if (dotproduct> = 0.0) {
                    double v1length=getVectorlength(v1);
                    double v2length=getVectorlength(v2);
                    if (v1length! = 0&&v2length! = 0)
                        return dotproduct/v1length/v2length;
                    else
                        return 0;
                }
                else {
                    return 0;
                }
            }
        }
    }

    public static float Cosinsimilarity(float []v1, float []v2) {
        if (v1.length!=v2.length) {
            System.out.println("Error occurs in computing the tfidf vector");
            return -1;
        }
        else {
            if (v1.length= = 0||v2.length= = 0||v1==null||v2==null)
                return 0;
            else {
                float dotproduct = 0;
                for (int i = 0;i<v1.length;i++)
                    dotproduct+=v1[i]*v2[i];
                if (dotproduct> = 0.0) {
                    float v1length=getVectorlength(v1);
                    float v2length=getVectorlength(v2);
                    if (v1length != 0 && v2length != 0)
                        return dotproduct/v1length/v2length;
                    else
                        return 0;
                }
                else {
                    return 0;
                }
            }
        }
    }

    public static double getVectorlength(ArrayList<Double> v) {
        double result = 0;
        for (int i = 0;i<v.size();i++) {
            result += v.get(i)*v.get(i);
        }
        return Math.sqrt(result);
    }

    public static float getVectorlength(float []v) {
        //compute the value ||v||
        float result = 0;
        for (int i = 0; i < v.length; i++)
            result += v[i]*v[i];
        return (float)Math.sqrt(result);
    }

    public static float EJsimilarity(float []v1, float []v2) {
        if (v1.length != v2.length) {
            System.out.println("Error occurs in computing the tfidf vector");
            return -1F;
        }
        else {
            if (v1.length= = 0||v2.length= = 0||v1==null||v2==null)
                return 0.0F;
            else {
                float dotproduct = 0;
                for (int i = 0;i<v1.length;i++)
                    dotproduct+=v1[i]*v2[i];
                if (dotproduct>0) {
                    float v1length = getVectorlength(v1);
                    float v2length = getVectorlength(v2);
                    float temp=v1length*v1length+v2length*v2length-dotproduct;
                    if (temp= = 0)
                        return 0.0F;
                    else
                        return dotproduct/temp;
                }
                else
                    return 0.0F;
            }
        }
    }

    /**
     *
     * @param v
     * @return the maximum length of non-zero sequence;
     */
    public static int MaxLength(boolean[] v) {
        int maxlen = 0;
        int pos = 0;
        while (pos<v.length) {
            if (v[pos]==false)
                pos++;
            else {
                int templen = 0;
                while (v[pos]==true) {
                    if (pos!=v.length-1) {
                        templen++;
                        pos++;
                    }
                    else {
                        pos++;
                        break;
                    }
                }
                if (templen > maxlen)
                    maxlen = templen;
            }
        }
        return maxlen;
    }

    public static float CC(int []v1, int []v2) {
        if (v1 == null||v2 == null||v1.length= = 0||v2.length= = 0||v1.length!=v2.length)
            return 0;
        else {
            float cc = 0.0F;
            float avg1=average(v1);
            float avg2=average(v2);
            float sd1=SD(v1);
            float sd2=SD(v2);
            for (int i = 0;i<v1.length;i++)
                cc = cc+v1[i]*v2[i];
            if (sd1! = 0&&sd2! = 0)
                cc = (cc-avg1*avg2*v1.length)/(v1.length-1)/sd1/sd2;
            else
                cc = 1;
            return cc;
        }
    }

    public static void main(String[]args) {
        //test
        boolean []v = new boolean[8];
        for (int i = 0; i <= 3; i++)
            v[i] = true;
        v[4] = false;
        for (int i = 5; i < v.length; i++)
            v[i] = true;
        int maxlen = MaxLength(v);
        System.out.println(maxlen);
    }

    public static void Normalize(ArrayList <Float> a) {
        float d = 0.0f;
        for (int i = 0;i<a.size();i++)
            d = d+a.get(i)*a.get(i);
        if (d == 1.0f  || d == 0.0f)
            return;

        d = 1/(float) Math.sqrt(d);
        for (int i = 0;i<a.size();i++) {
            float temp = a.get(i)*d;
            a.set(i, temp);
        }
    }

    public static void Lognormalize(ArrayList <Float> a) {
        float d = 0.0f;
        for (int i = 0;i<a.size();i++)
            d=d+(float)Math.log(a.get(i))*(float)Math.log(a.get(i));
        if (d == 1.0f  || d == 0.0f ) return;
        d = 1/(float) Math.sqrt(d);
        for (int i = 0;i<a.size();i++) {
            float temp = (float)Math.pow(a.get(i), d);
            a.set(i, temp);
        }
    }
}
