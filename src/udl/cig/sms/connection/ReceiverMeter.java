package udl.cig.sms.connection;

import udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;

/**
 * Interface of the receiver's meter
 */
public interface ReceiverMeter {

    /**
     * @return SMSDatagram that will be received from the connection.
     * @throws IOException in case uses classes that throws IOException
     */
    SMSDatagram receive() throws IOException;
}
