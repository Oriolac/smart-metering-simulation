package cat.udl.cig.sms.connection.factory;

import cat.udl.cig.sms.connection.datagram.EndOfDatagram;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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