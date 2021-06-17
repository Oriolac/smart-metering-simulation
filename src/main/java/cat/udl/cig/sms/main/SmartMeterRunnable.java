package cat.udl.cig.sms.main;


import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionMeter;
import cat.udl.cig.sms.consumption.ConsumptionRandom;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.State;
import cat.udl.cig.sms.recsi.meter.MeterContext;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

/**
 * Makes a run of the normal pattern used for a SM. In production,
 * it can be executed by the smart meter.
 */
public class SmartMeterRunnable implements Runnable {

    private static final CurveConfiguration CURVE_READER = new CurveConfiguration(new File("data/p192.toml"));
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
        MeterContext factory;
        long now, then;
        try {
            factory = new MeterContext(CURVE_READER, new ConnectionMeter(substation, CURVE_READER),
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
