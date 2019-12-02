package udl.cig.sms.runnable;

import udl.cig.sms.crypt.Cypher;
import udl.cig.sms.crypt.CypherMessage;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;

public class SmartMeterRunnable {

    private static Cypher cypher;


    public static void main(String[] args) {
        initVariables(args);
        while (true) {
            //getpackage().then( | );
        }
    }

    private static void initVariables(String[] args) {
        HashMap<String, BigInteger> param = new HashMap<>();
        File file = new File(args[1]);
        cypher = new CypherMessage(new Settings(file));

    }

}
