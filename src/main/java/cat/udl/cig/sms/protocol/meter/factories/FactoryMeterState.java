package cat.udl.cig.sms.protocol.meter.factories;

import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.sms.busom.MeterBusomController;
import cat.udl.cig.sms.busom.MeterBusomControllerInt;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.consumption.ConsumptionReader;
import cat.udl.cig.sms.data.LoadCurve;
import cat.udl.cig.sms.protocol.meter.states.ConsumptionTransmission;
import cat.udl.cig.sms.protocol.meter.states.KeyEstablishment;

import java.io.IOException;

/**
 * Factory of the states of the meter in smart metering protocol
 */
public class FactoryMeterState {

    private final LoadCurve loadCurve;
    private final ConnectionMeterInt connection;
    private final ConsumptionReader consumption;
    private final String certificate;

    /**
     * @param loadCurve   which has all the information of the ECC
     * @param connection  which manages the connection with the substation
     * @param consumption which reads all the consumption of the meter
     * @param certificate of the smart meter
     */
    public FactoryMeterState(LoadCurve loadCurve, ConnectionMeterInt connection,
                             ConsumptionReader consumption, String certificate) {
        this.loadCurve = loadCurve;
        this.connection = connection;
        this.consumption = consumption;
        this.certificate = certificate;
    }

    /**
     * @param privateKey or si, private key of current smart meter
     * @return the consumption transmition state of the meter in order to send the consumption
     */
    public ConsumptionTransmission makeConsumptionTransmission(PrimeFieldElement privateKey) {
        return new ConsumptionTransmission(this, privateKey);
    }

    /**
     * @return the key establishment state of the meter in order to set the keys
     */
    public KeyEstablishment makeKeyEstablishment() {
        return new KeyEstablishment(this);
    }

    /**
     * @return the meter's controller of the busom protocol
     */
    public MeterBusomControllerInt makeMeterBusomController() {
        return new MeterBusomController(certificate, loadCurve, connection);
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
    public LoadCurve getLoadCurve() {
        return loadCurve;
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
}
