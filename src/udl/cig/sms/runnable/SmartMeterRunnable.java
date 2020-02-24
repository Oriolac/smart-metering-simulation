package udl.cig.sms.runnable;

import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.connection.ConnectionMeter;
import udl.cig.sms.consumption.ConsumptionRandom;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.protocol.State;
import udl.cig.sms.protocol.meter.factories.FactoryMeterState;
import udl.cig.sms.protocol.meter.states.ConsumptionTransmission;
import udl.cig.sms.protocol.meter.states.KeyEstablishment;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

/**
 * Makes a run of the normal pattern used for a SM. In production,
 * it can be executed by the smart meter.
 */
public class SmartMeterRunnable implements Runnable {

    private static final LoadCurve loadCurve = new LoadCurve(new File("data/p192.toml"));
    private final File substation;

    /**
     * Generates a SMR with the predefined substation
     */
    public SmartMeterRunnable() {
        substation = new File("data/substation1.toml");
    }

    /**
     * SMR with a parameter file. Used by a neighborhood simulation
     *
     * @param file neighborhood toml containing configuration
     */
    public SmartMeterRunnable(File file) {
        this.substation = file;
    }

    /**
     * Main. Executes the 'normal' substation file.
     *
     * @param args -- not used
     */
    public static void main(String[] args) {
        new SmartMeterRunnable().run();
    }

    @Override
    public void run() {
        FactoryMeterState factory;
        long now, then;
        try {
            factory = new FactoryMeterState(loadCurve, new ConnectionMeter(substation, loadCurve),
                    new ConsumptionRandom(), "");
            State state = factory.makeKeyEstablishment();
            then = Instant.now().toEpochMilli();
            state = state.next();
            now = Instant.now().toEpochMilli();
            System.out.println("SM-KE: " + (now - then));
            then = now;
            for (int i = 0; i < 15; i++) {
                state = state.next();
                now = Instant.now().toEpochMilli();
                System.out.println("SM-CT: " + (now - then));
                then = now;
            }
            factory.closeConnection();
        } catch (IOException | NullMessageException e) {
            e.printStackTrace();
        }
    }


}
