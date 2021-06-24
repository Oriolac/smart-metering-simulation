package cat.udl.cig.sms.consumption;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Extracts dependecy of ConsumptionReader. Used so changing
 * the reader can be easily done.
 */
public interface ConsumptionReader {

    /**
     * Reads the consumption.
     *
     * @return consumption read as a BigInteger
     */
    BigInteger read() throws IOException;
}
