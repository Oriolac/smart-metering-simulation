package cat.udl.cig.sms.connection.serializer;

import cat.udl.cig.sms.connection.datagram.SMSDatagram;

/**
 * Builds the SMSDatagram
 */
public interface SMSDatagramSerializer {

    /**
     * @param bytes the content of the datagram
     * @return SMSDatagram
     */
        SMSDatagram fromBytes(byte[] bytes);

    /**
     * @return the size of the content
     */
    int getByteSize();

}
