package udl.cig.sms.busom.meter;

import cat.udl.cig.ecc.GeneralECPoint;
import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.data.MeterKey;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.Sender;
import udl.cig.sms.connection.datagram.GroupElementDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.crypt.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;

public class SendPartialDecryption implements BusomState {

    private final MeterKey meterKey;
    private final BigInteger noise;
    private final LoadCurve loadCurve;
    private final GeneralECPoint generator;
    private Receiver receiver;
    private Sender sender;
    private GroupElement partialDecryption;

    protected SendPartialDecryption(MeterKey meterKey, BigInteger noise, LoadCurve loadCurve) {
        this.meterKey = meterKey;
        this.noise = noise;
        this.generator = loadCurve.getGroup().getGenerator();
        this.loadCurve = loadCurve;
    }

    @Override
    public BusomState next() {
        partialDecryption = generatePartialDecryption();
        sendDecryption();
        return new SendChunk(meterKey, loadCurve);
    }

    //TODO: S'han de vigilar els errors
    protected GroupElement generatePartialDecryption() {
        SMSDatagram data;
        try {
            data = receiver.receive();
            if (data instanceof GroupElementDatagram) {
                GroupElementDatagram elementDatagram = (GroupElementDatagram) data;
                return elementDatagram.getElement().pow(meterKey.getPrivateKey()).multiply(generator.pow(noise));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void sendDecryption() {
        sender.send(new GroupElementDatagram(partialDecryption));
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
