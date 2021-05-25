package cat.udl.cig.sms.connection;

import cat.udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;
import java.util.List;

/**
 * Interface of the receiver's substation
 */
public interface ReceiverSubstation {

    /**
     * @return List of SMSDatagram that will be received from the connection.
     * @throws IOException in case uses classes that throws IOException
     */
    List<SMSDatagram> receive() throws IOException;
}
