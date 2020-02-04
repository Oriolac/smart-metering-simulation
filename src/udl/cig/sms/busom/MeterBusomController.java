package udl.cig.sms.busom;

import udl.cig.sms.busom.meter.BusomSetUp;
import udl.cig.sms.busom.meter.SendChunk;
import udl.cig.sms.connection.ConnectionMeterInt;
import udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class MeterBusomController implements MeterBusomControllerInt {

    private BusomState state;

    public MeterBusomController(String certificate, LoadCurve loadCurve, ConnectionMeterInt connection) {
        this.state = new BusomSetUp(certificate, loadCurve, connection);
    }

    @Override
    public void start() throws NullMessageException, IOException {
        this.state = state.next().next();
    }

    protected MeterBusomController(LoadCurve loadCurve, ConnectionMeterInt connection) throws IOException, NullMessageException {
        this.state = new BusomSetUp("", loadCurve, connection);
        this.state = state.next();
    }

    protected void meterSetUp() throws IOException, NullMessageException {
        this.state = state.next();
    }

    @Override
    public void sendMessage(List<BigInteger> messages) throws IOException, NullMessageException {
        SendChunk currentState = (SendChunk) this.state;
        for (BigInteger message : messages) {
            currentState.setMessage(message);
            currentState = (SendChunk) currentState.next().next();
        }
    }


}
