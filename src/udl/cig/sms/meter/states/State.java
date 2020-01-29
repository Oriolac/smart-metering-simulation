package udl.cig.sms.meter.states;

import udl.cig.sms.busom.NullMessageException;

import java.io.IOException;

public interface State {

    State next() throws IOException, NullMessageException;
}
