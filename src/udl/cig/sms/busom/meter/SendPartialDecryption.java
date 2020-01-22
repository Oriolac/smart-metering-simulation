package udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;

public class SendPartialDecryption implements BusomState {
    @Override
    public BusomState next() {
        return this;
    }

    protected GroupElement generatePartialDecryption() {
        return null;
    }

    protected void sendDecryption() {

    }

}
