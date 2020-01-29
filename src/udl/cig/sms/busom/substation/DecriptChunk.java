package udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.MultiplicativeSubgroup;
import cat.udl.cig.operations.wrapper.LogarithmAlgorithm;
import cat.udl.cig.operations.wrapper.PollardsLambda;
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

public class DecriptChunk implements BusomState {

    private final HomomorphicCiphertext ciphertext;
    private ConnectionSubstationInt connection;
    private LogarithmAlgorithm logarithm;
    private MultiplicativeSubgroup group;
    private GroupElement partialDecryption;
    private ReceiverSubstation receiver;
    private static final Logger LOGGER = Logger.getAnonymousLogger();

    public DecriptChunk(MultiplicativeSubgroup group, HomomorphicCiphertext ciphertext) {
        this.group = group;
        partialDecryption = group.getNeuterElement();
        this.ciphertext = ciphertext;
        // TODO : When PollardsLambda is working, change the next line to it
        logarithm = new PollardsLambda(group.getGenerator());
    }

    public DecriptChunk(MultiplicativeSubgroup group,
                        HomomorphicCiphertext ciphertext, ConnectionSubstationInt connection) {
        this.group = group;
        partialDecryption = group.getNeuterElement();
        this.ciphertext = ciphertext;
        // TODO : When PollardsLambda is working, change the next line to it
        logarithm = new PollardsLambda(group.getGenerator());
        this.connection = connection;
        receiver = connection;
    }

    @Override
    public BusomState next() throws IOException {
        receiveAndCompute();
        return new ReceiveChunk(group, connection);
    }

    protected void receiveAndCompute() throws IOException {
        List<SMSDatagram> datas;
        datas = receiver.receive();
        for(SMSDatagram data : datas) {
            if (data instanceof GroupElementDatagram) {
                GroupElementDatagram groupElementDatagram = (GroupElementDatagram) data;
                compute(groupElementDatagram);
            } else {
                LOGGER.log(Level.WARNING, "Not permitted data type.");
            }
        }
        GroupElement d = (GroupElement) ciphertext.getParts()[1];
        partialDecryption = partialDecryption.inverse().multiply(d);
    }

    private void compute(GroupElementDatagram groupElementDatagram) {
        partialDecryption = partialDecryption.multiply(groupElementDatagram.getElement());
    }

    public Optional<BigInteger> readMessage() {
        return logarithm.algorithm(partialDecryption);
    }

    protected void setPartialDecryption(GroupElement D) {
        this.partialDecryption = D;
    }

    public void setLogarithm(LogarithmAlgorithm logarithm) {
        this.logarithm = logarithm;
    }

    protected GroupElement getPartialDecryption() {
        return partialDecryption;
    }

    public void setReceiver(ReceiverSubstation receiver) {
        this.receiver = receiver;
    }
}
