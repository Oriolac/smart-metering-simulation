package cat.udl.cig.sms.recsi.substation;

import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.busom.SubstationBusomService;
import cat.udl.cig.sms.busom.SubstationBusomServiceInt;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.connection.KeyRenewalException;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.State;
import cat.udl.cig.sms.recsi.substation.states.ConsumptionTransmissionSubstation;
import cat.udl.cig.sms.recsi.substation.states.KeyEstablishmentSubstation;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Factory that makes the management of the states and busom controller
 */
public class SubstationStateContext implements SubstationStateContextInt {

    private final CurveConfiguration curveConfiguration;
    private final ConnectionSubstationInt connection;
    private BigInteger message;
    private State state;

    /**
     * @param curveConfiguration  that contains the information of the ECC
     * @param connection that makes the connection with the smart meters
     */
    public SubstationStateContext(CurveConfiguration curveConfiguration, ConnectionSubstationInt connection) {
        this.curveConfiguration = curveConfiguration;
        this.connection = connection;
        this.state = new KeyEstablishmentSubstation(this);
    }

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

    public void saveMessage(BigInteger message) {
        this.message = message;
    }

    /**
     * @return the protocol busom controller of the substation
     */
    public SubstationBusomService makeSubstationBusomController() {
        return new SubstationBusomService(curveConfiguration, connection);
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

    @Override
    public void startKeyEstablishment() throws IOException, NullMessageException {
        if (!(state instanceof KeyEstablishmentSubstation))
            throw new IllegalStateException();
        try {
            this.state = state.next();
        } catch (KeyRenewalException exception) {
            state = makeKeyEstablishment();
            startKeyEstablishment();
        }
    }

    public Optional<BigInteger> getPrivateKey() {
        Optional<BigInteger> privateKey = Optional.empty();
        if (state instanceof ConsumptionTransmissionSubstation) {
            privateKey = Optional.of(((ConsumptionTransmissionSubstation) state).getPrivateKey());
        }
        return privateKey;
    }

    public void setSubstationBusomControllerInt(SubstationBusomServiceInt controllerInt) {
        ((KeyEstablishmentSubstation) state).setService(controllerInt);
    }

    @Override
    public Optional<BigInteger> getMessage() throws IOException, NullMessageException {
        if (!(state instanceof ConsumptionTransmissionSubstation))
            return Optional.empty();
        this.state = ((ConsumptionTransmissionSubstation) state).next();
        return Optional.of(this.message);
    }
}
