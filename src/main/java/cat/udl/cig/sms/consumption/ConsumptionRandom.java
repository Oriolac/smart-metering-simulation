package cat.udl.cig.sms.consumption;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Extracts dependecy of ConsumptionReader. Used so changing
 * the reader can be easily done.
 */
public class ConsumptionRandom implements ConsumptionReader {


    private static final int LENGTH_OF_CONSUMPTION = 13;


    /**
     * Reads the consumption.
     *
     * @return consumption read as a BigInteger
     */
    @Override
    public BigInteger read() {
        return new BigInteger(LENGTH_OF_CONSUMPTION, new SecureRandom());
    }
}
