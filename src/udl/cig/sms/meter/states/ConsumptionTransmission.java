package udl.cig.sms.meter.states;

import cat.udl.cig.fields.PrimeFieldElement;
import udl.cig.sms.connection.ConnectionMeterInt;
import udl.cig.sms.data.LoadCurve;

public class ConsumptionTransmission implements State {

    private final PrimeFieldElement privateKey;
    private final LoadCurve loadCurve;
    private final ConnectionMeterInt connectionMeter;

    public ConsumptionTransmission(LoadCurve loadCurve, ConnectionMeterInt connectionMeter, PrimeFieldElement privateKey) {
        this.loadCurve = loadCurve;
        this.connectionMeter = connectionMeter;
        this.privateKey = privateKey;
    }

    @Override
    public State next() {
        return this;
    }
}
