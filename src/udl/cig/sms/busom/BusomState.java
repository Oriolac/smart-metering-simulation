package udl.cig.sms.busom;

import java.io.IOException;

/**
 * State of the protocol.
 */
public interface BusomState {

    /**
     * Next state of the protocol.
     *
     * @return next state given a preset of conditions
     * @throws NullMessageException, if the message to be sent is void
     * @throws IOException,          if connection has failed.
     */
    BusomState next() throws NullMessageException, IOException;
}
