package cat.udl.cig.sms.recsi.meter.states;

import cat.udl.cig.fields.PrimeFieldElement;
import cat.udl.cig.sms.busom.MeterBusomServiceInt;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.recsi.State;
import cat.udl.cig.sms.recsi.meter.MeterStateContext;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Meter state that represents the Key Establishment of the protocol
 */
public class KeyEstablishmentMeter implements State {

    private MeterBusomServiceInt busomService;
    private PrimeFieldElement privateKey;
    private final MeterStateContext factory;

    /**
     * @param factory Factory that has the information of the ECC and connection and
     *                creates the different states.
     */
    public KeyEstablishmentMeter(MeterStateContext factory) throws IOException, NullMessageException {
        this.factory = factory;
        privateKey = generatePrivateKey();
        busomService = factory.makeMeterBusomController();
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
     * @throws IOException          in case the IO fails.
     * @throws NullMessageException in case the message is null.
     */
    @Override
    public ConsumptionTransmissionMeter next() throws IOException, NullMessageException {
        busomService.start();
        busomService.sendMessage(generateChunksOfPrivateKey());
        return factory.makeConsumptionTransmission(privateKey);
    }

    /**
     * @param busomService The meter controller of the busom protocol.
     */
    protected void setBusomService(MeterBusomServiceInt busomService) {
        this.busomService = busomService;
    }
}
