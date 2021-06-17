package cat.udl.cig.sms.recsi.substation;

import cat.udl.cig.sms.busom.SubstationBusomController;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.substation.states.ConsumptionTransmissionSubstation;
import cat.udl.cig.sms.recsi.substation.states.KeyEstablishmentSubstation;

import java.math.BigInteger;

/**
 * Factory that makes the management of the states and busom controller
 */
public class SubstationContext {

    private final CurveConfiguration curveConfiguration;
    private final ConnectionSubstationInt connection;

    /**
     * @param curveConfiguration  that contains the information of the ECC
     * @param connection that makes the connection with the smart meters
     */
    public SubstationContext(CurveConfiguration curveConfiguration, ConnectionSubstationInt connection) {
        this.curveConfiguration = curveConfiguration;
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
        return new SubstationBusomController(curveConfiguration, connection);
    }

    /**
     * @return the loadCurve, which has all the information of the ECC
     */
    public CurveConfiguration getLoadCurve() {
        return curveConfiguration;
    }

    /**
     * @return the connection, which manages the connection with the meters
     */
    public ConnectionSubstationInt getConnection() {
        return connection;
    }
}
