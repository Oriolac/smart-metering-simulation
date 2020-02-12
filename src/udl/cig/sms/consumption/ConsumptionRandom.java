package udl.cig.sms.consumption;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ConsumptionRandom implements ConsumptionReader {

    private static final int LENGTH_OF_CONSUMPTION = 8;

    @Override
    public BigInteger read() {
        //TODO: System.out.println
        BigInteger mi = new BigInteger(LENGTH_OF_CONSUMPTION, new SecureRandom());
        System.out.println("mi: " + mi.toString());
        return mi;
    }
}
