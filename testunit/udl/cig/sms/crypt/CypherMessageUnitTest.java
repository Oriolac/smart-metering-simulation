package udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CypherMessageUnitTest implements HashTest {


    CypherMessage cyp;

    @BeforeEach
    void before() {
        cyp = new CypherMessage(new File("./data/p192.toml"));
    }


    @Test
    void encryptTest() {
        GeneralEC curve = cyp.getCurve();
        BigInteger xCoordinate = new BigInteger("2540186706032044856659695301755765373940821598684077640004");
        BigInteger message = new BigInteger("4");
        BigInteger time = new BigInteger("3");
        cyp.setPrivateKey(cyp.getField().toElement(BigInteger.TEN));
        GeneralECPoint expected = curve.liftX(cyp.getField().toElement(xCoordinate));
        assertEquals(expected, cyp.encrypt(message, time));
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