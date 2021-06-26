package cat.udl.cig.sms.main;

import cat.udl.cig.operations.wrapper.HashedAlgorithm;
import cat.udl.cig.sms.consumption.ConsumptionFileReader;
import cat.udl.cig.sms.consumption.RandomConsumption;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * Makes a runnable simulation of a neighborhood. It needs the number of meters
 */
public class NeighborhoodSimulation {

    private static File substationFile;
    private static SubstationRunnable substation;

    public static void main(String[] args) throws InterruptedException, IOException {
        int numberOfMeters;
        if (args.length > 1 ){
            String algorithm = args[1];
        }
        if (args.length > 0) {
            numberOfMeters = Integer.parseInt(args[0]);
            substationFile = new File("data/substation" + numberOfMeters + ".toml");
        } else {
            numberOfMeters = 3;
            substationFile = new File("data/substation3.toml");
        }
        HashedAlgorithm.loadHashedInstance(CurveConfiguration.P192().getGroup().getGenerator(),
                BigInteger.TWO.pow(20), BigInteger.TWO.pow(5));
        new Thread(() -> {
            try {
                substation = new SubstationRunnable(substationFile);
                substation.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(100);

        for (int i = 0; i < numberOfMeters; i++) {
            BufferedReader reader = new BufferedReader(new FileReader("consumptions/meter" + i + ".txt"));
            int finalI = i;
            new Thread(() -> new SmartMeterRunnable(finalI, substationFile, new ConsumptionFileReader(reader)).run()).start();
        }
    }
}
