package cat.udl.cig.sms.main;

import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionSubstation;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.substation.SubstationStateContext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
    private final SubstationStateContext substation;
    private final ConnectionSubstationInt connectionSubstationInt;

    /**
     * Generates SubstationRunnable with default file.
     */
    public SubstationRunnable() throws IOException {
        this(new File("data/substation1.toml"));
    }


    /**
     * Generates SubstationRunnable with a file passed as a parameter
     *
     * @param file toml config file for substation.
     */
    public SubstationRunnable(File file) throws IOException {
        this.substationFile = file;
        connectionSubstationInt = new ConnectionSubstation(substationFile, CURVE_READER);
        substation = new SubstationStateContext(CURVE_READER, connectionSubstationInt);

    }

    /**
     * Main -> Executes substationRunnable with default file
     *
     * @param args -- Not used
     */
    public static void main(String[] args) throws IOException {
        new SubstationRunnable().run();
    }

    @Override
    public void run() {
        long now, then;
        try {
            BufferedWriter consumption = new BufferedWriter(new FileWriter("analysis/ct-brute"+ this.substation.getConnection().getNumberOfMeters() +".csv"));
            consumption.write("timedelta");
            consumption.newLine();
            then = Instant.now().toEpochMilli();
            substation.startKeyEstablishment();
            now = Instant.now().toEpochMilli();
            //System.out.println("SSt-KE: " + (now - then));
            then = now;
            for (int i = 0; i < 50; i++) {
                Optional<BigInteger> message = substation.getMessage();
                int finalI = i;
                message.ifPresent((a) -> System.out.println(finalI));
                now = Instant.now().toEpochMilli();
                consumption.write(String.valueOf(now - then));
                consumption.newLine();
                then = now;
            }
            consumption.close();
        } catch (IOException | NullMessageException e) {
            e.printStackTrace();
        }
    }
}
