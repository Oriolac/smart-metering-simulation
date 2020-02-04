package udl.cig.sms.protocol.substation.factories;

import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.busom.SubstationBusomController;
import udl.cig.sms.connection.ConnectionSubstationInt;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.protocol.substation.states.ConsumptionTransmissionSubstation;
import udl.cig.sms.protocol.substation.states.KeyEstablishmentSubstation;

import java.io.IOException;

public class FactorySubstationState {

    private LoadCurve loadCurve;
    private ConnectionSubstationInt connection;

    public FactorySubstationState(LoadCurve loadCurve, ConnectionSubstationInt connection) {
        this.loadCurve = loadCurve;
        this.connection = connection;
    }

    public KeyEstablishmentSubstation makeKeyEstablishment() {
        return new KeyEstablishmentSubstation();
    }

    public ConsumptionTransmissionSubstation makeConsumptionTransmission() {
        return new ConsumptionTransmissionSubstation();
    }

    public SubstationBusomController makeSubstationBusomController() throws IOException, NullMessageException {
        return new SubstationBusomController(loadCurve, connection);
    }

    public LoadCurve getLoadCurve() {
        return loadCurve;
    }

    public ConnectionSubstationInt getConnection() {
        return connection;
    }
}
