package udl.cig.sms.crypt;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.fields.RingElement;
import cat.udl.cig.operations.wrapper.PollardsLambda;
import com.moandjiezana.toml.Toml;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DecipherMessage extends LoadCurve implements Decypher, Hash {

    private BigInteger privateKey;  // Si in formulas


    public DecipherMessage(File file) {
        loadCurve(file);
    }


    @Override
    public Optional<BigInteger> decrypt(List<GeneralECPoint> messageC, BigInteger t) {
        Optional<GeneralECPoint> c = messageC.stream().reduce(GeneralECPoint::multiply);
        Optional<GeneralECPoint> d = c.map((x) -> x.multiply(hash(t).pow(privateKey)));
        Optional<PollardsLambda> lambda = d.map((beta) -> new PollardsLambda(grup.getGenerator(), beta));
        if (lambda.isPresent()){
            return lambda.get().algorithm();
        }
        return Optional.empty();
    }

    public ECPrimeOrderSubgroup getGroup(){
        return this.grup;
    }

    @Override
    public GeneralEC getCurve(){
        return this.curve;
    }

    @Override
    public PrimeField getField() {
        return this.field;
    }

    public void setS0(BigInteger s0) {
        privateKey = s0;
    }
}
