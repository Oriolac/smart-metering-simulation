package cat.udl.cig.sms.busom.meter;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.sms.busom.BusomState;
import cat.udl.cig.sms.busom.data.MeterKey;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.connection.ReceiverMeter;
import cat.udl.cig.sms.connection.Sender;
import cat.udl.cig.sms.connection.datagram.GroupElementDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;
import cat.udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Sends partial Decryption to substation.
 */
public class SendPartialDecryption implements BusomState {

    private final MeterKey meterKey;
    private final BigInteger noise;
    private final LoadCurve loadCurve;
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
     * @param loadCurve parameters of ECC
     */
    protected SendPartialDecryption(MeterKey meterKey, BigInteger noise, LoadCurve loadCurve) {
        this.meterKey = meterKey;
        this.noise = noise;
        this.generator = loadCurve.getGroup().getGenerator();
        this.loadCurve = loadCurve;
    }

    /**
     * Generates state
     *
     * @param meterKey   container of private and general Key
     * @param noise      noise used for encryption
     * @param loadCurve  parameters of ECC
     * @param connection to the substation.
     */
    protected SendPartialDecryption(MeterKey meterKey, BigInteger noise,
                                    LoadCurve loadCurve, ConnectionMeterInt connection) {
        this(meterKey, noise, loadCurve);
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
    public BusomState next() throws IOException {
        partialDecryption = generatePartialDecryption();
        sendDecryption();
        return new SendChunk(meterKey, loadCurve, connection);
    }

    /**
     * Generates partial decryption
     *
     * @return partial decryption or null if connection fails to receive.
     */
    protected GroupElement generatePartialDecryption() {
        //TODO: S'han de vigilar els errors
        SMSDatagram data;
        try {
            data = receiverMeter.receive();
            if (data instanceof GroupElementDatagram) {
                GroupElementDatagram elementDatagram = (GroupElementDatagram) data;
                return elementDatagram.getElement().pow(meterKey.getPrivateKey()).multiply(generator.pow(noise));
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
