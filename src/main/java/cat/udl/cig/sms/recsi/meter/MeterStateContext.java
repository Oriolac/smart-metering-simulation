package cat.udl.cig.sms.recsi.meter;

import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.sms.busom.MeterBusomService;
import cat.udl.cig.sms.busom.MeterBusomServiceInt;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.consumption.ConsumptionReader;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.State;
import cat.udl.cig.sms.recsi.meter.states.ConsumptionTransmissionMeter;
import cat.udl.cig.sms.recsi.meter.states.KeyEstablishmentMeter;

import java.io.IOException;

/**
 * Factory of the states of the meter in smart metering protocol
 */
public class MeterStateContext implements MeterStateContextInt {

    private final CurveConfiguration curveConfiguration;
    private final ConnectionMeterInt connection;
    private final ConsumptionReader consumption;
    private final String certificate;
    private State state;

    /**
     * @param curveConfiguration   which has all the information of the ECC
     * @param connection  which manages the connection with the substation
     * @param consumption which reads all the consumption of the meter
     * @param certificate of the smart meter
     */
    public MeterStateContext(CurveConfiguration curveConfiguration, ConnectionMeterInt connection,
                             ConsumptionReader consumption, String certificate) {
        this.curveConfiguration = curveConfiguration;
        this.connection = connection;
        this.consumption = consumption;
        this.certificate = certificate;
        this.state = new KeyEstablishmentMeter(this);
    }

    /**
     * @param privateKey or si, private key of current smart meter
     * @return the consumption transmition state of the meter in order to send the consumption
     */
    public ConsumptionTransmissionMeter makeConsumptionTransmission(PrimeFieldElement privateKey) {
        return new ConsumptionTransmissionMeter(this, privateKey);
    }

    /**
     * @return the key establishment state of the meter in order to set the keys
     */
    public KeyEstablishmentMeter makeKeyEstablishment() {
        return new KeyEstablishmentMeter(this);
    }

    /**
     * @return the meter's controller of the busom protocol
     */
    public MeterBusomServiceInt makeMeterBusomController() {
        return new MeterBusomService(certificate, curveConfiguration, connection);
    }

    /**
     * @return the certificiate
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * @return the information of the ECC
     */
    public CurveConfiguration getLoadCurve() {
        return curveConfiguration;
    }

    /**
     * @return the connection controller
     */
    public ConnectionMeterInt getConnection() {
        return connection;
    }

    /**
     * @return the consumption reader
     */
    public ConsumptionReader getConsumption() {
        return consumption;
    }


    public void closeConnection() throws IOException {
        connection.close();
    }

    @Override
    public void establishKey() throws IOException, NullMessageException {
        if (!(state instanceof KeyEstablishmentMeter)) {
            throw new IllegalStateException();
        }
        state = state.next();
    }

    @Override
    public void sendConsumption() throws IOException, NullMessageException {
        if (!(state instanceof ConsumptionTransmissionMeter)) {
            throw new IllegalStateException();
        }
        state = state.next();
    }
}
