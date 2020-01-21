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
    GeneralECPoint alpha;
    PollardsLambda lambda;

    @Test
    void decryptUsingConstantLambda() {
        loadCurve(file);
        DecipherMessage dec = new DecipherMessage(file);
        alpha = this.grup.getGenerator();
        lambda = new PollardsLambda(alpha);
        assertEquals(lambda, dec.getLambda());
        //dec.setLambda(lambda);
        assertEquals(this.grup, dec.getGroup());
        assertEquals(alpha, dec.getGroup().getGenerator());
        assertEquals(Optional.of(BigInteger.ONE), dec.decrypt(alpha));
        assertEquals(Optional.of(BigInteger.TWO), dec.decrypt(alpha.pow(BigInteger.TWO)));
    }
}
