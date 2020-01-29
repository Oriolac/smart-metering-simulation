package udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.certificate.CertificateValidation;
import udl.cig.sms.busom.data.MeterKey;
import udl.cig.sms.connection.ConnectionMeterInt;
import udl.cig.sms.connection.ReceiverMeter;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;

public class NeighborhoodSetUp implements BusomState {


    private final BigInteger privateKey;
    private final LoadCurve loadCurve;
    private ConnectionMeterInt connection;
    private GroupElement generalKey;
    private GroupElement generator;
    private ReceiverMeter receiverMeter;
    private CertificateValidation<String> validation;

    protected NeighborhoodSetUp(BigInteger privateKey, LoadCurve loadCurve) {
        this.privateKey = privateKey;
        this.loadCurve = loadCurve;
        this.generator = loadCurve.getGroup().getGenerator();
    }

    protected NeighborhoodSetUp(BigInteger privateKey, LoadCurve loadCurve, ConnectionMeterInt connection) {
        this(privateKey, loadCurve);
        this.connection = connection;
        this.receiverMeter = connection;
    }

    @Override
    public BusomState next() throws IOException {
        receivePublicKeysAndCertificates();
        MeterKey meterKey = new MeterKey(privateKey, generalKey);
        return new SendChunk(meterKey, loadCurve, connection);
    }


    protected void receivePublicKeysAndCertificates() throws IOException {
        generalKey = generator.getGroup().getNeuterElement();
        SMSDatagram data;
        data = receiverMeter.receive();
        while (data instanceof NeighborhoodDatagram) {
            //noinspection unchecked cast
            NeighborhoodDatagram<String> dataN = (NeighborhoodDatagram<String>) data;
            if (dataN.validate(this.validation))
                generalKey = generalKey.multiply(dataN.getPublicKey());
            data = receiverMeter.receive();
        }
    }


    public void setReceiverMeter(ReceiverMeter receiverMeter) {
        this.receiverMeter = receiverMeter;
    }

    protected GroupElement getGeneralKey() {
        return this.generalKey;
    }

    public void setValidation(CertificateValidation<String> validation) {
        this.validation = validation;
    }
}
