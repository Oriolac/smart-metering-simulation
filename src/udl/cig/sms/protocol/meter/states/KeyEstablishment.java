package udl.cig.sms.protocol.meter.states;

import cat.udl.cig.fields.PrimeFieldElement;
import udl.cig.sms.busom.MeterBusomControllerInt;
import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.protocol.State;
import udl.cig.sms.protocol.meter.factories.FactoryMeterState;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class KeyEstablishment implements State {

    private MeterBusomControllerInt meterBusom;
    private PrimeFieldElement privateKey;
    private FactoryMeterState factory;

    public KeyEstablishment(FactoryMeterState factory) {
        this.factory = factory;
        privateKey = generatePrivateKey();
        meterBusom = factory.makeMeterBusomController();
    }

    protected List<BigInteger> generateChunksOfPrivateKey() {
        BigInteger divisor = BigInteger.TWO;
        divisor = divisor.pow(13);
        List<BigInteger> result = new ArrayList<>();
        BigInteger[] tmp = privateKey.getIntValue().divideAndRemainder(divisor);
        int bits = factory.getLoadCurve().getField().getSize().bitLength();
        int numberOfChunks = bits / 13 + ((bits % 13 == 0) ? 0 : 1);
        for (int j = 0; j < numberOfChunks; j++) {
            result.add(tmp[1]);
            tmp = tmp[0].divideAndRemainder(divisor);
        }
        return result;
    }

    private PrimeFieldElement generatePrivateKey() {
        return factory.getLoadCurve().getField().getRandomElement();
    }

    public void setPrivateKey(PrimeFieldElement privateKey) {
        this.privateKey = privateKey;
    }

    public PrimeFieldElement getPrivateKey() {
        return privateKey;
    }

    @Override
    public ConsumptionTransmission next() throws IOException, NullMessageException {
        meterBusom.start();
        meterBusom.sendMessage(generateChunksOfPrivateKey());
        return factory.makeConsumptionTransmission(privateKey);
    }

    protected void setMeterBusom(MeterBusomControllerInt meterBusom) {
        this.meterBusom = meterBusom;
    }
}
