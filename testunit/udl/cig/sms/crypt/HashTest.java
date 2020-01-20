package udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralEC;
import org.junit.jupiter.api.Test;
import udl.cig.sms.crypt.Hash;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public interface HashTest {

    Hash getHash();

    @Test
    default void hashTest() {
        GeneralEC curve = getHash().getCurve();
        for (int i = 0; i < 100000; i += 100) {
            assertTrue(curve.isOnCurve(getHash().hash(BigInteger.valueOf(i))));
        }
    }
}
