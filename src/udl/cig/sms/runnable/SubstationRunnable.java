package udl.cig.sms.runnable;

import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.connection.ConnectionSubstation;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.protocol.State;
import udl.cig.sms.protocol.substation.factories.FactorySubstationState;

import java.io.File;
import java.io.IOException;

/**
 * Makes a run of the normal pattern used for a Substation.
 * In production, it can be executed by the substation.
 */
public class SubstationRunnable implements Runnable {

    //TODO parse parameters for files.
    private static State state;
    private static final LoadCurve loadCurve = new LoadCurve(new File("data/p192.toml"));
    private final File substation;

    /**
     * Generates SubstationRunnable with default file.
     */
    public SubstationRunnable() {
        substation = new File("data/substation.toml");
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
        FactorySubstationState factory;
        try {
            factory = new FactorySubstationState(loadCurve,
                    new ConnectionSubstation(substation, loadCurve));
            state = factory.makeKeyEstablishment();
            for (int i = 0; i < 10; i++) {
                state = state.next();
            }
        } catch (IOException | NullMessageException e) {
            e.printStackTrace();
        }
    }
}
