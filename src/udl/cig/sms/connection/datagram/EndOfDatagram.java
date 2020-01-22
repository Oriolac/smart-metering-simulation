package udl.cig.sms.connection.datagram;

import udl.cig.sms.connection.SMSDatagram;

public class EndOfDatagram implements SMSDatagram {
    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
