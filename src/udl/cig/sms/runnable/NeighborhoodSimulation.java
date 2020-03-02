package udl.cig.sms.runnable;

import cat.udl.cig.operations.wrapper.HashedAlgorithm;
import udl.cig.sms.data.LoadCurve;

import java.io.File;
import java.math.BigInteger;

/**
 * Makes a runnable simulation of a neighborhood. It needs the number of meters
 */
public class NeighborhoodSimulation {

    private static int NUM_METERS = 2;
    private static File substation = new File("./data/substation2.toml");

    public static void main(String[] args) throws InterruptedException {
        if (args.length >= 1) {
            NUM_METERS = Integer.parseInt(args[0]);
            substation = new File("./data/substation" + NUM_METERS + ".toml");
        } else {
            NUM_METERS = 3;
            substation = new File("./data/substation3.toml");
        }
        HashedAlgorithm.loadHashedInstance(LoadCurve.P192().getGroup().getGenerator(),
                BigInteger.TWO.pow(20), BigInteger.TWO.pow(5));
        new Thread(() -> new SubstationRunnable(substation).run()).start();
        Thread.sleep(100);
        for (int i = 0; i < NUM_METERS; i++) {
            new Thread(() -> new SmartMeterRunnable(substation).run()).start();
        }
    }
}
