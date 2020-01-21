package udl.cig.sms.crypt;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.operations.wrapper.PollardsLambdaInt;
import com.moandjiezana.toml.Toml;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecipherMessageUnitTest implements HashTest{

    DecipherMessage dec;

    @BeforeEach
    void setUp() {
        dec = new DecipherMessage(new File("./data/p192.toml"));
    }

    @Test
    void isSameGroup() {
        File file = new File("./data/p192.toml");
        Toml toml = new Toml().read(file);
        BigInteger MODULE_ECC = new BigInteger(toml.getString("p"));
        BigInteger n = new BigInteger(toml.getString("n"));
        BigInteger b = new BigInteger(toml.getString("b").replaceAll("\\s", ""), 16);
        BigInteger gx = new BigInteger(toml.getString("gx")
                .replaceAll("\\s", ""), 16);
        BigInteger gy = new BigInteger(toml.getString("gy")
                .replaceAll("\\s", ""), 16);
        PrimeField ring = new PrimeField(MODULE_ECC);

        PrimeFieldElement[] COEF = new PrimeFieldElement[2];
        COEF[0] = new PrimeFieldElement(ring, BigInteger.valueOf(-3));
        COEF[1] = new PrimeFieldElement(ring, b);
        ArrayList<BigInteger> card = new ArrayList<>();
        card.add(n);
        GeneralEC curve = new GeneralEC(ring, COEF, card);
        GeneralECPoint gen = new GeneralECPoint(curve, new PrimeFieldElement(ring, gx), new PrimeFieldElement(ring, gy));
        ECPrimeOrderSubgroup g = new ECPrimeOrderSubgroup(curve, n, gen);
        assertEquals(g, dec.getGroup());

    }

    @Override
    public Hash getHash() {
        return dec;
    }

    @Test
    public void decryptUsingMockLambda() {
        PollardsLambdaMock lambda = new PollardsLambdaMock();
        dec.setLambda(lambda);
        CypherMessage cyp = new CypherMessage(new File("./data/p192.toml"));
        BigInteger si;
        BigInteger t = BigInteger.TEN;
        BigInteger s0 = BigInteger.ZERO;
        GeneralECPoint ci;
        List<BigInteger> mis = new ArrayList<>(Collections.singletonList(BigInteger.valueOf(4)));
        List<GeneralECPoint> cis = new LinkedList<>();
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
        assertEquals(Optional.of(dec.getGroup().getGenerator().pow(BigInteger.valueOf(4L))), dec.getBeta(cis, t));
        Optional<BigInteger> m = dec.decrypt(cis, t);
        Optional<BigInteger> mExpected = mis.stream().reduce(BigInteger::add);
        assertEquals(mExpected, m);
    }


    private static class PollardsLambdaMock implements PollardsLambdaInt{

        @Override
        public Optional<BigInteger> algorithm(GroupElement groupElement) throws ArithmeticException {
            return Optional.of(BigInteger.valueOf(4L));
        }

    }



}
