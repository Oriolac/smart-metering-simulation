package udl.cig.sms.crypt;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.fields.PrimeFieldElement;
import javafx.util.Pair;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CypherMessage extends LoadCurve implements Cypher, Hash {

    private PrimeFieldElement privateKey;

    public CypherMessage(File file) {
        loadCurve(file);
    }

    @Override
    public GeneralECPoint encrypt(BigInteger message, BigInteger t) {
        return grup.getGenerator().pow(message)
                .multiply(hash(t).pow(privateKey.getIntValue()));
    }

    @Override
    public List<Pair<BigInteger, Integer>> generateSij() {
        BigInteger divisor = BigInteger.TWO;
        divisor = divisor.pow(13);
        privateKey = generateSi();
        List<Pair<BigInteger, Integer>> result = new ArrayList<>();
        int j = 0;

        BigInteger[] tmp = privateKey.getIntValue().divideAndRemainder(divisor);
        while (!tmp[0].equals(BigInteger.ZERO)) {
            result.add(new Pair<>(tmp[1], j));
            j++;
            tmp = tmp[0].divideAndRemainder(divisor);
        }
        result.add(new Pair<>(tmp[1], j));
        return result;
    }

    public void setPrivateKey(PrimeFieldElement privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public GeneralEC getCurve() {
        return this.curve;
    }

    @Override
    public PrimeField getField() {
        return this.field;
    }

    public ECPrimeOrderSubgroup getGroup() {
        return this.grup;
    }

    public BigInteger getKey() {
        return privateKey.getIntValue();
    }

    private PrimeFieldElement generateSi() {
        return field.getRandomElement();
    }
}
