package cat.udl.cig.sms.busom;

import cat.udl.cig.sms.busom.substation.BusomSubstationSetup;
import cat.udl.cig.sms.busom.substation.DecriptChunk;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Optional;

/**
 * Controller of Substation Busom. Extract the method to calculate a key.
 */
public class SubstationBusomController implements SubstationBusomControllerInt {

    private final int numberOfChunks;
    private BusomState state;

    /**
     * Generates a Substation Busom Controller.
     *
     * @param loadCurve  loads the ECC curve used for the protocol
     * @param connection to all the meters
     */
    public SubstationBusomController(LoadCurve loadCurve, ConnectionSubstationInt connection) {
        this.state = new BusomSubstationSetup(loadCurve.getGroup(), connection);
        int bits = loadCurve.getField().getSize().bitLength();
        this.numberOfChunks = bits / 13 + ((bits % 13 == 0) ? 0 : 1);
    }

    /**
     * Receives and computes a key, adding all the messages (sis) with their power.
     *
     * @return Key to decrypt messages of the parent protocol
     * @throws IOException          If connection fails.
     * @throws NullMessageException Never sent.
     */
    @Override
    public BigInteger receiveSecretKey() throws IOException, NullMessageException {
        this.state = state.next();
        BigInteger message = BigInteger.ZERO;
        long then, now;
        then = Instant.now().toEpochMilli();
        for (int i = 0; i < this.numberOfChunks; ++i) {
            BusomState currentState = this.state.next();
            this.state = currentState.next();
            Optional<BigInteger> currentMessage = ((DecriptChunk) currentState).readMessage();
            if (currentMessage.isEmpty()) {
                throw new NullMessageException();
            }
            message = message.add(currentMessage.get().multiply(BigInteger.TWO.pow(13 * i)));
            now = Instant.now().toEpochMilli();
            System.out.println("SSt-BS: " + (now - then));
            then = now;
        }
        return message;
    }

}
