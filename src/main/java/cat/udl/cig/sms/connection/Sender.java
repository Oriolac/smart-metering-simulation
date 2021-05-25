package cat.udl.cig.sms.connection;

import cat.udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;

/**
 * Interface of the sender.
 * It must can send SMSDatragram
 */
public interface Sender {

    /**
     * @param data: the data that we would like to send
     * @throws IOException in case uses methods that throws IOException
     */
    void send(SMSDatagram data) throws IOException;
}
