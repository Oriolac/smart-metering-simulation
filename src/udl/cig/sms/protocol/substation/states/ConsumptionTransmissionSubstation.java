package udl.cig.sms.protocol.substation.states;

import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.protocol.State;

import java.io.IOException;
import java.math.BigInteger;

public class ConsumptionTransmissionSubstation implements State {

    @Override
    public State next() throws IOException, NullMessageException {
        return null;
    }

    public BigInteger getPrivateKey() {
        return null;
    }
}
