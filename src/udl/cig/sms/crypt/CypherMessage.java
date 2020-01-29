package udl.cig.sms.crypt;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.fields.PrimeFieldElement;
import udl.cig.sms.data.LoadCurve;

import java.math.BigInteger;

public class CypherMessage implements Cypher, Hash {

    private final PrimeField field;
    private final GeneralEC curve;
    private final ECPrimeOrderSubgroup grup;
    final private PrimeFieldElement privateKey;

    public CypherMessage(LoadCurve loadCurve, BigInteger privateKey) {
        this.curve = loadCurve.getCurve();
        this.grup = loadCurve.getGroup();
        this.field = loadCurve.getField();
        this.privateKey = field.toElement(privateKey);
    }

    @Override
    public GeneralECPoint encrypt(BigInteger message, BigInteger t) {
        return grup.getGenerator().pow(message)
                .multiply(hash(t).pow(privateKey.getIntValue()));
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

}
