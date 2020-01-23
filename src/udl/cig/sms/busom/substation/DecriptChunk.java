package udl.cig.sms.busom.substation;

import cat.udl.cig.fields.Group;
import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.Receiver;

import java.math.BigInteger;

public class DecriptChunk implements BusomState {

    private Group group;
    private GroupElement partialDecryption;
    private Receiver receiver;

    public DecriptChunk(Group group) {
        this.group = group;
    }

    @Override
    public BusomState next() {
        return null;
    }

    protected void receiveAndCompute() {

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
