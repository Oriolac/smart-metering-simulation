package cat.udl.cig.sms.busom.meter;

import cat.udl.cig.cryptography.cryptosystems.HomomorphicCypher;
import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.Group;
import cat.udl.cig.fields.GroupElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.busom.meter.doubles.SenderSpy;
import cat.udl.cig.sms.busom.BusomMeterState;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.busom.data.MeterKey;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class SendChunkTest {

    BigInteger privateKey;
    SendChunk sendChunk;
    static final BigInteger message = BigInteger.valueOf(13);
    CurveConfiguration curveConfiguration;

    @BeforeEach
    void setUp() {
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        privateKey = new BigInteger("1234567890");
        MeterKey meterKey = new MeterKey(privateKey, curveConfiguration.getGroup().getRandomElement());
        sendChunk = new SendChunk(meterKey, curveConfiguration);
    }

    @Test
    void next() throws NullMessageException, IOException {
        assertThrows(NullMessageException.class, () -> sendChunk.next());
        sendChunk.setMessage(message);
        SenderSpy sender = new SenderSpy();
        sendChunk.setSender(sender);
        BusomMeterState nextState = sendChunk.next();
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
    void sendCypherText() throws IOException {
        SenderSpy sender = new SenderSpy();
        CypherSpy cypher = new CypherSpy();
        sendChunk.setMessage(message);
        sendChunk.setSender(sender);
        sendChunk.setCypher(cypher);
        sendChunk.sendCypherText();
        assertEquals(0, cypher.getCount());
        assertEquals(1, sender.getCount());
    }

    static class CypherSpy implements HomomorphicCypher {

        private int count;

        public CypherSpy() {
            this.count = 0;
        }


        public int getCount() {
            return count;
        }

        @Override
        public GroupElement getPublicKey() {
            return null;
        }

        @Override
        public GroupElement getGenerator() {
            return null;
        }

        @Override
        public Group getGroup() {
            return null;
        }

        @Override
        public HomomorphicCiphertext encrypt(GroupElement message) {
            count++;
            return null;
        }

        @Override
        public HomomorphicCiphertext encrypt(GroupElement groupElement, BigInteger bigInteger) {
            return null;
        }
    }

}