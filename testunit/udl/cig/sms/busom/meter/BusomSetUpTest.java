package udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.meter.doubles.SenderSpy;
import udl.cig.sms.crypt.LoadCurve;

import java.io.File;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BusomSetUpTest {

    BusomSetUp busomSetUp;
    LoadCurve loadCurve;

    @BeforeEach
    void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        busomSetUp = new BusomSetUp("AESD-123456", loadCurve);
    }

    @Test
    void next() {
        SenderSpy senderSpy = new SenderSpy();
        busomSetUp.setSender(senderSpy);
        BusomState nextState = busomSetUp.next();
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
        assertEquals(Optional.empty(), busomSetUp.getPublicKey());
        busomSetUp.generatePrivateKey();
        busomSetUp.calculatePublicKey();
        Optional<GroupElement> publicKey = busomSetUp.getPublicKey();
        BigInteger privateKey = busomSetUp.getPrivateKey();
        assertEquals(Optional.of(loadCurve.getGroup().getGenerator().pow(privateKey)), publicKey);
    }

    @Test
    void sendPublicKey() {
        SenderSpy sender = new SenderSpy();
        assertEquals(0, sender.getCount());
        busomSetUp.setSender(sender);
        busomSetUp.generatePrivateKey();
        busomSetUp.calculatePublicKey();
        busomSetUp.sendPublicKey();
        assertEquals(1, sender.getCount());
    }

}