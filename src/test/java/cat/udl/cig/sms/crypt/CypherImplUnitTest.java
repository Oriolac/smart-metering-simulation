package cat.udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.data.LoadCurve;

import java.io.File;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CypherImplUnitTest implements HashTest {


    CypherImpl cyp;

    @BeforeEach
    void before() {
        LoadCurve loadCurve = new LoadCurve(new File("./data/p192.toml"));
        cyp = new CypherImpl(loadCurve, BigInteger.TEN);
    }


    @Test
    void encryptTest() {
        GeneralEC curve = cyp.getCurve();
        BigInteger xCoordinate = new BigInteger("2540186706032044856659695301755765373940821598684077640004");
        BigInteger message = new BigInteger("4");
        BigInteger time = new BigInteger("3");
        GeneralECPoint expected = curve.liftX(cyp.getField().toElement(xCoordinate));
        assertEquals(expected, cyp.encrypt(message, time));
    }

    @Override
    public Hash getHash() {
        return cyp;
    }
}