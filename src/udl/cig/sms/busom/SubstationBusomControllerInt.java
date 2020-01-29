package udl.cig.sms.busom;

import java.io.IOException;
import java.math.BigInteger;

public interface SubstationBusomControllerInt {
    BigInteger receiveSecretKey() throws IOException, NullMessageException;
}
