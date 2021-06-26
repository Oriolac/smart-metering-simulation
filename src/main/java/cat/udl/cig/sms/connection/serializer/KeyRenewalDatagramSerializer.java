package cat.udl.cig.sms.connection.serializer;

import cat.udl.cig.sms.connection.datagram.EndOfDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;

public class KeyRenewalDatagramSerializer implements SMSDatagramSerializer {
    /**
     * @param bytes not used in EndOfDatagram because it has any content.
     * @return EndOfDatagram
     */
    @Override
    public EndOfDatagram fromBytes(byte[] bytes) {
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
