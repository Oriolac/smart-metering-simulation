package cat.udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.busom.meter.doubles.SenderSpy;
import cat.udl.cig.sms.busom.BusomMeterState;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class BusomSetUpTest {

    BusomSetUp busomSetUp;
    CurveConfiguration curveConfiguration;

    @BeforeEach
    void setUp() {
        curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
        busomSetUp = new BusomSetUp("AESD-123456", curveConfiguration);
    }

    @Test
    void next() throws IOException {
        SenderSpy senderSpy = new SenderSpy();
        busomSetUp.setSender(senderSpy);
        BusomMeterState nextState = busomSetUp.next();
        assertTrue(nextState instanceof NeighborhoodSetUp);
    }


    @Test
    void generatePrivateKey() {
        busomSetUp.generatePrivateKey();
        BigInteger random = busomSetUp.getPrivateKey();
        busomSetUp.generatePrivateKey();
        BigInteger random2 = busomSetUp.getPrivateKey();
        assertNotEquals(random, random2, "Randoms are equals!");
    }

    @Test
    void calculatePublicKey() {
        busomSetUp.calculatePublicKey();
        assertNull(busomSetUp.getPublicKey());
        busomSetUp.generatePrivateKey();
        busomSetUp.calculatePublicKey();
        GroupElement publicKey = busomSetUp.getPublicKey();
        BigInteger privateKey = busomSetUp.getPrivateKey();
        assertEquals(curveConfiguration.getGroup().getGenerator().pow(privateKey), publicKey);
    }

    @Test
    void sendPublicKey() throws IOException {
        SenderSpy sender = new SenderSpy();
        assertEquals(0, sender.getCount());
        busomSetUp.setSender(sender);
        busomSetUp.generatePrivateKey();
        busomSetUp.calculatePublicKey();
        busomSetUp.sendPublicKey();
        assertEquals(1, sender.getCount());
    }

}