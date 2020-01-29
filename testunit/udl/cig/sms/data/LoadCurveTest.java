package udl.cig.sms.data;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.fields.PrimeFieldElement;
import com.moandjiezana.toml.Toml;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoadCurveTest {

    static LoadCurve loadCurve;

    @BeforeAll
    static void setUp() {
        loadCurve = new LoadCurve(new File("./data/p192.toml"));
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
        assertEquals(g, loadCurve.getGroup());

    }
}
