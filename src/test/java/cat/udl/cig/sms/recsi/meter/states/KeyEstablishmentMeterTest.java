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
    private MeterBusomServiceMock busomService;
    private MeterStateContext context;

    @BeforeEach
    void setUp() throws IOException, NullMessageException {
        curveConfiguration = CurveConfiguration.P192();
        connection = new ConnectionMeterMock();
        busomService = new MeterBusomServiceMock();
        context = new MeterStateContext(curveConfiguration, connection, new ConsumptionRandom(), "");
        keyEstablishmentMeter = context.makeKeyEstablishment();
        keyEstablishmentMeter.setBusomService(busomService);
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
        assertEquals(0, connection.getCountReceive());
        assertEquals(2, connection.getCountSend());
    }

    @Test
    void next() throws IOException, NullMessageException {
        keyEstablishmentMeter.next();
        assertEquals(2, busomService.getCount());
    }

    @Test
    void assertCounts() {
        assertEquals(0, connection.getCountReceive());
        assertEquals(2, connection.getCountSend());
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

        private int countReceive = 0;
        private int countSend = 0;

        @Override
        public SMSDatagram receive() throws IOException {
            countReceive += 1;
            return null;
        }

        @Override
        public void send(SMSDatagram data) throws IOException {
            countSend += 1;
        }

        @Override
        public void close() throws IOException {

        }

        public int getCountReceive() {
            return countReceive;
        }

        public int getCountSend() {
            return countSend;
        }
    }
}