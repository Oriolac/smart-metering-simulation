package cat.udl.cig.sms.busom;

import java.io.IOException;
import java.math.BigInteger;

public interface SubstationBusomServiceInt {

    /**
     * Receives and computes a key, adding all the messages (sis) with their power.
     *
     * @return Key to decrypt messages of the parent protocol
     * @throws IOException          If connection fails.
     * @throws NullMessageException Never sent.
     */
    BigInteger receiveSecretKey() throws IOException, NullMessageException;
}
