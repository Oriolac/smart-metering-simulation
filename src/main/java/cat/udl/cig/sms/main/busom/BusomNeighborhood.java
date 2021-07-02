package cat.udl.cig.sms.main.busom;

import cat.udl.cig.sms.consumption.RandomConsumption;
import cat.udl.cig.sms.main.SmartMeterRunnable;

import java.io.*;

public class BusomNeighborhood {

    public static void main(String[] args) throws IOException, InterruptedException {
        int numMeters = 3;
        int numMsgs = 92;
        switch (args.length) {
            case 2:
                numMeters = Integer.parseInt(args[0]);
                numMsgs = Integer.parseInt(args[1]);
                break;
            case 1:
                numMeters = Integer.parseInt(args[0]);
                numMsgs = 92;
            default:
                System.out.println("args: <numMeters> <numMsgs>");
                System.exit(-1);
        }
        final File file = new File("data/substation" + numMeters + ".toml");
        final int finalNumMsgs = numMsgs;
        new Thread(() -> {
            try {
                new BusomSubstationRunnable(file,
                        finalNumMsgs).run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(100);
        for (int i = 0; i < numMeters; i++) {
            int finalI = i;
            new Thread(() -> new BusomMeterRunnable(finalI, file, finalNumMsgs).run()).start();
        }
    }
}
