package udl.cig.sms.busom.meter;

import cat.udl.cig.cryptography.cryptosystems.HomomorphicCypher;
import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.Group;
import cat.udl.cig.fields.GroupElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.busom.meter.doubles.SenderSpy;
import udl.cig.sms.crypt.LoadCurve;

import java.io.File;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class SendChunkTest {

    BigInteger privateKey;
    SendChunk sendChunk;
    static final BigInteger message = BigInteger.valueOf(13);
    LoadCurve loadCurve;

    @BeforeEach
    void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        privateKey = new BigInteger("1234567890");
        sendChunk = new SendChunk(privateKey);
    }

    @Test
    void next() throws NullMessageException {
        assertThrows(NullMessageException.class, () -> sendChunk.next());
        sendChunk.setMessage(message);
        BusomState nextState = sendChunk.next();
        assertTrue(nextState instanceof SendPartialDecryption);
    }

    @Test
    void generateNoise() {
        BigInteger noise1 = SendChunk.generateNoise();
        assertNotNull(noise1);
        BigInteger noise2 = SendChunk.generateNoise();
        assertNotNull(noise2);
        assertNotEquals(noise1, noise2);
    }

    @Test
    void sendCypherText() {
        GroupElement generator = loadCurve.getGroup().getGenerator();
        SenderSpy sender = new SenderSpy();
        CypherSpy cypher = new CypherSpy();
        sendChunk.setMessage(message);
        sendChunk.setSender(sender);
        sendChunk.setCypher(cypher);
        sendChunk.sendCypherText();
        assertEquals(1, sender.getCount());
        assertEquals(1, cypher.getCount());
    }

    static class CypherSpy implements HomomorphicCypher {

        private int count;

        public CypherSpy() {
            this.count = 0;
        }

        @Override
        public HomomorphicCiphertext encrypt(Object o) {
            count++;
            return null;
        }

        public int getCount() {
            return count;
        }

        @Override
        public Object getPublicKey() {
            return null;
        }

        @Override
        public Object getGenerator() {
            return null;
        }

        @Override
        public Group getGroup() {
            return null;
        }

        @Override
        public HomomorphicCiphertext encrypt(Object o, BigInteger bigInteger) {
            return null;
        }
    }

}