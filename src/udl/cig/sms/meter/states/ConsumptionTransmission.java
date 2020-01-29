package udl.cig.sms.meter.states;

import cat.udl.cig.fields.PrimeFieldElement;
import udl.cig.sms.connection.ConnectionMeterInt;
import udl.cig.sms.data.LoadCurve;

public class ConsumptionTransmission implements State {

    public ConsumptionTransmission(LoadCurve loadCurve, ConnectionMeterInt connectionMeter, PrimeFieldElement privateKey) {

    }

    @Override
    public State next() {
        return this;
    }
}
