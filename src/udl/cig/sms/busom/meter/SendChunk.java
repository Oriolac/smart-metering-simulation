package udl.cig.sms.busom.meter;

import cat.udl.cig.cryptography.cryptosystems.ElGamalCypher;
import cat.udl.cig.cryptography.cryptosystems.HomomorphicCypher;
import cat.udl.cig.cryptography.cryptosystems.ciphertexts.ElGamalCiphertext;
import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.busom.data.MeterKey;
import udl.cig.sms.connection.Sender;
import udl.cig.sms.connection.datagram.CipherTextDatagram;
import udl.cig.sms.crypt.LoadCurve;

import java.math.BigInteger;
import java.util.Random;

public class SendChunk implements BusomState {

    private final GroupElement generator;
    private final LoadCurve loadCurve;
    private final MeterKey meterKey;
    private BigInteger message;
    private Sender sender;
    private HomomorphicCypher cypher;
    private ElGamalCiphertext ciphertext;

    protected SendChunk(MeterKey meterKey, LoadCurve loadCurve) {
        this.loadCurve = loadCurve;
        this.generator = loadCurve.getGroup().getGenerator();
        this.meterKey = meterKey;
    }


    @Override
    public BusomState next() throws NullMessageException {
        BigInteger noise = SendChunk.generateNoise();
        if (message == null)
            throw new NullMessageException();
        GroupElement point = this.generator.pow(message.add(noise));
        cypher = new ElGamalCypher(loadCurve.getGroup(), generator, meterKey.getGeneralKey());
        ciphertext = (ElGamalCiphertext) cypher.encrypt(point);
        sendCypherText();
        return new SendPartialDecryption(meterKey, noise, loadCurve);
    }

    static protected BigInteger generateNoise() {
        Random randomGen = new Random();
        long random = randomGen.nextLong();
        return BigInteger.valueOf(random);
    }

    protected void sendCypherText() {
        sender.send(new CipherTextDatagram(ciphertext));
    }

    protected BigInteger getPrivateKey() {
        return meterKey.getPrivateKey();
    }

    public void setMessage(BigInteger message) {
        this.message = message;
    }

    protected void setSender(Sender sender) {
        this.sender = sender;
    }

    protected void setCypher(HomomorphicCypher cypher) {
        this.cypher = cypher;
    }

}
