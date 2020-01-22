package udl.cig.sms.meter.states;

import cat.udl.cig.fields.PrimeFieldElement;
import javafx.util.Pair;
import udl.cig.sms.crypt.LoadCurve;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class KeyEstablishment implements State {

    private final LoadCurve loadCurve;
    private PrimeFieldElement privateKey;

    public KeyEstablishment(LoadCurve loadCurve) {
        this.loadCurve = loadCurve;

    }


    private List<Pair<BigInteger, Integer>> generateSij() {
        BigInteger divisor = BigInteger.TWO;
        divisor = divisor.pow(13);
        privateKey = generateSi();
        List<Pair<BigInteger, Integer>> result = new ArrayList<>();
        int j = 0;

        BigInteger[] tmp = privateKey.getIntValue().divideAndRemainder(divisor);
        while (!tmp[0].equals(BigInteger.ZERO)) {
            result.add(new Pair<>(tmp[1], j));
            j++;
            tmp = tmp[0].divideAndRemainder(divisor);
        }
        result.add(new Pair<>(tmp[1], j));
        return result;
    }

    private PrimeFieldElement generateSi() {
        return loadCurve.getField().getRandomElement();
    }

    @Override
    public State next() {
        return this;
    }
}
