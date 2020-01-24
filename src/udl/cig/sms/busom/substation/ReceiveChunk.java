package udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.Group;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.SMSDatagram;
import udl.cig.sms.connection.Sender;
import udl.cig.sms.connection.datagram.CipherTextDatagram;

public class ReceiveChunk implements BusomState {

    private final Group group;
    private Sender sender;
    private Receiver receiver;
    private HomomorphicCiphertext ciphertext;

    public ReceiveChunk(Group group) {
        this.group = group;
    }

    @Override
    public BusomState next() {
        receiveAndCompute();
        sendC();
        return null;
    }

    protected void receiveAndCompute() {
        SMSDatagram data = receiver.receive();
        while (data instanceof CipherTextDatagram) {
            CipherTextDatagram cipherTextDatagram= (CipherTextDatagram) data;
            compute(cipherTextDatagram);
            data = receiver.receive();
        }
    }

    private void compute(CipherTextDatagram cipherTextDatagram) {
        ciphertext.HomomorphicOperation(cipherTextDatagram.getCiphertext());
    }

    protected void sendC() {
        sender.send(new CipherTextDatagram(ciphertext));
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

