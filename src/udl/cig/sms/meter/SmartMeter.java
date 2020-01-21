package udl.cig.sms.meter;

import udl.cig.sms.meter.states.State;

public interface SmartMeter {

    long getId();
    State getState();
    State setState(State state);
}
