package udl.cig.sms.connection.datagram;

import udl.cig.sms.connection.SMSDatagram;

public class CipherTextDatagram implements SMSDatagram {

    public CipherTextDatagram() {}

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
