package cat.udl.cig.sms.connection.serializer;

import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.connection.datagram.BigIntegerDatagram;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryBigIntegerDatagramTest {

    @Test
    void buildDatagram() {
        byte[] bytes = new byte[3];
        bytes[2] = 1;
        assertEquals(new BigIntegerDatagram(BigInteger.ONE),
                new BigIntegerDatagramSerializer().fromBytes(bytes));
    }

    @Test
    void getByteSize() {
        assertEquals(3, new BigIntegerDatagramSerializer().getByteSize());
    }
}