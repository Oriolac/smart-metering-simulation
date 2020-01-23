package udl.cig.sms.busom.substation;

import cat.udl.cig.ecc.ECPrimeOrderSubgroup;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.Sender;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;

import java.util.List;

public class BusomSubstationSetup implements BusomState {

    private final ECPrimeOrderSubgroup group;
    private Receiver receiver;
    private Sender sender;
    private List<NeighborhoodDatagram> datagrams;

    public BusomSubstationSetup(ECPrimeOrderSubgroup group) {
        this.group = group;
    }

    @Override
    public BusomState next() {
        return null;
    }

    protected void receiveAndComputePublicKey() {

    }

    protected void sendPublicKey() {

    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void setDatagrams(List<NeighborhoodDatagram> datagrams) {
        this.datagrams = datagrams;
    }
}
