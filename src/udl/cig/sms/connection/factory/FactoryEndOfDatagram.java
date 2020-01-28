package udl.cig.sms.connection.factory;

import udl.cig.sms.connection.datagram.EndOfDatagram;

public class FactoryEndOfDatagram implements FactorySMSDatagram {

    @Override
    public EndOfDatagram buildDatagram(byte[] bytes) {
        return new EndOfDatagram();
    }

    @Override
    public int getByteSize() {
        return 0;
    }
}
