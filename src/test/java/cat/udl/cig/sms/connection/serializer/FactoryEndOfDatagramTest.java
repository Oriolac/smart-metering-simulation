package cat.udl.cig.sms.connection.serializer;

import cat.udl.cig.sms.connection.datagram.EndOfDatagram;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FactoryEndOfDatagramTest {

    private static EndOfDatagramSerializer factory;

    @BeforeAll
    static void setUp() {
        factory = new EndOfDatagramSerializer();
    }


    @Test
    void buildDatagramTest() {
        assertEquals(new EndOfDatagram(), factory.fromBytes(null));
    }

    @Test
    void getByteSizeTest() {
        assertEquals(0, factory.getByteSize());
    }
}