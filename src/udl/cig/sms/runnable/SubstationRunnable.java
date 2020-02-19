package udl.cig.sms.runnable;

import udl.cig.sms.busom.NullMessageException;
import udl.cig.sms.connection.ConnectionSubstation;
import udl.cig.sms.data.LoadCurve;
import udl.cig.sms.protocol.State;
import udl.cig.sms.protocol.substation.factories.FactorySubstationState;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

public class SubstationRunnable implements Runnable {

    //TODO parse parameters for files.
    private static State state;
    private static final LoadCurve loadCurve = new LoadCurve(new File("data/p192.toml"));
    private final File substation;

    public SubstationRunnable() {
        substation = new File("data/substation1.toml");
    }

    public SubstationRunnable(File file) {
        this.substation = file;
    }

    public static void main(String[] args) {
        new SubstationRunnable().run();
    }

    @Override
    public void run() {
        FactorySubstationState factory;
        long then, now;
        try {
            factory = new FactorySubstationState(loadCurve,
                    new ConnectionSubstation(substation, loadCurve));
            state = factory.makeKeyEstablishment();
            then = Instant.now().toEpochMilli();
            state = state.next();
            now = Instant.now().toEpochMilli();
            System.out.println("SM-KE: " + (now - then));
            then = now;
            for (int i = 0; i < 10; i++) {
                state = state.next();
                now = Instant.now().toEpochMilli();
                System.out.println("SM-CT: " + (now - then));
                then = now;
            }
            //TODO: closeConnection()
        } catch (IOException | NullMessageException e) {
            e.printStackTrace();
        }
    }
}
