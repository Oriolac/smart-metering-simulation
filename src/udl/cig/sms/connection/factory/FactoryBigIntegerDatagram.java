package udl.cig.sms.connection.factory;

import udl.cig.sms.connection.datagram.BigIntegerDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;

import java.math.BigInteger;

public class FactoryBigIntegerDatagram implements FactorySMSDatagram {
    private static final int NUM_PACKAGE = 1;
    private static final int BIG_INTEGER_SIZE = 3;

    @Override
    public SMSDatagram buildDatagram(byte[] bytes) {
        return new BigIntegerDatagram(new BigInteger(bytes));
    }

    @Override
    public int getByteSize() {
        return BIG_INTEGER_SIZE;
    }
}
