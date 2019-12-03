package udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralECPoint;
import javafx.util.Pair;

import java.math.BigInteger;
import java.util.List;

public interface Decypher {

    public BigInteger decrypt(List<GeneralECPoint> messageC, BigInteger t);
    //public void generatePrivateKey(List<Pair<BigInteger, Integer>> list);
}
