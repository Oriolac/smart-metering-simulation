package cat.udl.cig.sms.busom;

import cat.udl.cig.sms.busom.meter.BusomSetUp;
import cat.udl.cig.sms.busom.meter.MeterBusomContext;
import cat.udl.cig.sms.busom.meter.MeterBusomContextInt;
import cat.udl.cig.sms.busom.meter.SendChunk;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

/**
 * Controller of the protocol. Encapsulates the protocol to make it
 * easier for high architecture classes.
 */
public class MeterBusomService implements MeterBusomServiceInt {

    private final MeterBusomContextInt context;

    /**
     * Generates a meter busom controller
     *
     * @param certificate certificate of the meter
     * @param curveConfiguration   parameters of the ECC curve
     * @param connection  to the substation.
     */
    public MeterBusomService(String certificate, CurveConfiguration curveConfiguration, ConnectionMeterInt connection) throws IOException, NullMessageException {
        context = new MeterBusomContext(certificate, curveConfiguration, connection);
        context.generatePrivateKey();
    }

    /**
     * Generates a meter busom Controller. Used for testing
     *
     * @param curveConfiguration  parameters of the ECC curve
     * @param connection to the substation.
     * @throws IOException          if connection fails.
     * @throws NullMessageException Never throws this exception
     */
    public MeterBusomService(CurveConfiguration curveConfiguration, ConnectionMeterInt connection) throws IOException, NullMessageException {
        this("", curveConfiguration, connection);
    }

    /**
     * Starts the protocol
     *
     * @throws NullMessageException Never throwed. Declared by substractions of code.
     * @throws IOException          if connection fails.
     */
    @Override
    public void start() throws NullMessageException, IOException {
        context.setUpNeighborHood();
    }


    /**
     * Used for tests
     *
     * @throws IOException          if connection fails
     * @throws NullMessageException never throwed.
     */
    protected void meterSetUp() throws IOException, NullMessageException {
        context.setUpNeighborHood();
    }

    /**
     * Sends List of messages to the substation
     *
     * @param messages messages to be sent. At maximum 13 bits
     * @throws IOException          if connection fails
     * @throws NullMessageException if some message is null.
     */
    @Override
    public void sendMessage(List<BigInteger> messages) throws IOException, NullMessageException {
        long now, then;
        then = Instant.now().toEpochMilli();
        for (BigInteger message : messages) {
            context.sendChunk(message);
            context.sendPartialDecryption();
            now = Instant.now().toEpochMilli();
            System.out.println("SM-BS: " + (now - then));
            then = now;
        }
    }


}
