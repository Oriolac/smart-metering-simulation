package udl.cig.sms.busom.meter;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.CertificateFalseMock;
import udl.cig.sms.busom.CertificateTrueMock;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.SMSDatagram;
import udl.cig.sms.connection.datagram.EndOfDatagram;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import udl.cig.sms.crypt.LoadCurve;

import java.io.File;
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
        neighborhoodSetUp = new NeighborhoodSetUp(randomKey(), loadCurve);
    }

    static BigInteger randomKey() {
        Random random = new Random();
        random.setSeed(10000L);
        long key = random.nextLong();
        return BigInteger.valueOf(key);
    }

    @Test
    void next() {
        neighborhoodSetUp.setValidation(new CertificateTrueMock<>());
        neighborhoodSetUp.setReceiver(new ReceiverSpy(loadCurve.getGroup()));
        BusomState nextState = neighborhoodSetUp.next();
        assertTrue(nextState instanceof SendChunk);
        SendChunk state = (SendChunk) nextState;
        assertEquals(privateKey, state.getPrivateKey());
    }

    @Test
    void receivePublicKeysAndCorrectCertificates() {
        CertificateTrueMock<String> validation = new CertificateTrueMock<>();
        neighborhoodSetUp.setValidation(validation);
        ReceiverSpy receiver = new ReceiverSpy(loadCurve.getGroup());
        neighborhoodSetUp.setReceiver(receiver);
        neighborhoodSetUp.receivePublicKeysAndCertificates();
        assertEquals(loadCurve.getGroup().getGenerator().pow(BigInteger.valueOf(6)), neighborhoodSetUp.getGeneralKey());
        assertEquals(4, receiver.getCount());
    }

    @Test
    void receivePublicKeysAndFalseCertificates() {
        CertificateFalseMock<String> validation = new CertificateFalseMock<>();
        neighborhoodSetUp.setValidation(validation);
        ReceiverSpy receiver = new ReceiverSpy(loadCurve.getGroup());
        neighborhoodSetUp.setReceiver(receiver);
        neighborhoodSetUp.receivePublicKeysAndCertificates();
        assertEquals(loadCurve.getGroup().getNeuterElement(), neighborhoodSetUp.getGeneralKey());
        assertEquals(4, receiver.getCount());
    }

    public static class ReceiverSpy implements Receiver {

        private final GeneralECPoint generator;
        private int count;

        public ReceiverSpy(ECPrimeOrderSubgroup group) {
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