package cat.udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralECPoint;

import java.math.BigInteger;

/**
 * Cypher Interface.
 */
public interface Cypher {

    /**
     * Given a message and a big Integer t, it encripts the message
     * using the method described in the papers : TODO put reference.
     *
     * @param message to be encrypted
     * @param t       random number to be used for hashing
     * @return encrypted point
     */
    GeneralECPoint encrypt(BigInteger message, BigInteger t);

}
