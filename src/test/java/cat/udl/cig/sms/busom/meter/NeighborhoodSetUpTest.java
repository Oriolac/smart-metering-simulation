package cat.udl.cig.sms.busom.meter;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.sms.busom.CertificateFalseMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.busom.BusomState;
import cat.udl.cig.sms.busom.certificate.CertificateTrueMock;
import cat.udl.cig.sms.connection.ReceiverMeter;
import cat.udl.cig.sms.connection.datagram.EndOfDatagram;
import cat.udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NeighborhoodSetUpTest {

    static NeighborhoodSetUp neighborhoodSetUp;
    static BigInteger privateKey;
    static CurveConfiguration curveConfiguration;

    @BeforeAll
    static void setUp() {
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        privateKey = randomKey();
        neighborhoodSetUp = new NeighborhoodSetUp(privateKey, curveConfiguration);
    }

    static BigInteger randomKey() {
        Random random = new Random();
        random.setSeed(10000L);
        long key = random.nextLong();
        return BigInteger.valueOf(key);
    }

    @Test
    void next() throws IOException {
        neighborhoodSetUp.setValidation(new CertificateTrueMock<>());
        neighborhoodSetUp.setReceiverMeter(new ReceiverMeterSpy(curveConfiguration.getGroup()));
        BusomState nextState = neighborhoodSetUp.next();
        assertTrue(nextState instanceof SendChunk);
        SendChunk state = (SendChunk) nextState;
        assertEquals(privateKey, state.getPrivateKey());
    }

    @Test
    void receivePublicKeysAndCorrectCertificates() throws IOException {
        CertificateTrueMock<String> validation = new CertificateTrueMock<>();
        neighborhoodSetUp.setValidation(validation);
        ReceiverMeterSpy receiver = new ReceiverMeterSpy(curveConfiguration.getGroup());
        neighborhoodSetUp.setReceiverMeter(receiver);
        neighborhoodSetUp.receivePublicKeysAndCertificates();
        assertEquals(curveConfiguration.getGroup().getGenerator().pow(BigInteger.valueOf(6)), neighborhoodSetUp.getGeneralKey());
        assertEquals(4, receiver.getCount());
    }

    @Test
    void receivePublicKeysAndFalseCertificates() throws IOException {
        CertificateFalseMock<String> validation = new CertificateFalseMock<>();
        neighborhoodSetUp.setValidation(validation);
        ReceiverMeterSpy receiver = new ReceiverMeterSpy(curveConfiguration.getGroup());
        neighborhoodSetUp.setReceiverMeter(receiver);
        neighborhoodSetUp.receivePublicKeysAndCertificates();
        assertEquals(curveConfiguration.getGroup().getNeuterElement(), neighborhoodSetUp.getGeneralKey());
        assertEquals(4, receiver.getCount());
    }

    public static class ReceiverMeterSpy implements ReceiverMeter {

        private final GeneralECPoint generator;
        private int count;

        public ReceiverMeterSpy(ECPrimeOrderSubgroup group) {
            generator = group.getGenerator();
            this.count = 0;
        }

        @Override
        public SMSDatagram receive() {
            count++;
            if (count < 4)
                return new NeighborhoodDatagram<>(generator.pow(BigInteger.valueOf(count)), "");
            return new EndOfDatagram();
        }

        public int getCount() {
            return count;
        }
    }
}