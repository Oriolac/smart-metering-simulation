package cat.udl.cig.sms.main;

import cat.udl.cig.operations.wrapper.HashedAlgorithm;
import cat.udl.cig.sms.consumption.ConsumptionFileReader;
import cat.udl.cig.sms.consumption.ConsumptionReader;
import cat.udl.cig.sms.consumption.RandomConsumption;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.*;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

/**
 * Makes a runnable simulation of a neighborhood. It needs the number of meters
 */
public class NeighborhoodSimulation {

    private static File substationFile;
    private static SubstationRunnable substation;

    public static void main(String[] args) throws InterruptedException, IOException {
        int numberOfMeters = 3;
        int numMsgs = 92;
        Optional<ConsumptionReader> consumptionReader = Optional.empty();
        if (args.length > 0) {
            numberOfMeters = Integer.parseInt(args[0]);
            substationFile = new File("data/substation" + numberOfMeters + ".toml");
        } else {
            System.exit(0);
        }
        if (args.length > 1) {
            numMsgs = Integer.parseInt(args[1]);
            consumptionReader = Optional.of(new RandomConsumption());
        }
        HashedAlgorithm.loadHashedInstance(CurveConfiguration.P192().getGroup().getGenerator(),
                BigInteger.TWO.pow(20), BigInteger.TWO.pow(5));
        final int finalNumberOfMeters = numberOfMeters;
        final int finalNumMsgs = numMsgs;
        new Thread(() -> {
            try {
                substation = new SubstationRunnable(substationFile, finalNumberOfMeters, finalNumMsgs);
                substation.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(100);
        for (int i = 0; i < numberOfMeters; i++) {
            ConsumptionReader finalConsumptionReader;
            if (consumptionReader.isEmpty()) {
                BufferedReader reader = new BufferedReader(new FileReader("consumptions/meter" + i + ".txt"));
                finalConsumptionReader = new ConsumptionFileReader(reader);
            } else {
                finalConsumptionReader = consumptionReader.get();
            }
            int finalI = i;
            new Thread(() -> new SmartMeterRunnable(finalI, substationFile, finalConsumptionReader, finalNumMsgs).run()).start();
        }
    }
}
