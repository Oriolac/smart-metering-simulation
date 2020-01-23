package udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.Receiver;

import java.math.BigInteger;

public class NeighborhoodSetUp implements BusomState {


    private GroupElement generalKey;

    protected NeighborhoodSetUp(BigInteger privateKey) {

    }

    @Override
    public BusomState next() {
        return this;
    }

    protected void setGeneralKey() {
    }

    protected void receivePublicKeysAndCertificates() {

    }


    public void setReceiver(Receiver receiver) {
    }

    protected GroupElement getGeneralKey() {
        return this.generalKey;
    }
}
