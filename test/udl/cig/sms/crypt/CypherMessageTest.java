package udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class CypherMessageTest {


    CypherMessage cyp;

    @BeforeEach
    void before() {
        cyp = new CypherMessage(new File("./data/p192.toml"));
    }

    @Test
    void hashTest() {
        GeneralEC curve = cyp.getCurve();
        for(int i = 0; i < 100000; i++) {
            assertTrue(curve.isOnCurve(cyp.hash(BigInteger.valueOf(i))));
        }
    }


    @Test
    void generateSij() {
        //noinspection OptionalGetWithoutIsPresent
        BigInteger si = cyp.generateSij().stream()
                .map(x -> BigInteger.TWO.pow(13 * x.getValue()).multiply(x.getKey()))
                .reduce(BigInteger::add).get();
        assertEquals(cyp.getKey(), si);
    }
}