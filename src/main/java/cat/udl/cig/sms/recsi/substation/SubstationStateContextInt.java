package cat.udl.cig.sms.recsi.substation;

import cat.udl.cig.sms.busom.NullMessageException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public interface SubstationStateContextInt {

    public void startKeyEstablishment() throws IOException, NullMessageException;

    /**
     * @throws IOException          in case that the IO fails
     * @throws NullMessageException in case the message is empty
     * @return agregated message
     */
    public Optional<BigInteger> getMessage() throws IOException, NullMessageException;
}
