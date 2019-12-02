package udl.cig.sms.meter;

import java.math.BigInteger;

public interface SmartMeter {

    public long getId();
    public State getState();
    public State setState(State state);
}
