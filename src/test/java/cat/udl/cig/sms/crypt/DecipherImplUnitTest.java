package cat.udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.operations.wrapper.HashedAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

import static java.math.BigInteger.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecipherImplUnitTest implements HashTest {

    DecipherImpl dec;
    final CurveConfiguration curveConfiguration = new CurveConfiguration(new File("./data/p192.toml"));
    CypherImpl cyp;
    List<BigInteger> mis;
    List<GeneralECPoint> cis;
    BigInteger t = BigInteger.TEN;

    @BeforeEach
    void setUp() {
        BigInteger si;
        BigInteger s0 = BigInteger.ZERO;
        mis = new ArrayList<>(Collections.singletonList(valueOf(4)));
        cis = new LinkedList<>();
        BigInteger order = curveConfiguration.getGroup().getSize();
        si = generateSi().getIntValue();
        s0 = s0.add(si).remainder(order);

        s0 = s0.negate().add(order).remainder(order);
        HashedAlgorithm.loadHashedInstance(curveConfiguration.getGroup().getGenerator(), BigInteger.valueOf(1024 * 1024),
                BigInteger.valueOf(32));
        dec = new DecipherImpl(curveConfiguration, s0);
        cyp = new CypherImpl(curveConfiguration, si);
    }


    @Override
    public Hash getHash() {
        return dec;
    }

    @Test
    public void decryptUsingMockLambda() {
        assertEquals(curveConfiguration.getGroup().getGenerator(), dec.getLambda().getAlpha());
        GeneralECPoint ci = cyp.encrypt(mis.get(0), t);
        cis.add(ci);
        assertEquals(Optional.of(curveConfiguration.getGroup().getGenerator().pow(valueOf(4L))), dec.getBeta(cis, t));
        Optional<BigInteger> m; //= dec.decrypt(cis, t);
        m = dec.getLambda().algorithm(dec.getGroup().getGenerator().pow(valueOf(4L)));
        Optional<BigInteger> mExpected = mis.stream().reduce(BigInteger::add);
        assertEquals(mExpected, m);
    }

    @Test
    public void getBeta() {
        List<GeneralECPoint> points = new ArrayList<>();
        GeneralECPoint generator = curveConfiguration.getGroup().getGenerator();
        BigInteger time = BigInteger.ONE;
        List<Long> misint = new ArrayList<>(List.of(56L, 60L));
        List<GeneralECPoint> cis = new ArrayList<>();
        misint.forEach(x -> cis.add(cyp.encrypt(BigInteger.valueOf(x), time)));
        Optional<GeneralECPoint> CExpected = cis.stream().reduce(GeneralECPoint::multiply);
        assertEquals(CExpected.map((c -> c.multiply(dec.hash(time).pow(dec.getPrivateKey())))),
                dec.getBeta(cis, time));
    }

    private PrimeFieldElement generateSi() {
        return curveConfiguration.getField().getRandomElement();

    }
}