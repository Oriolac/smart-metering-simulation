package cat.udl.cig.sms.main;

import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionSubstation;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.State;
import cat.udl.cig.sms.recsi.substation.SubstationContext;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

/**
 * Makes a run of the normal pattern used for a Substation.
 * In production, it can be executed by the substation.
 */
public class SubstationRunnable implements Runnable {

    private static State state;
    private static final CurveConfiguration CURVE_READER = new CurveConfiguration(new File("data/p192.toml"));
    private final File substation;

    /**
     * Generates SubstationRunnable with default file.
     */
    public SubstationRunnable() {
        substation = new File("data/substation1.toml");
    }

    /**
     * Generates SubstationRunnable with a file passed as a parameter
     *
     * @param file toml config file for substation.
     */
    public SubstationRunnable(File file) {
        this.substation = file;
    }

    /**
     * Main -> Executes substationRunnable with default file
     *
     * @param args -- Not used
     */
    public static void main(String[] args) {
        new SubstationRunnable().run();
    }

    @Override
    public void run() {
        SubstationContext factory;
        long now, then;
        try {
            factory = new SubstationContext(CURVE_READER,
                    new ConnectionSubstation(substation, CURVE_READER));
            state = factory.makeKeyEstablishment();
            then = Instant.now().toEpochMilli();
            state = state.next();
            now = Instant.now().toEpochMilli();
            System.out.println("SSt-KE: " + (now - then));
            then = now;
            for (int i = 0; i < 15; i++) {
                state = state.next();
                now = Instant.now().toEpochMilli();
                System.out.println("SSt-CT: " + (now - then));
                then = now;
            }
            //TODO: closeConnection()
        } catch (IOException | NullMessageException e) {
            e.printStackTrace();
        }
    }
}
