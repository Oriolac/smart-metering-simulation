package cat.udl.cig.sms.connection.serializer;

import cat.udl.cig.sms.connection.datagram.BigIntegerDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;

import java.math.BigInteger;

/**
 * Builds the BigIntegerDatagram
 */
public class BigIntegerDatagramSerializer implements SMSDatagramSerializer {
    private static final int BIG_INTEGER_SIZE = 3;

    /**
     * @param bytes that represents the number of the BigInteger
     * @return a BigIntegerDatagram that contains the number of the BigInteger
     */
    @Override
    public SMSDatagram fromBytes(byte[] bytes) {
        return new BigIntegerDatagram(new BigInteger(bytes));
    }

    /**
     * @return the length of the BigInteger size
     */
    @Override
    public int getByteSize() {
        return BIG_INTEGER_SIZE;
    }
}
