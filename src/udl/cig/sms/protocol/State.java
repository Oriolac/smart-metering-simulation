package udl.cig.sms.protocol;

import udl.cig.sms.busom.NullMessageException;

import java.io.IOException;

/**
 * State of the protocol of Smart Metering
 */
public interface State {

    /**
     * @return the next state of the protocol
     * @throws IOException in case that the IO fails
     * @throws NullMessageException in case the message is empty
     */
    State next() throws IOException, NullMessageException;
}
