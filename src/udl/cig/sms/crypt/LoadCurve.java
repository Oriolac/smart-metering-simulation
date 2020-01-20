package udl.cig.sms.crypt;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.fields.RingElement;
import com.moandjiezana.toml.Toml;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;

public abstract class LoadCurve {

    final static int A = -3;
    protected ECPrimeOrderSubgroup grup;
    protected PrimeField field;
    protected GeneralEC curve;

    protected void loadCurve(File file) {
        RingElement[] COEF = new RingElement[2];
        ArrayList<BigInteger> card = new ArrayList<>();
        GeneralECPoint gen;

        Toml toml = new Toml().read(file);
        BigInteger module = new BigInteger(toml.getString("p"));
        BigInteger n = new BigInteger(toml.getString("n"));
        BigInteger b = new BigInteger(toml.getString("b")
                .replaceAll("\\s", ""), 16);
        BigInteger gx = new BigInteger(toml.getString("gx")
                .replaceAll("\\s", ""), 16);
        BigInteger gy = new BigInteger(toml.getString("gy")
                .replaceAll("\\s", ""), 16);

        this.field = new PrimeField(module);
        COEF[0] = new PrimeFieldElement(field, BigInteger.valueOf(A));
        COEF[1] = new PrimeFieldElement(field, b);
        card.add(n);
        curve = new GeneralEC(field, COEF, card);
        gen = new GeneralECPoint(curve, new PrimeFieldElement(field, gx), new PrimeFieldElement(field, gy));
        this.grup = new ECPrimeOrderSubgroup(curve, n, gen);
    }

}
