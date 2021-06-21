package cat.udl.cig.sms.busom;

import cat.udl.cig.sms.busom.meter.BusomSetUp;
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

    private BusomState state;

    /**
     * Generates a meter busom controller
     *
     * @param certificate certificate of the meter
     * @param curveConfiguration   parameters of the ECC curve
     * @param connection  to the substation.
     */
    public MeterBusomService(String certificate, CurveConfiguration curveConfiguration, ConnectionMeterInt connection) {
        this.state = new BusomSetUp(certificate, curveConfiguration, connection);
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
        this.state = state.next();
    }

    /**
     * Starts the protocol
     *
     * @throws NullMessageException Never throwed. Declared by substractions of code.
     * @throws IOException          if connection fails.
     */
    @Override
    public void start() throws NullMessageException, IOException {
        this.state = state.next().next();
    }


    /**
     * Used for tests
     *
     * @throws IOException          if connection fails
     * @throws NullMessageException never throwed.
     */
    protected void meterSetUp() throws IOException, NullMessageException {
        this.state = state.next();
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
        SendChunk currentState = (SendChunk) this.state;
        then = Instant.now().toEpochMilli();
        for (BigInteger message : messages) {
            currentState.setMessage(message);
            currentState = (SendChunk) currentState.next().next();
            now = Instant.now().toEpochMilli();
            System.out.println("SM-BS: " + (now - then));
            then = now;
        }
    }


}
