package udl.cig.sms.busom;

import udl.cig.sms.busom.meter.BusomSetUp;
import udl.cig.sms.busom.meter.SendChunk;
import udl.cig.sms.connection.ConnectionMeterInt;
import udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

/**
 * Controller of the protocol. Encapsulates the protocol to make it
 * easier for high architecture classes.
 */
public class MeterBusomController implements MeterBusomControllerInt {

    private BusomState state;

    /**
     * Generates a meter busom controller
     *
     * @param certificate certificate of the meter
     * @param loadCurve   parameters of the ECC curve
     * @param connection  to the substation.
     */
    public MeterBusomController(String certificate, LoadCurve loadCurve, ConnectionMeterInt connection) {
        this.state = new BusomSetUp(certificate, loadCurve, connection);
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
     * Generates a meter busom Controller. Used for testing
     *
     * @param loadCurve  parameters of the ECC curve
     * @param connection to the substation.
     * @throws IOException          if connection fails.
     * @throws NullMessageException Never throws this exception
     */
    protected MeterBusomController(LoadCurve loadCurve, ConnectionMeterInt connection) throws IOException, NullMessageException {
        this.state = new BusomSetUp("", loadCurve, connection);
        this.state = state.next();
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
            System.out.println("SM-BS-MS: " + (now - then));
            then = now;
        }
    }


}
