package udl.cig.sms.crypt.integration;

import cat.udl.cig.ecc.GeneralECPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import udl.cig.sms.crypt.CypherMessage;
import udl.cig.sms.crypt.DecipherMessage;

import java.io.File;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DecipherMessageTest {

    DecipherMessage dec;
    List<BigInteger> mis;
    List<BigInteger> sis;
    List<GeneralECPoint> cis;
    BigInteger t = BigInteger.TEN;
    BigInteger m;

    @BeforeEach
    void createDecipher() {
        dec = new DecipherMessage(new File("./data/p192.toml"));
        sis = new LinkedList<>();
        for (int i = 0; i < 1; i++) {
            sis.add(randomMessage());
        }
        mis = new LinkedList<>();
        for (int i = 0; i < 1; i++) {
            mis.add(randomMessage());
        }
    }

    BigInteger randomMessage() {
        Random r = new Random();
        return BigInteger.valueOf(4);
    }


    @Test
    void decrypt() {
        CypherMessage cyp = new CypherMessage(new File("./data/p192.toml"));
        BigInteger si;
        BigInteger s0 = BigInteger.ZERO;
        GeneralECPoint ci;
        cis = new LinkedList<>();
        BigInteger order = dec.getGroup().getSize();
        //noinspection OptionalGetWithoutIsPresent
        si = cyp.generateSij().stream()
                .map(x -> BigInteger.TWO.pow(13 * x.getValue()).multiply(x.getKey()))
                .reduce(BigInteger::add).get();
        s0 = s0.add(si).remainder(order);
        ci = cyp.encrypt(mis.get(0), t);
        cis.add(ci);
        s0 = s0.negate().add(order).remainder(order);
        dec.setS0(s0);
        Optional<BigInteger> m = dec.decrypt(cis, t);
        Optional<BigInteger> mExpected = mis.stream().reduce(BigInteger::add);
        assertEquals(mExpected, m);
    }

}