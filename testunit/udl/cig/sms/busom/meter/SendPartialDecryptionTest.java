package udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.meter.doubles.SenderSpy;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.SMSDatagram;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.crypt.LoadCurve;

import java.io.File;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SendPartialDecryptionTest {

    SendPartialDecryption currentState;
    BigInteger privateKey;
    BigInteger noise;
    LoadCurve loadCurve;

    @BeforeEach
    void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        privateKey = new BigInteger("1234567890");
        noise = BigInteger.valueOf(13000L);
        currentState = new SendPartialDecryption(privateKey, noise);
    }

    @Test
    void next() {
        BusomState nextState = currentState.next();
        assertTrue(nextState instanceof SendChunk);
    }

    @Test
    void generatePartialDecryption() {
        GroupElement generator = loadCurve.getGroup().getGenerator();
        ReceiverSpy receiver = new ReceiverSpy(generator);
        currentState.setReceiver(receiver);
        GroupElement expected = generator.pow(privateKey.add(noise));
        assertEquals(expected, currentState.generatePartialDecryption());
        assertEquals(1, receiver.getCount());
    }

    @Test
    void sendDecryption() {
        SenderSpy sender = new SenderSpy();
        currentState.setSender(sender);
        currentState.sendDecryption();
        assertEquals(1, sender.getCount());
    }

    static class ReceiverSpy implements Receiver {

        private final GroupElement element;
        private int count;

        protected ReceiverSpy(GroupElement element) {
            this.element = element;
            count = 0;
        }

        @Override
        public SMSDatagram receive(byte[] data) {
            count++;
            return new GroupElementDatagram(element);
        }

        public int getCount() {
            return count;
        }
    }
}