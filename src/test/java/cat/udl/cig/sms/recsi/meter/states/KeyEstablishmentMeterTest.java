package cat.udl.cig.sms.recsi.meter.states;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.busom.MeterBusomServiceInt;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.consumption.ConsumptionRandom;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.meter.MeterStateContext;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyEstablishmentMeterTest {

    private CurveConfiguration curveConfiguration;
    private KeyEstablishmentMeter keyEstablishmentMeter;
    private ConnectionMeterMock connection;
    private MeterBusomServiceMock controller;
    private MeterStateContext factory;

    @BeforeEach
    void setUp() {
        curveConfiguration = CurveConfiguration.P192();
        connection = new ConnectionMeterMock();
        controller = new MeterBusomServiceMock();
        factory = new MeterStateContext(curveConfiguration, connection, new ConsumptionRandom(), "");
        keyEstablishmentMeter = factory.makeKeyEstablishment();
        keyEstablishmentMeter.setMeterBusom(controller);
    }

    @Test
    void generateChunksOfPrivateKey() {
        BigInteger value = BigInteger.valueOf(1234567890L);
        keyEstablishmentMeter.setPrivateKey(curveConfiguration.getField().toElement(value));
        List<BigInteger> chunks = keyEstablishmentMeter.generateChunksOfPrivateKey();
        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < chunks.size(); i++) {
            res = res.add(chunks.get(i).multiply(BigInteger.TWO.pow(i * 13)));
        }
        assertEquals(value, res);
    }

    @Test
    void next() throws IOException, NullMessageException {
        keyEstablishmentMeter.next();
        assertEquals(2, controller.getCount());
    }

    public static class MeterBusomServiceMock implements MeterBusomServiceInt {

        private int count;

        public MeterBusomServiceMock() {
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