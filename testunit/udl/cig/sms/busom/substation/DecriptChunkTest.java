package udl.cig.sms.busom.substation;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.SMSDatagram;
import udl.cig.sms.connection.datagram.EndOfDatagram;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.crypt.LoadCurve;

import java.io.File;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecriptChunkTest {

    DecriptChunk currentState;
    LoadCurve loadCurve;

    @BeforeEach
    void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        currentState = new DecriptChunk(loadCurve.getGroup());
    }

    @Test
    void next() {
        BusomState nextState = currentState.next();
        assertTrue(nextState instanceof ReceiveChunk);
    }

    @Test
    void receiveAndCompute() {
        ReceiverSpy receiverSpy = new ReceiverSpy(loadCurve.getGroup());
        currentState.setReceiver(receiverSpy);
        currentState.receiveAndCompute();
        assertEquals(3, receiverSpy.getCount());
    }

    @Test
    void readMessage() {
        GeneralECPoint generator = loadCurve.getGroup().getGenerator();
        BigInteger message = BigInteger.valueOf(384329L);
        GeneralECPoint partialDecryption = generator.pow(message);
        currentState.setPartialDecryption(partialDecryption);
        assertEquals(message, currentState.readMessage());
    }

    private static class ReceiverSpy implements Receiver {

        private int count;
        ECPrimeOrderSubgroup group;

        private ReceiverSpy(ECPrimeOrderSubgroup group) {
            count = 0;
            this.group = group;
        }

        @Override
        public SMSDatagram receive(byte[] data) {
            count++;
            if(count < 4)
                return new GroupElementDatagram(group.getGenerator());
            return new EndOfDatagram(); //TODO @ori must be a throw new ....
        }

        public int getCount() {
            return count;
        }
    }
}