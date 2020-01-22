package udl.cig.sms.meter.states;

public class ConsumptionTransmission implements State {
    @Override
    public State next() {
        return this;
    }
}
