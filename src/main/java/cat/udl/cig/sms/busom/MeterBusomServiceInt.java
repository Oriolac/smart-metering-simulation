package cat.udl.cig.sms.busom;

import cat.udl.cig.sms.connection.KeyRenewalException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public interface MeterBusomServiceInt {

    /**
     * Starts the protocol
     *
     * @throws NullMessageException Never throwed. Declared by substractions of code.
     * @throws IOException          if connection fails.
     */
    void start() throws NullMessageException, IOException, KeyRenewalException;

    /**
     * Sends List of messages to the substation
     *
     * @param messages messages to be sent. At maximum 13 bits
     * @throws IOException          if connection fails
     * @throws NullMessageException if some message is null.
     */
    void sendMessage(List<BigInteger> messages) throws IOException, NullMessageException, KeyRenewalException;
}
