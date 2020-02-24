package udl.cig.sms.crypt;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.ecc.GeneralEC;
import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.PrimeField;
import cat.udl.cig.operations.wrapper.BruteForce;
import cat.udl.cig.operations.wrapper.LogarithmAlgorithm;
import udl.cig.sms.data.LoadCurve;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DecipherMessage implements Decypher, Hash {

    private final ECPrimeOrderSubgroup grup;
    private final GeneralEC curve;
    private final PrimeField field;
    final private BigInteger privateKey;
    private LogarithmAlgorithm lambda;


    public DecipherMessage(LoadCurve loadCurve, BigInteger privateKey) {
        this.curve = loadCurve.getCurve();
        this.field = loadCurve.getField();
        this.grup = loadCurve.getGroup();
        lambda = new BruteForce(grup.getGenerator(), BigInteger.valueOf(2000));
        this.privateKey = privateKey;
    }

    /**
     * decrypts the sum of the messages, given a t to hash
     *
     * @param messageC list of messages to be summed
     * @param time     random number to be hashed to a point
     * @return number if decription works, else it fails
     */
    @Override
    public Optional<BigInteger> decrypt(List<GeneralECPoint> messageC, BigInteger time) {
        Optional<GeneralECPoint> d = getBeta(messageC, time);
        return d.flatMap(lambda::algorithm);
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
     * Sets lambda parameter (it's needed for decrypt and automated testing)
     *
     * @param lambda lambda to be set
     */
    public void setLambda(LogarithmAlgorithm lambda) {
        this.lambda = lambda;
    }


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

    /**
     * @return gets the algorithm to decrypt
     */
    public LogarithmAlgorithm getLambda() {
        return lambda;
    }
}
