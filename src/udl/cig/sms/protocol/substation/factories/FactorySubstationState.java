package udl.cig.sms.protocol.substation.factories;

import udl.cig.sms.busom.SubstationBusomController;
import udl.cig.sms.connection.ConnectionSubstationInt;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.protocol.substation.states.ConsumptionTransmissionSubstation;
import udl.cig.sms.protocol.substation.states.KeyEstablishmentSubstation;

import java.math.BigInteger;

public class FactorySubstationState {

    private LoadCurve loadCurve;
    private ConnectionSubstationInt connection;

    public FactorySubstationState(LoadCurve loadCurve, ConnectionSubstationInt connection) {
        this.loadCurve = loadCurve;
        this.connection = connection;
    }

    public KeyEstablishmentSubstation makeKeyEstablishment() {
        return new KeyEstablishmentSubstation(this);
    }

    public ConsumptionTransmissionSubstation makeConsumptionTransmission(BigInteger privateKey) {
        return new ConsumptionTransmissionSubstation(this, privateKey);
    }

    public SubstationBusomController makeSubstationBusomController() {
        return new SubstationBusomController(loadCurve, connection);
    }

    public LoadCurve getLoadCurve() {
        return loadCurve;
    }

    public ConnectionSubstationInt getConnection() {
        return connection;
    }
}
