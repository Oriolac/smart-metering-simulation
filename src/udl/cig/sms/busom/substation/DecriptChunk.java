package udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.MultiplicativeSubgroup;
import cat.udl.cig.operations.wrapper.BruteForce;
import cat.udl.cig.operations.wrapper.HashedAlgorithm;
import cat.udl.cig.operations.wrapper.LogarithmAlgorithm;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.ConnectionSubstationInt;
import udl.cig.sms.connection.ReceiverSubstation;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Decripts Chunk, requesting the data necessary to be computed.
 */
public class DecriptChunk implements BusomState {

    private final HomomorphicCiphertext ciphertext;
    private ConnectionSubstationInt connection;
    private LogarithmAlgorithm logarithm;
    private MultiplicativeSubgroup group;
    private GroupElement partialDecryption;
    private ReceiverSubstation receiver;
    private static final Logger LOGGER = Logger.getAnonymousLogger();

    /**
     * Generates DecriptChunkState for testing.
     *
     * @param group      Group be needed to encrypt and decrypt.
     * @param ciphertext [C, D], used to compute the adding of messages.
     */
    public DecriptChunk(MultiplicativeSubgroup group, HomomorphicCiphertext ciphertext) {
        this.group = group;
        partialDecryption = group.getNeuterElement();
        this.ciphertext = ciphertext;
        logarithm = new BruteForce(group.getGenerator());
    }

    /**
     * Generates DecriptChunkState.
     *
     * @param group      Group be needed to encrypt and decrypt.
     * @param ciphertext [C, D], used to compute the adding of messages.
     * @param connection to all the meters.
     */
    public DecriptChunk(MultiplicativeSubgroup group,
                        HomomorphicCiphertext ciphertext, ConnectionSubstationInt connection) {
        this.group = group;
        partialDecryption = group.getNeuterElement();
        this.ciphertext = ciphertext;
        logarithm = HashedAlgorithm.getHashedInstance();
        this.connection = connection;
        receiver = connection;
    }

    /**
     * Makes the next state. It also generates the message readed. Needs to be extracted by a get
     * method.
     *
     * @return Next state. It's always ReceiveChunk.
     * @throws IOException if connection fails.
     */
    @Override
    public ReceiveChunk next() throws IOException {
        receiveAndCompute();
        return new ReceiveChunk(group, connection);
    }

    /**
     * Receives and computes the sum of messages.
     *
     * @throws IOException if connection fails
     */
    protected void receiveAndCompute() throws IOException {
        List<SMSDatagram> datas;
        datas = receiver.receive();
        for (SMSDatagram data : datas) {
            if (data instanceof GroupElementDatagram) {
                GroupElementDatagram groupElementDatagram = (GroupElementDatagram) data;
                compute(groupElementDatagram);
            } else {
                LOGGER.log(Level.WARNING, "Not permitted data type.");
            }
        }
        GroupElement d = ciphertext.getParts()[1];
        partialDecryption = partialDecryption.inverse().multiply(d);
    }

    /**
     * Computes a partialDecription.
     *
     * @param groupElementDatagram to be computed.
     */
    private void compute(GroupElementDatagram groupElementDatagram) {
        partialDecryption = partialDecryption.multiply(groupElementDatagram.getElement());
    }

    /**
     * Reads the sum of messages.
     *
     * @return Optional of the read message (it can fail)
     */
    public Optional<BigInteger> readMessage() {
        return logarithm.algorithm(partialDecryption);
    }

    /**
     * Sets the partialDecription to the element D. Used for testing
     *
     * @param D to be used.
     */
    protected void setPartialDecryption(GroupElement D) {
        this.partialDecryption = D;
    }

    /**
     * Sets the logarithm algorithm
     *
     * @param logarithm to be set.
     */
    public void setLogarithm(LogarithmAlgorithm logarithm) {
        this.logarithm = logarithm;
    }

    /**
     * Gets the partialDecryption.
     *
     * @return partialDecryption
     */
    protected GroupElement getPartialDecryption() {
        return partialDecryption;
    }

    /**
     * Sets a mock of receiver.
     *
     * @param receiver mock of receiver
     */
    public void setReceiver(ReceiverSubstation receiver) {
        this.receiver = receiver;
    }
}
