package udl.cig.sms.runnable;

import cat.udl.cig.operations.wrapper.HashedAlgorithm;

import java.io.File;

/**
 * Makes a runnable simulation of a neighborhood. It needs the number of meters
 */
public class NeighborhoodSimulation {

    private static int NUM_METERS = 3;
    private static File substation = new File("./data/substation3.toml");

    public static void main(String[] args) {
        if (args.length >= 1) {
            NUM_METERS = Integer.parseInt(args[0]);
            substation = new File("./data/substation" + NUM_METERS + ".toml");
        } else {
            NUM_METERS = 3;
            substation = new File("./data/substation3.toml");
        }
        //noinspection ResultOfMethodCallIgnored
        HashedAlgorithm.getHashedInstance();
        new Thread(() -> new SubstationRunnable(substation).run()).start();
        for (int i = 0; i < NUM_METERS; i++) {
            new Thread(() -> new SmartMeterRunnable(substation).run()).start();
        }
    }
}
