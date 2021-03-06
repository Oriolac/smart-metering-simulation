package cat.udl.cig.sms.busom;

import cat.udl.cig.sms.connection.KeyRenewalException;

import java.io.IOException;

/**
 * State of the protocol.
 */
public interface BusomMeterState {

    /**
     * Next state of the protocol.
     *
     * @return next state given a preset of conditions
     * @throws NullMessageException, if the message to be sent is void
     * @throws IOException,          if connection has failed.
     */
    BusomMeterState next() throws NullMessageException, IOException, KeyRenewalException;
}
