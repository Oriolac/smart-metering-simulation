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
            System.out.println("args: <numMeters> <numMsgs>");
            System.exit(-1);
        }
        final int numMeters = Integer.parseInt(args[0]);
        final int numMsgs = Integer.parseInt(args[0]);
        final File file = new File("data/substation" + numMeters + ".toml");
        new Thread(() -> {
            try {
                new BusomSubstationRunnable(file, numMsgs).run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(100);
        for (int i = 0; i < numMeters; i++) {
            int finalI = i;
            new Thread(() -> new BusomMeterRunnable(finalI, file, numMsgs).run()).start();
        }
    }
}
