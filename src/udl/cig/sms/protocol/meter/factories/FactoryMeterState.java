package udl.cig.sms.protocol.meter.factories;

import cat.udl.cig.fields.PrimeFieldElement;
import udl.cig.sms.busom.MeterBusomController;
import udl.cig.sms.busom.MeterBusomControllerInt;
import udl.cig.sms.connection.ConnectionMeterInt;
import udl.cig.sms.consumption.ConsumptionReader;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.protocol.meter.states.ConsumptionTransmission;
import udl.cig.sms.protocol.meter.states.KeyEstablishment;

public class FactoryMeterState {

    private final LoadCurve loadCurve;
    private final ConnectionMeterInt connection;
    private final ConsumptionReader consumption;
    private String certificate;

    public FactoryMeterState(LoadCurve loadCurve, ConnectionMeterInt connection,
                             ConsumptionReader consumption, String certificate) {
        this.loadCurve = loadCurve;
        this.connection = connection;
        this.consumption = consumption;
        this.certificate = certificate;
    }

    public ConsumptionTransmission makeConsumptionTransmission(PrimeFieldElement privateKey) {
        return new ConsumptionTransmission(this, privateKey);
    }

    public KeyEstablishment makeKeyEstablishment() {
        return new KeyEstablishment(this);
    }

    public MeterBusomControllerInt makeMeterBusomController() {
        return new MeterBusomController(certificate, loadCurve, connection);
    }

    public String getCertificate() {
        return certificate;
    }

    public LoadCurve getLoadCurve() {
        return loadCurve;
    }

    public ConnectionMeterInt getConnection() {
        return connection;
    }

    public ConsumptionReader getConsumption() {
        return consumption;
    }


}
