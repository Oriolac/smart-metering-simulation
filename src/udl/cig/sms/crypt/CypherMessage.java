package udl.cig.sms.crypt;

import cat.udl.cig.cryptography.hashes.SHA1;
import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.fields.RingElement;
import com.moandjiezana.toml.Toml;
import javafx.util.Pair;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class CypherMessage implements Cypher{

    private ECPrimeOrderSubgroup grup;
    final static int A = -3;
    private BigInteger privateKey;  // Si in formulas
    private PrimeField field;
    private GeneralEC curve;

    public CypherMessage(File file) {
        RingElement[] COEF = new RingElement[2];
        ArrayList<BigInteger> card = new ArrayList<>();
        GeneralECPoint gen;

        Toml toml = new Toml().read(file);
        BigInteger module = new BigInteger(toml.getString("module"));
        BigInteger n = new BigInteger(toml.getString("n"));
        BigInteger b = new BigInteger(toml.getString("b"));
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

    private GeneralECPoint hash(BigInteger t) {
        try {
            GeneralECPoint point;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            BigInteger x = new BigInteger(1, md.digest(t.toByteArray()));
            while((point = curve.liftX(new PrimeFieldElement(field, x))) == null) {
                x = x.add(BigInteger.ONE);
            }
            return point;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    @Override
    public GeneralECPoint encrypt(BigInteger message, BigInteger t) {
        return grup.getGenerator().pow(message)
                .multiply(hash(t).pow(privateKey));
    }

    @Override
    public List<Pair<BigInteger, Integer>> generateSij(final int l) {
        BigInteger divisor = BigInteger.TWO;
        divisor = divisor.pow(13);
        privateKey = generateSi();
        List<Pair<BigInteger, Integer>> result = new ArrayList<>();
        BigInteger[] tmp = privateKey.divideAndRemainder(divisor);
        for (int j = 0; j < l; j++) {
            result.add(new Pair<>(tmp[1], j));
            tmp = tmp[0].divideAndRemainder(divisor);
        }
        return result;
    }

    private BigInteger generateSi() {
        return field.getRandomElement().getIntValue();
    }
}
