package cat.udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.operations.wrapper.PollardsLambda;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cat.udl.cig.sms.crypt.CypherMessage;
import cat.udl.cig.sms.crypt.DecipherMessage;
import cat.udl.cig.sms.data.LoadCurve;

import java.io.File;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecipherMessageIntegrationTest {

    static final int N = 3;
    static DecipherMessage dec;
    static List<BigInteger> sis;
    static List<GeneralECPoint> cis;
    static BigInteger t = BigInteger.ONE;
    static BigInteger message = new BigInteger("9");
    static BigInteger s0;
    static BigInteger order;
    static LoadCurve loadCurve;

    @BeforeAll
    static void createDecipher() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
        createSecretKeys();
        keyEstablishment();
        dec = new DecipherMessage(loadCurve, s0);
        dec.setLambda(new PollardsLambda(loadCurve.getGroup().getGenerator()));
    }

    static void createSecretKeys() {
        sis = new LinkedList<>();
        for (int i = 0; i < N; i++) {
            sis.add(randomMessage());
        }
    }

    static void keyEstablishment() {
        cis = new LinkedList<>();
        order = loadCurve.getGroup().getSize();
        s0 = sis.stream().reduce(BigInteger::add).map(s0 -> s0.remainder(order)).get();
        cis = sis.stream()
                .map((privateKey) -> new CypherMessage(loadCurve, privateKey))
                .map((cyper) -> cyper.encrypt(message, t)).collect(Collectors.toList());
        s0 = s0.negate().add(order).remainder(order);
    }


    static BigInteger randomMessage() {
        return message;
    }

    @Test
    void decrypt() {
        Optional<BigInteger> m = dec.decrypt(cis, t);
        Optional<BigInteger> mExpected = Optional.of(message.multiply(BigInteger.valueOf(N)));
        assertEquals(mExpected, m);
    }

    @Test
    void isS0Correct() {
        assertEquals(Optional.of(BigInteger.ZERO),
                sis.stream().reduce(BigInteger::add).map((si) -> si.add(s0).remainder(order)));
    }


    @Test
    void isSameAlphaBetaInDecryptFunction() {
        Optional<GeneralECPoint> beta =
                Optional.of(dec.getGroup().getGenerator().pow(message.multiply(BigInteger.valueOf(N))));
        assertEquals(beta, dec.getBeta(cis, t));
        GeneralECPoint expectedGenerator = dec.getGroup()
                .toElement(new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16));
        assertEquals(expectedGenerator, dec.getGroup().getGenerator());
    }


    static private PrimeFieldElement generateSi() {
        return loadCurve.getField().getRandomElement();

    }
}