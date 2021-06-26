package cat.udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.MultiplicativeSubgroup;
import cat.udl.cig.sms.busom.BusomMeterState;
import cat.udl.cig.sms.busom.BusomSubstationState;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.connection.ReceiverSubstation;
import cat.udl.cig.sms.connection.datagram.GroupElementDatagram;
import cat.udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Decripts Chunk, requesting the data necessary to be computed.
 */
public class DecriptChunk implements BusomSubstationState {

    private final HomomorphicCiphertext ciphertext;
    private final SubstationBusomContextInt substationBusomContextInt;
    private ConnectionSubstationInt connection;
    private final MultiplicativeSubgroup group;
    private GroupElement partialDecryption;
    private ReceiverSubstation receiver;
    private static final Logger LOGGER = Logger.getAnonymousLogger();

    /**
     * Generates DecriptChunkState.
     *
     * @param group      Group be needed to encrypt and decrypt.
     * @param ciphertext [C, D], used to compute the adding of messages.
     * @param substationBusomContextInt context of state-machine context.
     */
    public DecriptChunk(MultiplicativeSubgroup group,
                        HomomorphicCiphertext ciphertext, SubstationBusomContextInt substationBusomContextInt) {
        this.group = group;
        partialDecryption = group.getNeuterElement();
        this.ciphertext = ciphertext;
        this.substationBusomContextInt = substationBusomContextInt;
        this.connection = substationBusomContextInt.getConnection();
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
        return this.substationBusomContextInt.makeReceiveChunk(group);
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
        return substationBusomContextInt.getDiscreteLogarithmAlgorithm().algorithm(partialDecryption);
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
     * Gets the partialDecryption.
     *
     * @return partialDecryption
     */
    protected GroupElement getPartialDecryption() {
        return partialDecryption;
    }
}
