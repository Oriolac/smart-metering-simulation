package udl.cig.sms.busom;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public interface MeterBusomControllerInt {

    void start() throws NullMessageException, IOException;
    void sendMessage(List<BigInteger> messages) throws IOException, NullMessageException;
}
