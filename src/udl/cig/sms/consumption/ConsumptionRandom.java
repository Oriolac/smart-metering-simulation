package udl.cig.sms.consumption;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Extracts dependecy of ConsumptionReader. Used so changing
 * the reader can be easily done.
 */
public class ConsumptionRandom implements ConsumptionReader {


    private static final int LENGTH_OF_CONSUMPTION = 7;


    /**
     * Reads the consumption.
     *
     * @return consumption read as a BigInteger
     */
    @Override
    public BigInteger read() {
        //TODO: System.out.println
        BigInteger mi = new BigInteger(LENGTH_OF_CONSUMPTION, new SecureRandom());
        System.out.println("mi: " + mi.toString());
        return mi;
    }
}
