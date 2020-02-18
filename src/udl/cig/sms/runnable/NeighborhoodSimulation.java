package udl.cig.sms.runnable;

import java.io.File;

/**
 * Makes a runnable simulation of a neighborhood. It needs the number of meters
 */
public class NeighborhoodSimulation {

    //TODO make the number of meters extracted from the file.
    private static final int NUM_METERS = 3;
    private static final File substation = new File("./data/substation3.toml");

    public static void main(String[] args) {
        new Thread(() -> new SubstationRunnable(substation).run()).start();
        for(int i = 0; i < NUM_METERS; i++) {
            new Thread(() -> new SmartMeterRunnable(substation).run()).start();
        }
    }
}
