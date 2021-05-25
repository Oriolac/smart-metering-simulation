package cat.udl.cig.sms.connection.datagram;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.connection.Datagrams;
import cat.udl.cig.sms.connection.datagram.EndOfDatagram;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class EndOfDatagramTest {

    private static EndOfDatagram data;

    @BeforeAll
    static void setUp() {
        data = new EndOfDatagram();
    }

    @Test
    void toByteArray() {
        byte[] expected = new byte[1];
        expected[0] = (byte) Datagrams.END_OF_DATAGRAM.ordinal();
        assertArrayEquals(expected, data.toByteArray());
    }
}