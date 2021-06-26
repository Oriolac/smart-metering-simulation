package cat.udl.cig.sms.busom.meter;

import cat.udl.cig.sms.busom.BusomMeterState;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.connection.KeyRenewalException;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.IOException;
import java.math.BigInteger;

public class MeterBusomContext implements MeterBusomContextInt {

    private BusomMeterState state;

    public MeterBusomContext(String certificate, CurveConfiguration curveConfiguration, ConnectionMeterInt connection) {
        this.state = new BusomSetUp(certificate, curveConfiguration, connection);
    }

    @Override
    public void generatePrivateKey() throws IOException {
        if (!(state instanceof BusomSetUp)) {
            throw new IllegalStateException("Expected BusomSetUp BusomState");
        }
        this.state = ((BusomSetUp) state).next();
    }

    @Override
    public void setUpNeighborHood() throws IOException, NullMessageException, KeyRenewalException {
        if (!(state instanceof NeighborhoodSetUp)) {
            throw new IllegalStateException("Expected NeighborhoodSetUp BusomState");
        }
        this.state = this.state.next();

    }

    @Override
    public void sendChunk(BigInteger message) throws IOException, NullMessageException, KeyRenewalException {
        if (!(state instanceof SendChunk)) {
            throw new IllegalStateException("Excepted SendChunk BusomState");
        }
        ((SendChunk) state).setMessage(message);
        this.state = this.state.next();
    }

    @Override
    public void sendPartialDecryption() throws IOException, NullMessageException, KeyRenewalException {
        if (!(state instanceof SendPartialDecryption)) {
            throw new IllegalStateException("Excepted SendPartialDecryption BusomState");
        }
        this.state = this.state.next();
    }
}
