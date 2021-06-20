package cat.udl.cig.sms.main;

import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionSubstation;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.substation.SubstationContext;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Optional;

/**
 * Makes a run of the normal pattern used for a Substation.
 * In production, it can be executed by the substation.
 */
public class SubstationRunnable implements Runnable {

    private static final CurveConfiguration CURVE_READER = new CurveConfiguration(new File("data/p192.toml"));
    private final File substationFile;

    /**
     * Generates SubstationRunnable with default file.
     */
    public SubstationRunnable() {
        substationFile = new File("data/substation1.toml");
    }

    /**
     * Generates SubstationRunnable with a file passed as a parameter
     *
     * @param file toml config file for substation.
     */
    public SubstationRunnable(File file) {
        this.substationFile = file;
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
        SubstationContext substation;
        long now, then;
        try {
            substation = new SubstationContext(CURVE_READER,
                    new ConnectionSubstation(substationFile, CURVE_READER));
            then = Instant.now().toEpochMilli();
            substation.startKeyEstablishment();
            now = Instant.now().toEpochMilli();
            System.out.println("SSt-KE: " + (now - then));
            then = now;
            for (int i = 0; i < 15; i++) {
                Optional<BigInteger> message = substation.getMessage();
                now = Instant.now().toEpochMilli();
                System.out.println("SSt-CT: " + (now - then));
                message.ifPresent(bigInteger -> System.out.println("Message: " + bigInteger));
                then = now;
            }
            //TODO: closeConnection()
        } catch (IOException | NullMessageException e) {
            e.printStackTrace();
        }
    }
}
