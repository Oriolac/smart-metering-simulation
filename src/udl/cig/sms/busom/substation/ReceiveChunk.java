package udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.MultiplicativeSubgroup;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.ConnectionSubstationInt;
import udl.cig.sms.connection.ReceiverSubstation;
import udl.cig.sms.connection.Sender;
import udl.cig.sms.connection.datagram.CipherTextDatagram;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;
import java.util.List;

/**
 * Receive chunk state of protocol
 */
public class ReceiveChunk implements BusomState {

    private final MultiplicativeSubgroup group;
    private ConnectionSubstationInt connection;
    private Sender sender;
    private ReceiverSubstation receiver;
    private HomomorphicCiphertext ciphertext;

    /**
     * Genereates a ReceiveChunk state. Used for testing.
     *
     * @param group Group used for encrypt and decrypt.
     */
    public ReceiveChunk(MultiplicativeSubgroup group) {
        this.group = group;
    }

    /**
     * Generates a ReceiverChunk state.
     *
     * @param group      Group used for encrypt and decrypt.
     * @param connection to all the meters.
     */
    public ReceiveChunk(MultiplicativeSubgroup group, ConnectionSubstationInt connection) {
        this.group = group;
        receiver = connection;
        sender = connection;
        this.connection = connection;
    }

    /**
     * Consumes the messages and goes to the next stage of the protocol
     *
     * @return next state (it's always DecriptChunk).
     * @throws IOException if connection fails.
     */
    @Override
    public DecriptChunk next() throws IOException {
        receiveAndCompute();
        sendC();
        return new DecriptChunk(group, ciphertext, connection);
    }

    /**
     * Receives and computes the encription of the first value.
     * The computation is based on adding all the points of ElGamal, as then
     * it can be used to decrypt without giving the information of xi.
     */
    protected void receiveAndCompute() {
        List<SMSDatagram> datas;
        try {
            datas = receiver.receive();
            GroupElement[] elements = new GroupElement[]{group.getNeuterElement(), group.getNeuterElement()};
            ciphertext = new ElGamalCiphertext(elements);
            for (SMSDatagram data : datas) {
                if (data instanceof CipherTextDatagram) {
                    CipherTextDatagram cipherTextDatagram = (CipherTextDatagram) data;
                    compute(cipherTextDatagram);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Computes the adding of ci and di. ElGamalCipherText is the pair of values.
     *
     * @param cipherTextDatagram Pair of values to be added.
     */
    private void compute(CipherTextDatagram cipherTextDatagram) {
        ciphertext = ciphertext.HomomorphicOperation(cipherTextDatagram.getCiphertext());
    }

    /**
     * Sends C to all the meters, where C = sum(ci)
     *
     * @throws IOException if connection fails.
     */
    protected void sendC() throws IOException {
        sender.send(new GroupElementDatagram(ciphertext.getParts()[0]));
    }

    /**
     * Sets sender. Used for testing
     *
     * @param sender mock of sender
     */
    public void setSender(Sender sender) {
        this.sender = sender;
    }

    /**
     * Sets the receiver. Used for testing.
     *
     * @param receiver mock of receiver.
     */
    public void setReceiver(ReceiverSubstation receiver) {
        this.receiver = receiver;
    }

    /**
     * Gets actual ciphertext. As it can be calledonly after and before
     * of receive and Compute, it can only return null or [C, D] as a Ciphertext
     *
     * @return [C, D] if computed, else null
     */
    public HomomorphicCiphertext getCiphertext() {
        return ciphertext;
    }

    /**
     * Sets cipertext. Used for testing.
     *
     * @param ciphertext to be set.
     */
    public void setCipherText(HomomorphicCiphertext ciphertext) {
        this.ciphertext = ciphertext;
    }

}

