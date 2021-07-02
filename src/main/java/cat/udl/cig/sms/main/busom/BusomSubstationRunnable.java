package cat.udl.cig.sms.main.busom;

import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.busom.SubstationBusomService;
import cat.udl.cig.sms.connection.ConnectionSubstation;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

public class BusomSubstationRunnable implements Runnable{

    private static final CurveConfiguration CURVE_READER = new CurveConfiguration(new File("data/p192.toml"));
    private final File substationFile;
    private final SubstationBusomService service;
    private final ConnectionSubstationInt connectionSubstationInt;
    private final int numMsgs;

    /**
     * Generates SubstationRunnable with default file.
     */
    public BusomSubstationRunnable(int numMsgs) throws IOException {
        this(new File("data/substation1.toml"), numMsgs);
    }


    /**
     * Generates SubstationRunnable with a file passed as a parameter
     *
     * @param file toml config file for substation.
     */
    public BusomSubstationRunnable(File file, int numMsgs) throws IOException {
        this.substationFile = file;
        this.numMsgs = numMsgs;
        connectionSubstationInt = new ConnectionSubstation(substationFile, CURVE_READER);
        service = new SubstationBusomService(CURVE_READER, connectionSubstationInt);
        service.setNumberOfChunks(numMsgs);
    }

    /**
     * Main -> Executes substationRunnable with default file
     *
     * @param args -- Not used
     */
    public static void main(String[] args) throws IOException {
        int numMsgs = 92;
        if (args.length <= 2) {
            numMsgs = Integer.parseInt(args[1]);
        }
        new BusomSubstationRunnable(new File(args[0]), numMsgs).run();
    }

    @Override
    public void run() {
        long now, then;
        try {
            BufferedWriter consumption = new BufferedWriter(new FileWriter("analysis/msgm/busom"+ this.connectionSubstationInt.getNumberOfMeters() +".csv", true));
            then = Instant.now().toEpochMilli();
            service.receiveSecretKey();
            now = Instant.now().toEpochMilli();
            consumption.write( this.numMsgs + "," + (now - then));
            consumption.newLine();
            consumption.close();
        } catch (IOException | NullMessageException e) {
            e.printStackTrace();
        }
    }
}
