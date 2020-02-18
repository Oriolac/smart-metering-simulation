package udl.cig.sms.runnable;

import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.connection.ConnectionMeter;
import udl.cig.sms.consumption.ConsumptionRandom;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.protocol.State;
import udl.cig.sms.protocol.meter.factories.FactoryMeterState;

import java.io.File;
import java.io.IOException;

public class SmartMeterRunnable implements Runnable {

    private static final LoadCurve loadCurve = new LoadCurve(new File("data/p192.toml"));
    private final File substation;

    public SmartMeterRunnable() {
        substation = new File("data/substation1.toml");
    }

    public SmartMeterRunnable(File file) {
        this.substation = file;
    }

    public static void main(String[] args) {
        new SmartMeterRunnable().run();
    }

    @Override
    public void run() {
        FactoryMeterState factory;
        try {
            factory = new FactoryMeterState(loadCurve, new ConnectionMeter(substation, loadCurve),
                    new ConsumptionRandom(), "");
            State state = factory.makeKeyEstablishment();
            for (int i = 0; i < 10; i++) {
                state = state.next();
            }
        } catch (IOException | NullMessageException e) {
            e.printStackTrace();
        }
    }


}
