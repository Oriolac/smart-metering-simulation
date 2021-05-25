package sms.connection.factory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.connection.datagram.EndOfDatagram;
import udl.cig.sms.connection.factory.FactoryEndOfDatagram;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryEndOfDatagramTest {

    private static FactoryEndOfDatagram factory;

    @BeforeAll
    static void setUp() {
        factory = new FactoryEndOfDatagram();
    }


    @Test
    void buildDatagramTest() {
        assertEquals(new EndOfDatagram(), factory.buildDatagram(null));
    }

    @Test
    void getByteSizeTest() {
        assertEquals(0, factory.getByteSize());
    }
}