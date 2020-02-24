package udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.ConnectionMeterInt;
import udl.cig.sms.connection.Sender;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

/**
 * Sets up parameters needed for the protocol
 */
public class BusomSetUp implements BusomState {


    private final GroupElement generator;
    private final String certificate;
    private final LoadCurve loadCurve;
    private ConnectionMeterInt connection;
    private BigInteger privateKey;
    private GroupElement publicKey;
    private Sender sender;

    /**
     * Sets up the state.
     *
     * @param certificate certificate to validate the point in the meter
     * @param loadCurve   The curve and all the parameters needed to encrypt
     *                    and decrypt data.
     */
    public BusomSetUp(String certificate, LoadCurve loadCurve) {
        this.certificate = certificate;
        this.loadCurve = loadCurve;
        this.generator = loadCurve.getGroup().getGenerator();
    }

    /**
     * Sets up the state.
     *
     * @param certificate certificate to validate the point in the meter
     * @param loadCurve   The curve and all the parameters needed to encrypt
     *                    and decrypt data.
     * @param connection  connection to the substation.
     */
    public BusomSetUp(String certificate, LoadCurve loadCurve, ConnectionMeterInt connection) {
        this(certificate, loadCurve);
        this.connection = connection;
        sender = connection;
    }


    /**
     * Next state of the protocol.
     *
     * @return next state given a preset of conditions
     * @throws IOException, if connection has failed.
     */
    @Override
    public BusomState next() throws IOException {
        generatePrivateKey();
        calculatePublicKey();
        sendPublicKey();
        return new NeighborhoodSetUp(privateKey, loadCurve, connection);
    }

    /**
     * Generates the privateKey of the meter
     */
    protected void generatePrivateKey() {
        Random random = new Random();
        long key = random.nextLong();
        privateKey = BigInteger.valueOf(Math.abs(key));


    }

    /**
     * Calculates the private key of the meter (pk * G)
     */
    protected void calculatePublicKey() {
        publicKey = (privateKey == null)
                ? null
                : generator.pow(privateKey);
    }

    /**
     * Sends public Key to the server
     *
     * @throws IOException If connection fails.
     */
    protected void sendPublicKey() throws IOException {
        if (publicKey != null)
            sender.send(new NeighborhoodDatagram<>(publicKey, certificate));
    }

    /**
     * Setters used for tests
     *
     * @param sender sender to be set
     */
    protected void setSender(Sender sender) {
        this.sender = sender;
    }

    /**
     * Getter used for tests
     *
     * @return the public key of the meter
     */
    protected GroupElement getPublicKey() {
        return publicKey;
    }

    /**
     * Gets the private Key. Used for tests
     *
     * @return the private key if the meter
     */
    protected BigInteger getPrivateKey() {
        return privateKey;
    }
}
