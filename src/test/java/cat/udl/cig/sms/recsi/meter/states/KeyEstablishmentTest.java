package cat.udl.cig.sms.recsi.meter.states;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.busom.MeterBusomControllerInt;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.consumption.ConsumptionRandom;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.meter.MeterContext;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyEstablishmentTest {

    private CurveConfiguration curveConfiguration;
    private KeyEstablishment keyEstablishment;
    private ConnectionMeterMock connection;
    private MeterBusomControllerMock controller;
    private MeterContext factory;

    @BeforeEach
    void setUp() {
        curveConfiguration = CurveConfiguration.P192();
        connection = new ConnectionMeterMock();
        controller = new MeterBusomControllerMock();
        factory = new MeterContext(curveConfiguration, connection, new ConsumptionRandom(), "");
        keyEstablishment = factory.makeKeyEstablishment();
        keyEstablishment.setMeterBusom(controller);
    }

    @Test
    void generateChunksOfPrivateKey() {
        BigInteger value = BigInteger.valueOf(1234567890L);
        keyEstablishment.setPrivateKey(curveConfiguration.getField().toElement(value));
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
        assertEquals(2, controller.getCount());
    }

    public static class MeterBusomControllerMock implements MeterBusomControllerInt {

        private int count;

        public MeterBusomControllerMock() {
            count = 0;
        }

        @Override
        public void start() {
            count++;
        }

        @Override
        public void sendMessage(List<BigInteger> messages) {
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

        @Override
        public void close() throws IOException {

        }
    }
}