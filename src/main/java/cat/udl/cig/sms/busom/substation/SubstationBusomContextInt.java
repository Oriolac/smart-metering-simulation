package cat.udl.cig.sms.busom.substation;

import cat.udl.cig.sms.busom.NullMessageException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public interface SubstationBusomContextInt {

    void setUp() throws IOException, NullMessageException;

    void computeC() throws IOException, NullMessageException;

    Optional<BigInteger> decrypt() throws IOException, NullMessageException;
}
