package cat.udl.cig.sms.connection.factory;

import cat.udl.cig.sms.connection.datagram.SMSDatagram;

/**
 * Builds the SMSDatagram
 */
public interface FactorySMSDatagram {

    /**
     * @param bytes the content of the datagram
     * @return SMSDatagram
     */
    SMSDatagram buildDatagram(byte[] bytes);

    /**
     * @return the size of the content
     */
    int getByteSize();

}
