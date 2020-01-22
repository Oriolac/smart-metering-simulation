package udl.cig.sms.runnable;

import udl.cig.sms.crypt.DecipherMessage;

import java.io.File;

public class SubstationRunnable {

    private static DecipherMessage cypher;


    public static void main(String[] args) {
        initVariables(args);
        // t = random.ital()
        // List<GeneralECPoint> cis = getCis(t)
        // BigInteger m = cypher.decrypt(cis, t);
    }


    private static void initVariables(String[] args) {
        File file = new File(args[1]);
    }
}
