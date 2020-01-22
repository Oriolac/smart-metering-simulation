package udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralECPoint;

import java.math.BigInteger;

public interface Cypher {

    GeneralECPoint encrypt(BigInteger message, BigInteger t);

    // Creates Si, and returns Sij to be send to substation.
}
