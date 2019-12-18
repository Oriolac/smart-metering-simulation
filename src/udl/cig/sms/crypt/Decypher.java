package udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralECPoint;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface Decypher {

    public Optional<BigInteger> decrypt(List<GeneralECPoint> messageC, BigInteger t);
    //public void generatePrivateKey(List<Pair<BigInteger, Integer>> list);
}
