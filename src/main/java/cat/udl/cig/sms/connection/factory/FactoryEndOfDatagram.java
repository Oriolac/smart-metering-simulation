package cat.udl.cig.sms.connection.factory;

import cat.udl.cig.sms.connection.datagram.EndOfDatagram;

/**
 * Builds the EndOfDatagram
 */
public class FactoryEndOfDatagram implements FactorySMSDatagram {

    /**
     * @param bytes not used in EndOfDatagram because it has any content.
     * @return EndOfDatagram
     */
    @Override
    public EndOfDatagram buildDatagram(byte[] bytes) {
        return new EndOfDatagram();
    }

    /**
     * @return the size of the content of the datagram.
     */
    @Override
    public int getByteSize() {
        return 0;
    }
}
