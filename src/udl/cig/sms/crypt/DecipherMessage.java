package udl.cig.sms.crypt;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.fields.RingElement;
import cat.udl.cig.operations.wrapper.PollardsLambda;
import cat.udl.cig.operations.wrapper.PollardsLambdaInt;
import com.moandjiezana.toml.Toml;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DecipherMessage extends LoadCurve implements Decypher, Hash {

    private BigInteger privateKey;
    private PollardsLambdaInt lambda;


    public DecipherMessage(File file) {
        loadCurve(file);
        lambda = new PollardsLambda(grup.getGenerator());
    }

    @Override
    public Optional<BigInteger> decrypt(List<GeneralECPoint> messageC, BigInteger time) {
        Optional<GeneralECPoint> d = getBeta(messageC, time);
        return d.flatMap((beta) -> lambda.algorithm(beta));
    }

    protected Optional<GeneralECPoint> getBeta(List<GeneralECPoint> messageC, BigInteger t) {
        Optional<GeneralECPoint> c = messageC.stream().reduce(GeneralECPoint::multiply);
        return c.map((x) -> x.multiply(hash(t).pow(privateKey)));
    }

    protected Optional<BigInteger> decrypt(GeneralECPoint beta) {
        return lambda.algorithm(beta);
    }

    public ECPrimeOrderSubgroup getGroup() {
        return this.grup;
    }

    @Override
    public GeneralEC getCurve() {
        return this.curve;
    }

    @Override
    public PrimeField getField() {
        return this.field;
    }

    protected void setLambda(PollardsLambdaInt lambda) {
        this.lambda = lambda;
    }

    public void setPrivateKey(BigInteger s0) {
        privateKey = s0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecipherMessage that = (DecipherMessage) o;
        return Objects.equals(privateKey, that.privateKey) &&
                Objects.equals(lambda, that.lambda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(privateKey, lambda);
    }

    public PollardsLambdaInt getLambda() {
        return lambda;
    }
}
