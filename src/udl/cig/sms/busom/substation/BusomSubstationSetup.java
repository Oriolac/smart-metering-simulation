package udl.cig.sms.busom.substation;

import cat.udl.cig.fields.MultiplicativeSubgroup;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.CertificateValidation;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.SMSDatagram;
import udl.cig.sms.connection.Sender;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;

import java.util.ArrayList;
import java.util.List;

public class BusomSubstationSetup implements BusomState {

    private final MultiplicativeSubgroup group;
    private Receiver receiver;
    private Sender sender;
    private List<NeighborhoodDatagram<String>> datagrams;
    private CertificateValidation<String> validation;

    public BusomSubstationSetup(MultiplicativeSubgroup group) {
        this.group = group;
        datagrams = new ArrayList<>();
    }

    @Override
    public BusomState next() {
        receivePublicKeys();
        sendPublicKey();
        return new ReceiveChunk(group);
    }

    @SuppressWarnings("unchecked cast")
    protected void receivePublicKeys() {
        SMSDatagram data = receiver.receive();
        while (data instanceof NeighborhoodDatagram) {
            NeighborhoodDatagram<String> neighbourData = (NeighborhoodDatagram<String>) data;
            receivePublicMeterKey(neighbourData);
            data = receiver.receive();
        }
    }

    private void receivePublicMeterKey(NeighborhoodDatagram<String> data) {
        if (data.validate(validation))
            datagrams.add(data);
    }


    protected void sendPublicKey() {
        for (NeighborhoodDatagram<String> data : datagrams) {
            sender.send(data);
        }
    }

    public void setValidation(CertificateValidation<String> validation) {
        this.validation = validation;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void setDatagrams(List<NeighborhoodDatagram<String>> datagrams) {
        this.datagrams = datagrams;
    }
}
