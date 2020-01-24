package udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.meter.doubles.SenderSpy;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.SMSDatagram;
import udl.cig.sms.connection.datagram.CipherTextDatagram;
import udl.cig.sms.connection.datagram.EndOfDatagram;
import udl.cig.sms.crypt.LoadCurve;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReceiveChunkTest {

    LoadCurve loadCurve;
    ReceiveChunk currentState;

    @BeforeEach
    void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        currentState = new ReceiveChunk(loadCurve.getGroup());
    }

    @Test
    void next() {
        BusomState nextState = currentState.next();
        assertTrue(nextState instanceof DecriptChunk);
    }

    @Test
    void receiveAndCompute() {
        ReceiverSpy receiver = new ReceiverSpy();
        ElGamalCiphertext expected = new ElGamalCiphertext(null); //TODO: @ori s'ha de mirar al Sagemath3
        currentState.setReceiver(receiver);
        currentState.receiveAndCompute();
        assertEquals(3, receiver.getCount());
        ElGamalCiphertext ciphertext = (ElGamalCiphertext) currentState.getCiphertext();
        assertEquals(expected, ciphertext);
    }

    @Test
    void sendC() {
        SenderSpy senderSpy = new SenderSpy();
        currentState.setSender(senderSpy);
        ElGamalCiphertext ciphertext = new ElGamalCiphertext(null); //TODO: @ori s'ha de mirar al Sagemath3
        currentState.setCipherText(ciphertext);
        currentState.sendC();
        assertEquals(3, senderSpy.getCount());
    }

    protected static class ReceiverSpy implements Receiver {


        private int count;

        protected ReceiverSpy() {
            this.count = 0;
        }

        @Override
        public SMSDatagram receive() {
            count++;
            if (count < 4)
                return new CipherTextDatagram(null); //TODO: null must be an instance of cyphertext
            return new EndOfDatagram(); //TODO : @sergisi missing number of smart meters.
        }

        public int getCount() {
            return count;
        }
    }
}