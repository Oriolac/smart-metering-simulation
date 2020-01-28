package udl.cig.sms.connection.factory;

import udl.cig.sms.connection.datagram.SMSDatagram;

public interface FactorySMSDatagram {

    SMSDatagram buildDatagram(byte[] bytes);

    int getByteSize();
}
