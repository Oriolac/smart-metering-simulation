package cat.udl.cig.sms.main;

import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionSubstation;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.substation.SubstationStateContext;

import java.io.*;
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
    private final int numMeters;
    private final int numMsgs;


    /**
     * Generates SubstationRunnable with a file passed as a parameter
     *
     * @param file toml config file for substation.
     */
    public SubstationRunnable(File file, int numMeters, int numMsgs) throws IOException {
        this.substationFile = file;
        connectionSubstationInt = new ConnectionSubstation(substationFile, CURVE_READER);
        substation = new SubstationStateContext(CURVE_READER, connectionSubstationInt);
        this.numMeters = numMeters;
        this.numMsgs = numMsgs;
    }

    /**
     * Main -> Executes substationRunnable with default file
     *
     * @param args -- Not used
     */
    public static void main(String[] args) throws IOException {
        int numMsgs = 92;
        if (args.length <= 3) {
            numMsgs = Integer.parseInt(args[2]);
        }
        new SubstationRunnable(new File(args[0]), Integer.parseInt(args[1]), numMsgs).run();
    }

    @Override
    public void run() {
        long now, then;
        try {
            //BufferedWriter writer = new BufferedWriter(new FileWriter("analysis/dataset/recsip" + this.numMeters + ".csv", true));
            BufferedWriter writer = new BufferedWriter(new FileWriter("analysis/ke-hashed.csv", true));
            then = Instant.now().toEpochMilli();
            substation.startKeyEstablishment();
            now = Instant.now().toEpochMilli();
            writer.write(this.numMeters + "," + (now-then));
            writer.newLine();
            writer.close();
            writer = new BufferedWriter(new FileWriter("analysis/ct-hashed.csv", true));
            then = Instant.now().toEpochMilli();
            for (int i = 0; i < this.numMsgs; i++) {
                substation.getMessage();
            }
            now = Instant.now().toEpochMilli();
            writer.write(this.numMeters + "," + (now-then));
            writer.newLine();
            writer.close();
        } catch (IOException | NullMessageException e) {
            e.printStackTrace();
        }
    }
}
