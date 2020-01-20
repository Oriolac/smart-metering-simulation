package udl.cig.sms.crypt.unit;

import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import udl.cig.sms.crypt.CypherMessage;
import udl.cig.sms.crypt.Hash;
import udl.cig.sms.crypt.unit.HashTest;

import java.io.File;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class CypherMessageTest implements HashTest {


    CypherMessage cyp;

    @BeforeEach
    void before() {
        cyp = new CypherMessage(new File("./data/p192.toml"));
    }




    @Test
    void generateSij() {
        //noinspection OptionalGetWithoutIsPresent
        BigInteger si = cyp.generateSij().stream()
                .map(x -> BigInteger.TWO.pow(13 * x.getValue()).multiply(x.getKey()))
                .reduce(BigInteger::add).get();
        assertEquals(cyp.getKey(), si);
    }

    @Override
    public Hash getHash() {
        return cyp;
    }
}