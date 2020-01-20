package udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.fields.PrimeFieldElement;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface Hash {

    GeneralEC getCurve();

    PrimeField getField();

    default GeneralECPoint hash(BigInteger t) {
        try {
            GeneralECPoint point;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            BigInteger x = new BigInteger(1, md.digest(t.toByteArray()));
            while((point = getCurve().liftX(new PrimeFieldElement(getField(), x))) == null) {
                x = x.add(BigInteger.ONE);
            }
            return point;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
