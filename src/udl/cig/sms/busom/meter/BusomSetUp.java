package udl.cig.sms.busom.meter;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.connection.Sender;
import udl.cig.sms.connection.datagram.NeighborhoodDatagram;
import udl.cig.sms.crypt.LoadCurve;

import java.math.BigInteger;
import java.util.Random;

public class BusomSetUp implements BusomState {


    private final GroupElement generator;
    private final String certificate;
    private final LoadCurve loadCurve;
    private BigInteger privateKey;
    private GroupElement publicKey;
    private Sender sender;

    public BusomSetUp(String certificate, LoadCurve loadCurve) {
        this.certificate = certificate;
        this.loadCurve = loadCurve;
        this.generator = loadCurve.getGroup().getGenerator();
    }

    @Override
    public BusomState next() {
        generatePrivateKey();
        calculatePublicKey();
        sendPublicKey();
        return new NeighborhoodSetUp(privateKey, loadCurve);
    }

    protected void generatePrivateKey() {
        Random random = new Random();
        long key = random.nextLong();
        privateKey = BigInteger.valueOf(key);
    }

    protected void calculatePublicKey() {
        publicKey = (privateKey == null)
                ? null
                : generator.pow(privateKey);
    }

    protected void sendPublicKey() {
        if (publicKey != null)
            sender.send(new NeighborhoodDatagram<>(publicKey, certificate));
    }

    protected void setSender(Sender sender) {
        this.sender = sender;
    }

    protected GroupElement getPublicKey() {
        return publicKey;
    }

    protected BigInteger getPrivateKey() {
        return privateKey;
    }
}
