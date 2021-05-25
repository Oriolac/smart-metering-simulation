package cat.udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.busom.meter.doubles.SenderSpy;
import cat.udl.cig.sms.busom.BusomState;
import cat.udl.cig.sms.busom.data.MeterKey;
import cat.udl.cig.sms.busom.meter.SendChunk;
import cat.udl.cig.sms.busom.meter.SendPartialDecryption;
import cat.udl.cig.sms.connection.ReceiverMeter;
import cat.udl.cig.sms.connection.datagram.GroupElementDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.data.LoadCurve;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SendPartialDecryptionTest {

    SendPartialDecryption currentState;
    BigInteger privateKey;
    BigInteger noise;
    LoadCurve loadCurve;
    ReceiverMeterSpy receiver;
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
        receiver = new ReceiverMeterSpy(generator);
        currentState.setReceiverMeter(receiver);
        sender = new SenderSpy();
        currentState.setSender(sender);
    }

    @Test
    void next() throws IOException {
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
    void sendDecryption() throws IOException {
        currentState.sendDecryption();
        assertEquals(1, sender.getCount());
    }

    static class ReceiverMeterSpy implements ReceiverMeter {

        private final GroupElement element;
        private int count;

        protected ReceiverMeterSpy(GroupElement element) {
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