package cat.udl.cig.sms.busom.meter;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.sms.busom.BusomMeterState;
import cat.udl.cig.sms.busom.data.MeterKey;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.connection.KeyRenewalException;
import cat.udl.cig.sms.connection.ReceiverMeter;
import cat.udl.cig.sms.connection.Sender;
import cat.udl.cig.sms.connection.datagram.GroupElementDatagram;
import cat.udl.cig.sms.connection.datagram.KeyRenewalDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Sends partial Decryption to substation.
 */
public class SendPartialDecryption implements BusomMeterState {

    private final MeterKey meterKey;
    private final BigInteger noise;
    private final CurveConfiguration curveConfiguration;
    private final GeneralECPoint generator;
    private ConnectionMeterInt connection;
    private ReceiverMeter receiverMeter;
    private Sender sender;
    private GroupElement partialDecryption;

    /**
     * Generates state
     *
     * @param meterKey  container of private and general Key
     * @param noise     noise used for encryption
     * @param curveConfiguration parameters of ECC
     */
    protected SendPartialDecryption(MeterKey meterKey, BigInteger noise, CurveConfiguration curveConfiguration) {
        this.meterKey = meterKey;
        this.noise = noise;
        this.generator = curveConfiguration.getGroup().getGenerator();
        this.curveConfiguration = curveConfiguration;
    }

    /**
     * Generates state
     *
     * @param meterKey   container of private and general Key
     * @param noise      noise used for encryption
     * @param curveConfiguration  parameters of ECC
     * @param connection to the substation.
     */
    protected SendPartialDecryption(MeterKey meterKey, BigInteger noise,
                                    CurveConfiguration curveConfiguration, ConnectionMeterInt connection) {
        this(meterKey, noise, curveConfiguration);
        this.connection = connection;
        this.sender = connection;
        this.receiverMeter = connection;
    }

    /**
     * Next state of the protocol.
     *
     * @return next state given a preset of conditions
     * @throws IOException, if connection has failed.
     */
    @Override
    public BusomMeterState next() throws IOException, KeyRenewalException {
        partialDecryption = generatePartialDecryption();
        sendDecryption();
        return new SendChunk(meterKey, curveConfiguration, connection);
    }

    /**
     * Generates partial decryption
     *
     * @return partial decryption or null if connection fails to receive.
     */
    protected GroupElement generatePartialDecryption() throws KeyRenewalException {
        SMSDatagram data;
        try {
            data = receiverMeter.receive();
            if (data instanceof GroupElementDatagram) {
                GroupElementDatagram elementDatagram = (GroupElementDatagram) data;
                return elementDatagram.getElement().pow(meterKey.getPrivateKey()).multiply(generator.pow(noise));
            } else if (data instanceof KeyRenewalDatagram) {
                throw new KeyRenewalException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sends decryption.
     *
     * @throws IOException fails if and only if connection fails.
     */
    protected void sendDecryption() throws IOException {
        sender.send(new GroupElementDatagram(partialDecryption));
    }

    /**
     * Sets receiver. Used for testing
     *
     * @param receiverMeter mock of receiver.
     */
    public void setReceiverMeter(ReceiverMeter receiverMeter) {
        this.receiverMeter = receiverMeter;
    }

    /**
     * Sets sender used for tests.
     *
     * @param sender mock of sender.
     */
    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
