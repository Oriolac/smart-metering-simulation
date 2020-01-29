package udl.cig.sms.busom;

import udl.cig.sms.busom.certificate.CertificateValidation;
import udl.cig.sms.busom.substation.BusomSubstationSetup;
import udl.cig.sms.busom.substation.DecriptChunk;
import udl.cig.sms.connection.ConnectionSubstationInt;
import udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public class SubstationBusomController implements SubstationBusomControllerInt {

    private final int numberOfMessages;
    private BusomState state;

    public SubstationBusomController(LoadCurve loadCurve, ConnectionSubstationInt connection, CertificateValidation<String> validation)
            throws IOException, NullMessageException {
        this.state = new BusomSubstationSetup(loadCurve.getGroup(), connection, validation);
        this.state = state.next();
        int bits = loadCurve.getField().getSize().bitLength();
        this.numberOfMessages =  bits / 13 + ((bits % 13 == 0) ? 0 : 1);
    }

    @Override
    public BigInteger receiveSecretKey() throws IOException, NullMessageException {
        BigInteger message = BigInteger.ZERO;
        for (int i = 0; i < this.numberOfMessages; ++i) {
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
