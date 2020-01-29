package udl.cig.sms.meter.states;

import cat.udl.cig.fields.PrimeFieldElement;
import udl.cig.sms.busom.MeterBusomController;
import udl.cig.sms.busom.MeterBusomControllerInt;
import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.connection.ConnectionMeterInt;
import udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class KeyEstablishment implements State {

    private final LoadCurve loadCurve;
    private MeterBusomControllerInt meterBusom;
    private final ConnectionMeterInt connectionMeter;
    private PrimeFieldElement privateKey;

    public KeyEstablishment(LoadCurve loadCurve, String certificate, ConnectionMeterInt connectionMeter) throws IOException, NullMessageException {
        this.loadCurve = loadCurve;
        privateKey = generatePrivateKey();
        this.connectionMeter = connectionMeter;
        meterBusom = new MeterBusomController(certificate, loadCurve, connectionMeter);
    }

    protected KeyEstablishment(LoadCurve loadCurve, ConnectionMeterInt connectionMeter, MeterBusomControllerInt controller) {
        this.loadCurve = loadCurve;
        privateKey = generatePrivateKey();
        this.connectionMeter = connectionMeter;
        this.meterBusom = controller;
    }

    protected List<BigInteger> generateChunksOfPrivateKey() {
        BigInteger divisor = BigInteger.TWO;
        divisor = divisor.pow(13);
        List<BigInteger> result = new ArrayList<>();
        BigInteger[] tmp = privateKey.getIntValue().divideAndRemainder(divisor);
        int bits = loadCurve.getField().getSize().bitLength();
        int numberOfChunks = bits / 13 + ((bits % 13 == 0) ? 0 : 1);
        for (int j = 0; j < numberOfChunks; j++) {
            result.add(tmp[1]);
            tmp = tmp[0].divideAndRemainder(divisor);
        }
        return result;
    }

    private PrimeFieldElement generatePrivateKey() {
        return loadCurve.getField().getRandomElement();
    }

    public void setPrivateKey(PrimeFieldElement privateKey) {
        this.privateKey = privateKey;
    }

    public PrimeFieldElement getPrivateKey() {
        return privateKey;
    }

    @Override
    public ConsumptionTransmission next() throws IOException, NullMessageException {
        meterBusom.sendMessage(generateChunksOfPrivateKey());
        return new ConsumptionTransmission(loadCurve, connectionMeter, privateKey);
    }

    protected void setMeterBusom(MeterBusomControllerInt meterBusom) {
        this.meterBusom = meterBusom;
    }
}
