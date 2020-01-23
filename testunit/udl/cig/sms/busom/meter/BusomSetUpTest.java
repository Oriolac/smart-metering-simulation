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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusomSetUpTest {

    BusomSetUp busomSetUp;
    LoadCurve loadCurve;

    @BeforeEach
    void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        busomSetUp = new BusomSetUp("AESD-123456", loadCurve.getGroup().getGenerator());
    }

    @Test
    void next() {
        BusomState nextState = busomSetUp.next();
        assertTrue(nextState instanceof NeighborhoodSetUp);
    }


    @Test
    void generatePrivateKey() {
        assertTrue(busomSetUp.generatePrivateKey().compareTo(BigInteger.ZERO) > 0);
    }

    @Test
    void calculatePublicKey() {
        assertEquals(Optional.empty(), busomSetUp.calculatePublicKey());
        BigInteger privateKey = busomSetUp.generatePrivateKey();
        Optional<GroupElement> publicKey = busomSetUp.calculatePublicKey();
        assertEquals(Optional.of(loadCurve.getGroup().getGenerator().pow(privateKey)), publicKey);
    }

    @Test
    void sendPublicKey() {
        SenderSpy sender = new SenderSpy();
        assertEquals(0,sender.getCount());
        busomSetUp.setSender(sender);
        busomSetUp.generatePrivateKey();
        busomSetUp.calculatePublicKey();
        busomSetUp.sendPublicKey();
        assertEquals(1,sender.getCount());
    }

}