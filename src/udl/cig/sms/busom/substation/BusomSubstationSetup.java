package udl.cig.sms.busom.substation;

import cat.udl.cig.fields.MultiplicativeSubgroup;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.certificate.CertificateTrueMock;
import udl.cig.sms.busom.certificate.CertificateValidation;
import udl.cig.sms.connection.ConnectionSubstationInt;
import udl.cig.sms.connection.ReceiverSubstation;
import udl.cig.sms.connection.Sender;
import udl.cig.sms.connection.datagram.EndOfDatagram;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BusomSubstationSetup implements BusomState {

    private final MultiplicativeSubgroup group;
    private ConnectionSubstationInt connection;
    private ReceiverSubstation receiver;
    private Sender sender;
    private List<NeighborhoodDatagram<String>> datagrams;
    private CertificateValidation<String> validation;
    private static final Logger LOGGER = Logger.getAnonymousLogger();

    protected BusomSubstationSetup(MultiplicativeSubgroup group) {
        this.group = group;
        datagrams = new ArrayList<>();
    }

    public BusomSubstationSetup(MultiplicativeSubgroup group, ConnectionSubstationInt connection) {
        this.group = group;
        datagrams = new ArrayList<>();
        this.connection = connection;
        this.sender = connection;
        this.receiver = connection;
        this.validation = new CertificateTrueMock<>();
    }

    @Override
    public BusomState next() throws IOException {
        receivePublicKeys();
        sendPublicKey();
        return new ReceiveChunk(group, connection);
    }

    @SuppressWarnings("unchecked cast")
    protected void receivePublicKeys() throws IOException {
        List<SMSDatagram> datas;
        datas = receiver.receive();
        for (SMSDatagram data : datas) {
            if (data instanceof NeighborhoodDatagram) {
                NeighborhoodDatagram<String> neighbourData = (NeighborhoodDatagram<String>) data;
                receivePublicMeterKey(neighbourData);
            } else {
                LOGGER.log(Level.WARNING, "Not permitted data type.");
            }
        }
    }

    private void receivePublicMeterKey(NeighborhoodDatagram<String> data) {
        if (data.validate(validation))
            datagrams.add(data);
    }


    protected void sendPublicKey() throws IOException {
        for (NeighborhoodDatagram<String> data : datagrams) {
            sender.send(data);
        }
        sender.send(new EndOfDatagram());
    }

    public void setValidation(CertificateValidation<String> validation) {
        this.validation = validation;
    }

    public void setReceiver(ReceiverSubstation receiver) {
        this.receiver = receiver;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void setDatagrams(List<NeighborhoodDatagram<String>> datagrams) {
        this.datagrams = datagrams;
    }
}
