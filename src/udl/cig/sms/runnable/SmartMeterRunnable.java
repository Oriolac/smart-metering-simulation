package udl.cig.sms.runnable;

import cat.udl.cig.exceptions.NotImplementedException;
import javafx.util.Pair;
import udl.cig.sms.crypt.Cypher;
import udl.cig.sms.crypt.CypherMessage;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

public class SmartMeterRunnable {

    private static Cypher cypher;
    private static final int L = 10;


    public static void main(String[] args) {
        initVariables(args);
        /*while (true) {
            // Enviar....
            send(cypher.generateSij(L));
            //getpackage().then( | );
        }*/
    }

    private static void initVariables(String[] args) {
        File file = new File(args[1]);
        cypher = new CypherMessage(file);
    }

    private static void send(List<Pair<BigInteger, Integer>> privateKey) {
        throw new NotImplementedException();
    }

}
