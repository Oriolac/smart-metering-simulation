package udl.cig.sms.busom.meter;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.CertificateFalseMock;
import udl.cig.sms.busom.certificate.CertificateTrueMock;
import udl.cig.sms.connection.ReceiverMeter;
import udl.cig.sms.connection.datagram.EndOfDatagram;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.data.LoadCurve;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NeighborhoodSetUpTest {

    static NeighborhoodSetUp neighborhoodSetUp;
    static BigInteger privateKey;
    static LoadCurve loadCurve;

    @BeforeAll
    static void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        privateKey = randomKey();
        neighborhoodSetUp = new NeighborhoodSetUp(privateKey, loadCurve);
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
        neighborhoodSetUp.setReceiverMeter(new ReceiverMeterSpy(loadCurve.getGroup()));
        BusomState nextState = neighborhoodSetUp.next();
        assertTrue(nextState instanceof SendChunk);
        SendChunk state = (SendChunk) nextState;
        assertEquals(privateKey, state.getPrivateKey());
    }

    @Test
    void receivePublicKeysAndCorrectCertificates() throws IOException {
        CertificateTrueMock<String> validation = new CertificateTrueMock<>();
        neighborhoodSetUp.setValidation(validation);
        ReceiverMeterSpy receiver = new ReceiverMeterSpy(loadCurve.getGroup());
        neighborhoodSetUp.setReceiverMeter(receiver);
        neighborhoodSetUp.receivePublicKeysAndCertificates();
        assertEquals(loadCurve.getGroup().getGenerator().pow(BigInteger.valueOf(6)), neighborhoodSetUp.getGeneralKey());
        assertEquals(4, receiver.getCount());
    }

    @Test
    void receivePublicKeysAndFalseCertificates() throws IOException {
        CertificateFalseMock<String> validation = new CertificateFalseMock<>();
        neighborhoodSetUp.setValidation(validation);
        ReceiverMeterSpy receiver = new ReceiverMeterSpy(loadCurve.getGroup());
        neighborhoodSetUp.setReceiverMeter(receiver);
        neighborhoodSetUp.receivePublicKeysAndCertificates();
        assertEquals(loadCurve.getGroup().getNeuterElement(), neighborhoodSetUp.getGeneralKey());
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