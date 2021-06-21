package cat.udl.cig.sms.busom.substation;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import cat.udl.cig.sms.busom.BusomState;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.recsi.State;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public class SubstationBusomContext implements SubstationBusomContextInt {

    private BusomState state;

    public SubstationBusomContext(ECPrimeOrderSubgroup group, ConnectionSubstationInt connection) {
        this.state = new BusomSubstationSetup(group, connection);
    }

    @Override
    public void setUp() throws IOException, NullMessageException {
        if (!(state instanceof  BusomSubstationSetup)) {
            throw new IllegalStateException();
        }
        this.state = state.next();
    }

    @Override
    public void computeC() throws IOException, NullMessageException {
        if (!(state instanceof ReceiveChunk)) {
            throw new IllegalStateException();
        }
        this.state = state.next();
    }

    @Override
    public Optional<BigInteger> decrypt() throws IOException, NullMessageException {
        if (!(state instanceof  DecriptChunk)) {
            throw new IllegalStateException();
        }
        BusomState receiveChunk = state.next();
        Optional<BigInteger> message = ((DecriptChunk) state).readMessage();
        this.state = receiveChunk;
        return message;
    }
}
