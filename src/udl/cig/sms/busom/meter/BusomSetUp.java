package udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.connection.Sender;

import java.math.BigInteger;
import java.util.Optional;

public class BusomSetUp implements BusomState {


    public BusomSetUp(String certificate) {}

    @Override
    public BusomState next() {
        return this;
    }

    protected BigInteger generatePrivateKey() {
        return null;
    }

    protected Optional<GroupElement> calculatePublicKey() {
        return Optional.empty();
    }

    protected void sendPublicKey() {

    }

    protected void setSender(Sender sender) {

    }

}
