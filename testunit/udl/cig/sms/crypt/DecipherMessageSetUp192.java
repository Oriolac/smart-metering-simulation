package udl.cig.sms.crypt;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.operations.wrapper.PollardsLambda;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecipherMessageSetUp192 extends LoadCurve {

    final File file = new File("data/p192.toml");


    @Test
    void decryptUsingConstantLambda() {
        loadCurve(file);
        GeneralECPoint alpha = this.grup.getGenerator();
        PollardsLambda lambda = new PollardsLambda(alpha);
        DecipherMessage dec = new DecipherMessage(file);
        assertEquals(lambda, dec.getLambda());
        dec.setLambda(lambda);
        assertEquals(this.grup, dec.getGroup());
        assertEquals(alpha, dec.getGroup().getGenerator());
        assertEquals(Optional.of(BigInteger.ONE), dec.decrypt(alpha));
    }
}
