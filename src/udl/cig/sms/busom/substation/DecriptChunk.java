package udl.cig.sms.busom.substation;

import cat.udl.cig.fields.Group;
import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.SMSDatagram;
import udl.cig.sms.connection.datagram.GroupElementDatagram;

import java.math.BigInteger;

public class DecriptChunk implements BusomState {

    private Group group;
    private GroupElement partialDecryption;
    private Receiver receiver;

    public DecriptChunk(Group group) {
        this.group = group;
        partialDecryption = group.getNeuterElement();
    }

    @Override
    public BusomState next() {
        receiveAndCompute();
        return new ReceiveChunk(group);
    }

    protected void receiveAndCompute() {
        SMSDatagram data = receiver.receive();
        while (data instanceof GroupElementDatagram) {
            GroupElementDatagram groupElementDatagram = (GroupElementDatagram) data;
            compute(groupElementDatagram);
            data = receiver.receive();
        }
    }

    private void compute(GroupElementDatagram groupElementDatagram) {
        partialDecryption.multiply(groupElementDatagram.getElement());
    }

    public BigInteger readMessage() {
        return null;
    }

    protected void setPartialDecryption(GroupElement D) {
        this.partialDecryption = D;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }
}
