package udl.cig.sms.meter.states;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import udl.cig.sms.busom.MeterBusomControllerInt;
import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.connection.ConnectionMeterInt;
import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyEstablishmentTest {

    private LoadCurve loadCurve;
    private KeyEstablishment keyEstablishment;
    private ConnectionMeterMock connection;
    private MeterBusomControllerMock controller;

    @BeforeEach
    void setUp() throws IOException, NullMessageException {
        loadCurve = LoadCurve.P192();
        connection = new ConnectionMeterMock();
        controller = new MeterBusomControllerMock();
        keyEstablishment = new KeyEstablishment(loadCurve, connection, controller);
    }

    @Test
    void generateChunksOfPrivateKey() {
        BigInteger value = BigInteger.valueOf(1234567890L);
        keyEstablishment.setPrivateKey(loadCurve.getField().toElement(value));
        List<BigInteger> chunks = keyEstablishment.generateChunksOfPrivateKey();
        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < chunks.size(); i++) {
            res = res.add(chunks.get(i).multiply(BigInteger.TWO.pow(i * 13)));
        }
        assertEquals(value, res);
    }

    @Test
    void next() throws IOException, NullMessageException {
        keyEstablishment.next();
        assertEquals(1, controller.getCount());
    }

    public static class MeterBusomControllerMock implements MeterBusomControllerInt {

        private int count;

        public MeterBusomControllerMock() {
            count = 0;
        }

        @Override
        public void sendMessage(List<BigInteger> messages) throws IOException, NullMessageException {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    public static class ConnectionMeterMock implements ConnectionMeterInt {

        @Override
        public SMSDatagram receive() throws IOException {
            throw new IOException("Unreachable exception");
        }

        @Override
        public void send(SMSDatagram data) throws IOException {
            throw new IOException("Unreachable exception");
        }
    }
}