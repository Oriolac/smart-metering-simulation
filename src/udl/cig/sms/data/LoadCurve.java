package udl.cig.sms.data;

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


/**
 * Loads curve from a TOML file
 */
public class LoadCurve {

    final static int A = -3;
    private ECPrimeOrderSubgroup grup;
    private PrimeField field;
    private GeneralEC curve;

    /**
     * Loads toml file
     *
     * @param file TOML file containing curve
     */
    public LoadCurve(File file) {
        Toml toml = new Toml().read(file);
        BigInteger module = new BigInteger(toml.getString("p"));
        BigInteger n = new BigInteger(toml.getString("n"));
        BigInteger b = new BigInteger(getTrimmedString(toml, "b"), 16);
        BigInteger gx = new BigInteger(getTrimmedString(toml, "gx"), 16);
        BigInteger gy = new BigInteger(getTrimmedString(toml, "gy"), 16);

        curveConstruction(module, b, n);
        groupConstruction(n, gx, gy);
    }

    private void groupConstruction(BigInteger n, BigInteger gx, BigInteger gy) {
        GeneralECPoint gen;
        gen = new GeneralECPoint(curve, new PrimeFieldElement(field, gx), new PrimeFieldElement(field, gy));
        this.grup = new ECPrimeOrderSubgroup(curve, n, gen);
    }

    private void curveConstruction(BigInteger module, BigInteger b, BigInteger order) {
        RingElement[] COEF = new RingElement[2];
        ArrayList<BigInteger> card = new ArrayList<>();

        this.field = new PrimeField(module);
        COEF[0] = new PrimeFieldElement(field, BigInteger.valueOf(A));
        COEF[1] = new PrimeFieldElement(field, b);
        card.add(order);
        curve = new GeneralEC(field, COEF, card);
    }

    private String getTrimmedString(Toml toml, String key) {
        return toml.getString(key)
                .replaceAll("\\s", "");
    }

    /**
     * @return group
     */
    public ECPrimeOrderSubgroup getGroup() {
        return grup;
    }

    /**
     * @return field
     */
    public PrimeField getField() {
        return field;
    }

    /**
     * @return curve
     */
    public GeneralEC getCurve() {
        return curve;
    }

    /**
     * Loads p-192 curve. As it's widely used for tests and production,
     * we give it a special method to load it
     *
     * @return LoadCurve of p-192
     */
    public static LoadCurve P192() {
        return new LoadCurve(new File("./data/p192.toml"));
    }
}
