package cat.udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.sms.busom.BusomState;
import cat.udl.cig.sms.busom.certificate.CertificateTrueMock;
import cat.udl.cig.sms.busom.certificate.CertificateValidation;
import cat.udl.cig.sms.busom.data.MeterKey;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.connection.ReceiverMeter;
import cat.udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Sets up the parametrs of the neighborhood, like the
 * general key.
 */
public class NeighborhoodSetUp implements BusomState {


    private final BigInteger privateKey;
    private final LoadCurve loadCurve;
    private ConnectionMeterInt connection;
    private GroupElement generalKey;
    private GroupElement generator;
    private ReceiverMeter receiverMeter;
    private CertificateValidation<String> validation;

    /**
     * Sets up the Neighborhood set up
     *
     * @param privateKey Private key of the meter
     * @param loadCurve  curve to be used to encrypt and decrypt
     */
    protected NeighborhoodSetUp(BigInteger privateKey, LoadCurve loadCurve) {
        this.privateKey = privateKey;
        this.loadCurve = loadCurve;
        this.generator = loadCurve.getGroup().getGenerator();
        this.validation = new CertificateTrueMock<>();
    }

    /**
     * Sets up the Neighborhood set up
     *
     * @param privateKey Private key of the meter
     * @param loadCurve  curve to be used to encrypt and decrypt
     * @param connection connection to communicate to the substation
     */
    protected NeighborhoodSetUp(BigInteger privateKey, LoadCurve loadCurve, ConnectionMeterInt connection) {
        this(privateKey, loadCurve);
        this.connection = connection;
        this.receiverMeter = connection;
    }

    /**
     * Next state of the protocol.
     *
     * @return next state given a preset of conditions
     * @throws IOException, if connection has failed.
     */
    @Override
    public BusomState next() throws IOException {
        receivePublicKeysAndCertificates();
        MeterKey meterKey = new MeterKey(privateKey, generalKey);
        return new SendChunk(meterKey, loadCurve, connection);
    }

    /**
     * Receives the public keys and certificates. Adds all the public keys to
     * generate the general Key
     *
     * @throws IOException If connection is closed, or have some other troubles.
     */
    protected void receivePublicKeysAndCertificates() throws IOException {
        generalKey = generator.getGroup().getNeuterElement();
        SMSDatagram data;
        data = receiverMeter.receive();
        while (data instanceof NeighborhoodDatagram) {
            //noinspection unchecked cast
            NeighborhoodDatagram<String> dataN = (NeighborhoodDatagram<String>) data;
            if (dataN.validate(this.validation))
                generalKey = generalKey.multiply(dataN.getPublicKey());
            data = receiverMeter.receive();
        }
    }

    /**
     * Sets Receiver for tests
     *
     * @param receiverMeter receiverMeter to be set
     */
    public void setReceiverMeter(ReceiverMeter receiverMeter) {
        this.receiverMeter = receiverMeter;
    }

    /**
     * Used for tests, it sets the generalKey
     *
     * @return the general Key of the meters
     */
    protected GroupElement getGeneralKey() {
        return this.generalKey;
    }

    /**
     * Sets class to validate the certificate. Used in tests.
     *
     * @param validation class containing one method to validate the
     *                   group Element and certificate.
     */
    public void setValidation(CertificateValidation<String> validation) {
        this.validation = validation;
    }
}
