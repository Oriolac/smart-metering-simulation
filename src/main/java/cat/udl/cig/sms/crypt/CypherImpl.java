package cat.udl.cig.sms.crypt;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.sms.data.LoadCurve;

import java.math.BigInteger;

/**
 * Implements Cypher, uses Hash
 */
public class CypherImpl implements Cypher, Hash {

    private final PrimeField field;
    private final GeneralEC curve;
    private final ECPrimeOrderSubgroup grup;
    final private PrimeFieldElement privateKey;

    /**
     * Genenerates a cypherMessage
     *
     * @param loadCurve  curve to be used for encrypts
     * @param privateKey private key to be used for encrypts
     */
    public CypherImpl(LoadCurve loadCurve, BigInteger privateKey) {
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


    /**
     * @return curve
     */
    @Override
    public GeneralEC getCurve() {
        return this.curve;
    }

    /**
     * @return field
     */
    @Override
    public PrimeField getField() {
        return this.field;
    }

    /**
     * @return ECPrimeOrderSubgroup
     */
    public ECPrimeOrderSubgroup getGroup() {
        return this.grup;
    }

    /**
     * @return privateKey as a BigInteger
     */
    public BigInteger getKey() {
        return privateKey.getIntValue();
    }

}
