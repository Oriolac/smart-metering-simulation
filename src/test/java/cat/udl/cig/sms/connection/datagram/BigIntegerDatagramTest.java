package cat.udl.cig.sms.connection.datagram;

import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.connection.Datagrams;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class BigIntegerDatagramTest {

    @Test
    void toByteArray() {
        byte[] expected = new byte[4];
        expected[0] = (byte) Datagrams.BIG_INTEGER_DATAGRAM.ordinal();
        expected[3] = 1;
        BigIntegerDatagram actual = new BigIntegerDatagram(BigInteger.ONE);
        assertArrayEquals(expected, actual.toByteArray());
    }
}