package udl.cig.sms.runnable;

import udl.cig.sms.crypt.Decypher;
import udl.cig.sms.crypt.DecypherMessage;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;

public class SubstationRunnable {

    private static Decypher cypher;

    public static void main(String[] args) {
        initVariables(args);
        /*while (true) {

        }*/
    }


    private static void initVariables(String[] args) {
        File file = new File(args[1]);
        cypher = new DecypherMessage(file);
    }
}
