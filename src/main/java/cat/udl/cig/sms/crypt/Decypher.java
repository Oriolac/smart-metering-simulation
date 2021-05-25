package cat.udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.operations.wrapper.LogarithmAlgorithm;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Decipher interface
 */
public interface Decypher {

    /**
     * decrypts the sum of the messages, given a t to hash
     *
     * @param messageC list of messages to be summed
     * @param t        random number to be hashed to a point
     * @return number if decription works, else it fails
     */
    Optional<BigInteger> decrypt(List<GeneralECPoint> messageC, BigInteger t);

    /**
     * Sets lambda parameter (it's needed for decrypt and automated testing)
     *
     * @param lambda lambda to be set
     */
    void setLambda(LogarithmAlgorithm lambda);
}
