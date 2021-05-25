package sms.connection.factory;

import org.junit.jupiter.api.Test;
import udl.cig.sms.connection.datagram.BigIntegerDatagram;
import udl.cig.sms.connection.factory.FactoryBigIntegerDatagram;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryBigIntegerDatagramTest {

    @Test
    void buildDatagram() {
        byte[] bytes = new byte[3];
        bytes[2] = 1;
        assertEquals(new BigIntegerDatagram(BigInteger.ONE),
                new FactoryBigIntegerDatagram().buildDatagram(bytes));
    }

    @Test
    void getByteSize() {
        assertEquals(3, new FactoryBigIntegerDatagram().getByteSize());
    }
}