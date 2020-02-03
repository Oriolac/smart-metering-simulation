package udl.cig.sms.busom.substation;

import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.cryptography.cryptosystems.ciphertexts.HomomorphicCiphertext;
import cat.udl.cig.fields.GroupElement;
import cat.udl.cig.fields.MultiplicativeSubgroup;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.ConnectionSubstationInt;
import udl.cig.sms.connection.ReceiverSubstation;
import udl.cig.sms.connection.Sender;
import udl.cig.sms.connection.datagram.CipherTextDatagram;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;
import java.util.List;

public class ReceiveChunk implements BusomState {

    private final MultiplicativeSubgroup group;
    private ConnectionSubstationInt connection;
    private Sender sender;
    private ReceiverSubstation receiver;
    private HomomorphicCiphertext ciphertext;

    public ReceiveChunk(MultiplicativeSubgroup group) {
        this.group = group;
    }

    public ReceiveChunk(MultiplicativeSubgroup group, ConnectionSubstationInt connection) {
        this.group = group;
        receiver = connection;
        sender = connection;
        this.connection = connection;
    }

    @Override
    public BusomState next() throws IOException {
        receiveAndCompute();
        sendC();
        return new DecriptChunk(group, ciphertext, connection);
    }

    protected void receiveAndCompute() {
        List<SMSDatagram> datas;
        try {
            datas = receiver.receive();
            GroupElement[] elements = new GroupElement[]{group.getNeuterElement(), group.getNeuterElement()};
            ciphertext = new ElGamalCiphertext(elements);
            for (SMSDatagram data : datas) {
                if (data instanceof CipherTextDatagram) {
                    CipherTextDatagram cipherTextDatagram = (CipherTextDatagram) data;
                    compute(cipherTextDatagram);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compute(CipherTextDatagram cipherTextDatagram) {
        ciphertext = ciphertext.HomomorphicOperation(cipherTextDatagram.getCiphertext());
    }

    protected void sendC() throws IOException {
        sender.send(new GroupElementDatagram(ciphertext.getParts()[0]));
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void setReceiver(ReceiverSubstation receiver) {
        this.receiver = receiver;
    }

    public HomomorphicCiphertext getCiphertext() {
        return ciphertext;
    }

    public void setCipherText(HomomorphicCiphertext ciphertext) {
        this.ciphertext = ciphertext;
    }

}

