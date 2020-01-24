package udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.CertificateValidation;
import udl.cig.sms.busom.data.MeterKey;
import udl.cig.sms.connection.Receiver;
import udl.cig.sms.connection.SMSDatagram;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import udl.cig.sms.crypt.LoadCurve;

import java.math.BigInteger;

public class NeighborhoodSetUp implements BusomState {


    private final BigInteger privateKey;
    private final LoadCurve loadCurve;
    private GroupElement generalKey;
    private GroupElement generator;
    private Receiver receiver;
    private CertificateValidation<String> validation;

    protected NeighborhoodSetUp(BigInteger privateKey, LoadCurve loadCurve) {
        this.privateKey = privateKey;
        this.loadCurve = loadCurve;
        this.generator = loadCurve.getGroup().getGenerator();
    }

    @Override
    public BusomState next() {
        receivePublicKeysAndCertificates();
        MeterKey meterKey = new MeterKey(privateKey, generalKey);
        return new SendChunk(meterKey, loadCurve);
    }


    protected void receivePublicKeysAndCertificates() {
        generalKey = generator.getGroup().getNeuterElement();
        SMSDatagram data = receiver.receive();
        while (data instanceof NeighborhoodDatagram) {
            //noinspection unchecked cast
            NeighborhoodDatagram<String> dataN = (NeighborhoodDatagram<String>) data;
            if (dataN.validate(this.validation))
                generalKey = generalKey.multiply(dataN.getPublicKey());
            data = receiver.receive();
        }

    }


    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    protected GroupElement getGeneralKey() {
        return this.generalKey;
    }

    public void setValidation(CertificateValidation<String> validation) {
        this.validation = validation;
    }
}
