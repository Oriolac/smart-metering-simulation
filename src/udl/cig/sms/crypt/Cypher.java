package udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralECPoint;
import javafx.util.Pair;

import java.math.BigInteger;
import java.util.List;

public interface Cypher {

    public GeneralECPoint encrypt(BigInteger message);
    public List<Pair<BigInteger, Integer>> generateSij();
    // Creates Si, and returns Sij to be send to substation.
}
