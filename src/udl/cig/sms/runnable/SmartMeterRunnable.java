package udl.cig.sms.runnable;

import cat.udl.cig.exceptions.NotImplementedException;
import javafx.util.Pair;
import udl.cig.sms.crypt.Cypher;
import udl.cig.sms.crypt.LoadCurve;
import udl.cig.sms.meter.states.KeyEstablishment;
import udl.cig.sms.meter.states.State;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

public class SmartMeterRunnable {

    private static Cypher cypher;
    private static final int L = 10;
    private static State state;
    private static final LoadCurve loadCurve = new LoadCurve(new File("data/p192.toml"));


    public static void main(String[] args) {
        state = new KeyEstablishment(loadCurve);
        /*while (true) {
            //select(pkg);
            //state.next(pkg);
        }*/


    }

    private static void send(List<Pair<BigInteger, Integer>> privateKey) {
        throw new NotImplementedException();
    }

}
