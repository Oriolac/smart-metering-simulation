package udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.Sender;

import java.math.BigInteger;

public class SendPartialDecryption implements BusomState {

    private final BigInteger privateKey;
    private final BigInteger noise;
    private Receiver receiver;
    private Sender sender;

    protected SendPartialDecryption(BigInteger privateKey, BigInteger noise) {
        this.privateKey = privateKey;
        this.noise = noise;
    }

    @Override
    public BusomState next() {
        return this;
    }

    protected GroupElement generatePartialDecryption() {
        return null;
    }

    protected void sendDecryption() {

    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
