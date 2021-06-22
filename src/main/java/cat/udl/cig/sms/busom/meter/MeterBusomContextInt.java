package cat.udl.cig.sms.busom.meter;

import cat.udl.cig.sms.busom.NullMessageException;

import java.io.IOException;
import java.math.BigInteger;

public interface MeterBusomContextInt {


    void generatePrivateKey() throws IOException, NullMessageException;
    void setUpNeighborHood() throws IOException, NullMessageException;
    void sendChunk(BigInteger message) throws IOException, NullMessageException;
    void sendPartialDecryption() throws IOException, NullMessageException;
}
