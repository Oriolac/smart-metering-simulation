package udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.MultiplicativeSubgroup;
import cat.udl.cig.operations.wrapper.LogarithmAlgorithm;
import cat.udl.cig.operations.wrapper.PollardsLambda;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

public class DecriptChunk implements BusomState {

    private final HomomorphicCiphertext ciphertext;
    private LogarithmAlgorithm logarithm;
    private MultiplicativeSubgroup group;
    private GroupElement partialDecryption;
    private Receiver receiver;

    public DecriptChunk(MultiplicativeSubgroup group, HomomorphicCiphertext ciphertext) {
        this.group = group;
        partialDecryption = group.getNeuterElement();
        this.ciphertext = ciphertext;
        logarithm = new PollardsLambda(group.getGenerator());
    }

    @Override
    public BusomState next() {
        receiveAndCompute();
        return new ReceiveChunk(group);
    }

    protected void receiveAndCompute() {
        SMSDatagram data;
        try {
            data = receiver.receive();
            while (data instanceof GroupElementDatagram) {
                GroupElementDatagram groupElementDatagram = (GroupElementDatagram) data;
                compute(groupElementDatagram);
                data = receiver.receive();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }
}
