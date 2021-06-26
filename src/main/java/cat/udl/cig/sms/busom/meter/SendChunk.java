package cat.udl.cig.sms.busom.meter;

import cat.udl.cig.cryptography.cryptosystems.ElGamalCypher;
import cat.udl.cig.cryptography.cryptosystems.HomomorphicCypher;
import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.sms.busom.BusomMeterState;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.busom.data.MeterKey;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.connection.Sender;
import cat.udl.cig.sms.connection.datagram.CipherTextDatagram;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

/**
 * Sends a chunk of a message to the substation
 */
public class SendChunk implements BusomMeterState {

    private final GroupElement generator;
    private final CurveConfiguration curveConfiguration;
    private final MeterKey meterKey;
    private ConnectionMeterInt connection;
    private BigInteger message;
    private Sender sender;
    private HomomorphicCypher cypher;
    private HomomorphicCiphertext ciphertext;

    /**
     * Constructs the sender
     *
     * @param meterKey  Parameter containing information to encrypt in this meter
     * @param curveConfiguration Parameters of the ECC curve.
     */
    protected SendChunk(MeterKey meterKey, CurveConfiguration curveConfiguration) {
        this.curveConfiguration = curveConfiguration;
        this.generator = curveConfiguration.getGroup().getGenerator();
        this.meterKey = meterKey;
    }

    /**
     * Constructs the sender
     *
     * @param meterKey   Parameter containing information to encrypt in this meter
     * @param curveConfiguration  Parameters of the ECC curve.
     * @param connection connection to the substation.
     */
    protected SendChunk(MeterKey meterKey, CurveConfiguration curveConfiguration, ConnectionMeterInt connection) {
        this(meterKey, curveConfiguration);
        this.connection = connection;
        this.sender = connection;
    }


    /**
     * Next state of the protocol.
     *
     * @return next state given a preset of conditions
     * @throws NullMessageException, if the message to be sent is void
     * @throws IOException,          if connection has failed.
     */
    @Override
    public BusomMeterState next() throws NullMessageException, IOException {
        BigInteger noise = SendChunk.generateNoise();
        if (message == null)
            throw new NullMessageException();
        GroupElement point = this.generator.pow(message.add(noise));
        cypher = new ElGamalCypher(curveConfiguration.getGroup(), generator, meterKey.getGeneralKey());
        ciphertext = cypher.encrypt(point);
        sendCypherText();
        return new SendPartialDecryption(meterKey, noise, curveConfiguration, connection);
    }

    /**
     * Generates noise to secure encrytion
     *
     * @return noise
     */
    static protected BigInteger generateNoise() {
        Random randomGen = new Random();
        long random = randomGen.nextLong();
        return BigInteger.valueOf(random);
    }

    /**
     * Sends cyphered text to the substation.
     *
     * @throws IOException if sender can't send
     */
    protected void sendCypherText() throws IOException {
        sender.send(new CipherTextDatagram(ciphertext));
    }

    /**
     * Gets privateKey. Used for tests
     *
     * @return private key of the meter
     */
    protected BigInteger getPrivateKey() {
        return meterKey.getPrivateKey();
    }

    /**
     * Sets message to be send. It's mandatory to be used before sending
     * information to the substation, aka call next method.
     *
     * @param message to be sent.
     */
    public void setMessage(BigInteger message) {
        this.message = message;
    }

    /**
     * Sets sender. Used for testing.
     *
     * @param sender mock of it.
     */
    protected void setSender(Sender sender) {
        this.sender = sender;
    }

    /**
     * Sets cypher. Used for testing.
     *
     * @param cypher mock of it.
     */
    protected void setCypher(HomomorphicCypher cypher) {
        this.cypher = cypher;
    }

}
