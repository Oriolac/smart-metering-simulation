package udl.cig.sms.runnable;

import java.io.File;

public class NeighborhoodSimulation {

    private static int NUM_METERS;
    private static File substation;

    public static void main(String[] args) {
        if (args.length >= 1) {
            NUM_METERS = Integer.parseInt(args[0]);
            substation = new File("./data/substation" + NUM_METERS + ".toml");
        } else {
            NUM_METERS = 3;
            substation = new File("./data/substation3.toml");
        }
        new Thread(() -> new SubstationRunnable(substation).run()).start();
        for(int i = 0; i < NUM_METERS; i++) {
            new Thread(() -> new SmartMeterRunnable(substation).run()).start();
        }
    }
}
