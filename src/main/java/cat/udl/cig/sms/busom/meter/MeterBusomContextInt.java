package cat.udl.cig.sms.busom.meter;

import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.KeyRenewalException;

import java.io.IOException;
import java.math.BigInteger;

public interface MeterBusomContextInt {


    void generatePrivateKey() throws IOException, NullMessageException;
    void setUpNeighborHood() throws IOException, NullMessageException, KeyRenewalException;
    void sendChunk(BigInteger message) throws IOException, NullMessageException, KeyRenewalException;
    void sendPartialDecryption() throws IOException, NullMessageException, KeyRenewalException;
}
