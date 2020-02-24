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

/**
 * Meter state that represents the Key Establishment of the protocol
 */
public class KeyEstablishment implements State {

    private MeterBusomControllerInt meterBusom;
    private PrimeFieldElement privateKey;
    private FactoryMeterState factory;

    /**
     * @param factory Factory that has the information of the ECC and connection and
     *                creates the different states.
     */
    public KeyEstablishment(FactoryMeterState factory) {
        this.factory = factory;
        privateKey = generatePrivateKey();
        meterBusom = factory.makeMeterBusomController();
    }

    /**
     * @return a list of BigIntegers (sij) that are the chunks of the private key.
     */
    protected List<BigInteger> generateChunksOfPrivateKey() {
        BigInteger divisor = BigInteger.TWO.pow(13);
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

    /**
     * @param privateKey or si, the private key of each smart meter.
     */
    public void setPrivateKey(PrimeFieldElement privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * @return the private key of each smart meter
     */
    public PrimeFieldElement getPrivateKey() {
        return privateKey;
    }

    /**
     * @return the next state, which is ConsumptionTransmission
     * @throws IOException in case the IO fails.
     * @throws NullMessageException in case the message is null.
     */
    @Override
    public ConsumptionTransmission next() throws IOException, NullMessageException {
        meterBusom.start();
        meterBusom.sendMessage(generateChunksOfPrivateKey());
        return factory.makeConsumptionTransmission(privateKey);
    }

    /**
     * @param meterBusom The meter controller of the busom protocol.
     */
    protected void setMeterBusom(MeterBusomControllerInt meterBusom) {
        this.meterBusom = meterBusom;
    }
}
