package udl.cig.sms.busom.meter;

import java.math.BigInteger;

public class SendChunk implements BusomState{
    @Override
    public BusomState next() {
        return this;
    }

    protected BigInteger generateNoise(){
        return null;
    }

    protected void sendCypherText() {

    }


    protected BigInteger getPrivateKey() {
        return null;
    }
}
