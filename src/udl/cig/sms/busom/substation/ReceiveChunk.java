package udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.Group;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.Sender;

public class ReceiveChunk implements BusomState {

    private final Group group;
    private Sender sender;
    private Receiver receiver;
    private HomomorphicCiphertext ciphertext;

    public ReceiveChunk(Group group){
        this.group = group;
    }

    @Override
    public BusomState next() {
        return null;
    }

    protected void receiveAndCompute() {

    }

    protected void sendC() {

    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public HomomorphicCiphertext getCiphertext() {
        return ciphertext;
    }

    protected void setCipherText(ElGamalCiphertext ciphertext) {
    }
}
