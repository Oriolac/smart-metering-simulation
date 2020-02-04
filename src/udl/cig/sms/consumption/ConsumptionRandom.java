package udl.cig.sms.consumption;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ConsumptionRandom implements ConsumptionReader {

    private static final int LENGTH_OF_CONSUMPTION = 13;

    @Override
    public BigInteger read() {
        return new BigInteger(LENGTH_OF_CONSUMPTION, new SecureRandom());
    }
}
