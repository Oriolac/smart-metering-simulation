package cat.udl.cig.sms.protocol.substation.factories;

import cat.udl.cig.sms.busom.SubstationBusomController;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.data.LoadCurve;
import cat.udl.cig.sms.protocol.substation.states.ConsumptionTransmissionSubstation;
import cat.udl.cig.sms.protocol.substation.states.KeyEstablishmentSubstation;

import java.math.BigInteger;

/**
 * Factory that makes the management of the states and busom controller
 */
public class FactorySubstationState {

    private LoadCurve loadCurve;
    private ConnectionSubstationInt connection;

    /**
     * @param loadCurve that contains the information of the ECC
     * @param connection that makes the connection with the smart meters
     */
    public FactorySubstationState(LoadCurve loadCurve, ConnectionSubstationInt connection) {
        this.loadCurve = loadCurve;
        this.connection = connection;
    }

    /**
     * @return the Key Establishment state in order to do the establishment of the keys
     */
    public KeyEstablishmentSubstation makeKeyEstablishment() {
        return new KeyEstablishmentSubstation(this);
    }

    /**
     * @param privateKey or s0
     * @return the Consumption Transmission State in order to pass the information of the meters
     */
    public ConsumptionTransmissionSubstation makeConsumptionTransmission(BigInteger privateKey) {
        return new ConsumptionTransmissionSubstation(this, privateKey);
    }

    /**
     * @return the protocol busom controller of the substation
     */
    public SubstationBusomController makeSubstationBusomController() {
        return new SubstationBusomController(loadCurve, connection);
    }

    /**
     * @return the loadCurve, which has all the information of the ECC
     */
    public LoadCurve getLoadCurve() {
        return loadCurve;
    }

    /**
     * @return the connection, which manages the connection with the meters
     */
    public ConnectionSubstationInt getConnection() {
        return connection;
    }
}
