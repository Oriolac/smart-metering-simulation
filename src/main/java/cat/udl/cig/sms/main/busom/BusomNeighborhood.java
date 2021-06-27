package cat.udl.cig.sms.main.busom;

import cat.udl.cig.sms.consumption.RandomConsumption;
import cat.udl.cig.sms.main.SmartMeterRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BusomNeighborhood {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 2) {
            System.exit(-1);
        }
        final int numMeters = Integer.parseInt(args[0]);
        final int numMsgs = Integer.parseInt(args[0]);
        final File file = new File("data/substation" + numMeters + ".toml");
        BusomSubstationRunnable substationRunnable = new BusomSubstationRunnable(file, numMsgs);
        new Thread(substationRunnable).start();
        Thread.sleep(100);
        for (int i = 0; i < numMeters; i++) {
            BufferedReader reader = new BufferedReader(new FileReader("consumptions/meter" + i + ".txt"));
            int finalI = i;
            new Thread(() -> new BusomMeterRunnable(finalI, file, numMsgs)).start();
        }
    }
}
