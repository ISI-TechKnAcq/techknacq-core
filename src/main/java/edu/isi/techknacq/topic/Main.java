package edu.isi.techknacq.topic;

/**
 *
 * @author linhong
 */
public class Main {
    public static void main(String []args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar [jarfilename] [dirname] " +
                               "[topicnum] [wordnum][alpha] [prefix]");
            System.out.println("args[0]: String: dirname");
            System.out.println("args[1]: Int: topicnum (default 20)");
            System.out.println("Int: k, where k denotes top-k word to be " +
                               "printed for each (default k=10)");
            System.out.println("double: parameter alpha (default 0.01)");
            System.out.println("String: prefix name for topic model " +
                               "(default tech)");
            System.exit(2);
        }
        // args[0]: String: dirname
        // args[1]: Int: topicnum (default 20)
        // args[2]: Int: k, where k denotes top-k word to be printed for each
        //          (default k=10)
        // args[3]: double: parameter alpha (default 0.01)
        // args[4]: String: prefix name for topic model (default tech)
        int topicnum = 20;
        int k = 10;
        String prefix = "tech";
        double alpha = 0.007;
        if (args.length > 1) {
            topicnum = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            k = Integer.parseInt(args[2]);
        }
        if (args.length > 3) {
            alpha = Double.parseDouble(args[3]);
        }
        if (args.length > 4) {
            prefix = args[4];
        }
        Topic mytopic = new Topic();
        mytopic.runTopic(args[0], topicnum, k, alpha, prefix);
    }
}
