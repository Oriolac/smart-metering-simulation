package udl.cig.sms.busom.meter;

import cat.udl.cig.cryptography.cryptosystems.HomomorphicCypher;
import udl.cig.sms.busom.BusomState;
import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.connection.Sender;

import java.math.BigInteger;

public class SendChunk implements BusomState {

    private BigInteger privateKey;
    private BigInteger message;
    private Sender sender;
    private HomomorphicCypher cypher;

    protected SendChunk(BigInteger privateKey) {
        this.privateKey = privateKey;
    }


    @Override
    public BusomState next() throws NullMessageException {
        BigInteger noise = SendChunk.generateNoise();
        if (message == null)
            throw new NullMessageException();
        // g ^ (m + z)
        // ELGAMAL -> ElGamalCypherText :: (GroupElement, GroupElement)
        // sendCypherText
        return this;
    }

    static protected BigInteger generateNoise() {
        return null;
    }

    protected void sendCypherText() {

    }

    protected BigInteger getPrivateKey() {
        return null;
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
