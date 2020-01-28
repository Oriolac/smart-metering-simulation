package udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.data.MeterKey;
import udl.cig.sms.busom.meter.doubles.SenderSpy;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;
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
    ReceiverSpy receiver;
    GroupElement generator;
    SenderSpy sender;

    @BeforeEach
    void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        privateKey = new BigInteger("1234567890");
        noise = BigInteger.valueOf(13000L);
        MeterKey meterKey = new MeterKey(privateKey, loadCurve.getGroup().getRandomElement());
        currentState = new SendPartialDecryption(meterKey, noise, loadCurve);
        generator = loadCurve.getGroup().getGenerator();
        receiver = new ReceiverSpy(generator);
        currentState.setReceiver(receiver);
        sender = new SenderSpy();
        currentState.setSender(sender);
    }

    @Test
    void next() {
        BusomState nextState = currentState.next();
        assertTrue(nextState instanceof SendChunk);
    }

    @Test
    void generatePartialDecryption() {
        GroupElement expected = generator.pow(privateKey.add(noise));
        assertEquals(expected, currentState.generatePartialDecryption());
        assertEquals(1, receiver.getCount());
    }

    @Test
    void sendDecryption() {
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
        public SMSDatagram receive() {
            count++;
            return new GroupElementDatagram(element);
        }

        public int getCount() {
            return count;
        }
    }
}