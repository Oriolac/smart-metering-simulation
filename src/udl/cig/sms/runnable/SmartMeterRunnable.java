package udl.cig.sms.runnable;

import cat.udl.cig.exceptions.NotImplementedException;
import javafx.util.Pair;
import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.connection.ConnectionMeter;
import udl.cig.sms.consumption.ConsumptionRandom;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.protocol.State;
import udl.cig.sms.protocol.meter.factories.FactoryMeterState;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class SmartMeterRunnable {

    //TODO parse parameters for files.
    private static State state;
    private static final LoadCurve loadCurve = new LoadCurve(new File("data/p192.toml"));
    private static final File substation = new File("data/substation.toml");


    public static void main(String[] args) throws IOException, NullMessageException {
        FactoryMeterState factory;
        factory = new FactoryMeterState(loadCurve, new ConnectionMeter(substation, loadCurve),
                new ConsumptionRandom(), "");
        state = factory.makeKeyEstablishment();
        for(int i = 0; i < 10; i++){
            state = state.next();
        }
    }

    private static void send(List<Pair<BigInteger, Integer>> privateKey) {
        throw new NotImplementedException();
    }

}
