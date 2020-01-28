package udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.MultiplicativeSubgroup;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.Sender;
import udl.cig.sms.connection.datagram.CipherTextDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;

public class ReceiveChunk implements BusomState {

    private final MultiplicativeSubgroup group;
    private Sender sender;
    private Receiver receiver;
    private HomomorphicCiphertext ciphertext;

    public ReceiveChunk(MultiplicativeSubgroup group) {
        this.group = group;
    }

    @Override
    public BusomState next() {
        receiveAndCompute();
        sendC();
        return new DecriptChunk(group, ciphertext);
    }

    protected void receiveAndCompute() {
        SMSDatagram data;
        try {
            data = receiver.receive();
            GroupElement[] elements = new GroupElement[]{group.getNeuterElement(), group.getNeuterElement()};
            ciphertext = new ElGamalCiphertext(elements);
            while (data instanceof CipherTextDatagram) {
                CipherTextDatagram cipherTextDatagram = (CipherTextDatagram) data;
                compute(cipherTextDatagram);
                data = receiver.receive();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compute(CipherTextDatagram cipherTextDatagram) {
        ciphertext = ciphertext.HomomorphicOperation(cipherTextDatagram.getCiphertext());
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

