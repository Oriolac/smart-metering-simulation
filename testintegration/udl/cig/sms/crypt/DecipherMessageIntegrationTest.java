package udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DecipherMessageIntegrationTest {

    static DecipherMessage dec;
    static List<BigInteger> mis;
    static List<BigInteger> sis;
    static List<GeneralECPoint> cis;
    static BigInteger t = BigInteger.TEN;
    static BigInteger message = new BigInteger("9");
    static BigInteger si;
    static BigInteger s0;
    static GeneralECPoint ci;
    static CypherMessage cyp;
    static BigInteger order;

    @BeforeAll
    static void createDecipher() {
        dec = new DecipherMessage(new File("./data/p192.toml"));
        createSecretKeys();
        createMessage();
        cyp = new CypherMessage(new File("./data/p192.toml"));
        keyEstablishment();
    }

    static void createMessage() {
        mis = new LinkedList<>();
        for (int i = 0; i < 1; i++) {
            mis.add(randomMessage());
        }
    }

    static void createSecretKeys() {
        sis = new LinkedList<>();
        for (int i = 0; i < 1; i++) {
            sis.add(randomMessage());
        }
    }

    static void keyEstablishment() {
        cis = new LinkedList<>();
        order = dec.getGroup().getSize();
        //noinspection OptionalGetWithoutIsPresent
        si = cyp.generateSij().stream()
                .map(x -> BigInteger.TWO.pow(13 * x.getValue()).multiply(x.getKey()))
                .reduce(BigInteger::add).get();
        s0 = si.remainder(order);
        ci = cyp.encrypt(mis.get(0), t);
        cis.add(ci);
        s0 = s0.negate().add(order).remainder(order);
        dec.setS0(s0);

    }


    static BigInteger randomMessage() {
        return message;
    }

    @Test
    void decrypt() {
        Optional<BigInteger> m = dec.decrypt(cis, t);
        Optional<BigInteger> mExpected = mis.stream().reduce(BigInteger::add);
        assertEquals(mExpected, m);
    }

    @Test
    void isS0Correct() {
        assertEquals(BigInteger.ZERO, si.add(s0).remainder(order));
    }


    @Test
    void isSameAlphaBetaInDecryptFunction() {
        assertEquals(Optional.of(dec.getGroup().getGenerator().pow(message)), dec.getBeta(cis, t));
        GeneralECPoint expectedGenerator = dec.getGroup()
                .toElement(new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16));
        assertEquals(expectedGenerator , dec.getGroup().getGenerator());
    }

}