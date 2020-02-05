package udl.cig.sms.busom;

import udl.cig.sms.busom.substation.BusomSubstationSetup;
import udl.cig.sms.busom.substation.DecriptChunk;
import udl.cig.sms.connection.ConnectionSubstationInt;
import udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public class SubstationBusomController implements SubstationBusomControllerInt {

    private final int numberOfChunks;
    private BusomState state;

    public SubstationBusomController(LoadCurve loadCurve, ConnectionSubstationInt connection) {
        this.state = new BusomSubstationSetup(loadCurve.getGroup(), connection);
        int bits = loadCurve.getField().getSize().bitLength();
        this.numberOfChunks =  bits / 13 + ((bits % 13 == 0) ? 0 : 1);
    }

    @Override
    public BigInteger receiveSecretKey() throws IOException, NullMessageException {
        this.state = state.next();
        BigInteger message = BigInteger.ZERO;
        for (int i = 0; i < this.numberOfChunks; ++i) {
            BusomState currentState = this.state.next();
            this.state = currentState.next();
            Optional<BigInteger> currentMessage = ((DecriptChunk) currentState).readMessage();
            if (currentMessage.isEmpty()) {
                throw new NullMessageException();
            }
            message = message.add(currentMessage.get().multiply(BigInteger.TWO.pow(13 * i)));
        }
        return message;
    }

}
