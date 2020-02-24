package udl.cig.sms.busom;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public interface MeterBusomControllerInt {

    /**
     * Starts the protocol
     *
     * @throws NullMessageException Never throwed. Declared by substractions of code.
     * @throws IOException          if connection fails.
     */
    void start() throws NullMessageException, IOException;

    /**
     * Sends List of messages to the substation
     *
     * @param messages messages to be sent. At maximum 13 bits
     * @throws IOException          if connection fails
     * @throws NullMessageException if some message is null.
     */
    void sendMessage(List<BigInteger> messages) throws IOException, NullMessageException;
}
