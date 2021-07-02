package cat.udl.cig.sms.main;


import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionMeter;
import cat.udl.cig.sms.consumption.ConsumptionFileReader;
import cat.udl.cig.sms.consumption.RandomConsumption;
import cat.udl.cig.sms.consumption.ConsumptionReader;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.recsi.meter.MeterStateContext;
import cat.udl.cig.sms.recsi.meter.MeterStateContextInt;

import java.io.*;
import java.time.Instant;

/**
 * Makes a run of the normal pattern used for a SM. In production,
 * it can be executed by the smart meter.
 */
public class SmartMeterRunnable implements Runnable {

    private static final CurveConfiguration CURVE_READER = new CurveConfiguration(new File("data/p192.toml"));
    private final File substation;
    private final ConsumptionReader consumptionReader;
    private final int numMeter;
    private final int numMsgs;

    /**
     * Generates a SMR with the predefined substation
     */
    public SmartMeterRunnable() {
        this(1, new File("data/substation1.toml"));
    }

    /**
     * SMR with a parameter file. Used by a neighborhood simulation
     *
     * @param file neighborhood toml containing configuration
     */
    public SmartMeterRunnable(int numMeter, File file) {
        this(numMeter, file, new RandomConsumption());
    }

    public SmartMeterRunnable(int numMeter, File file, ConsumptionReader consumptionReader) {
        this.numMeter = numMeter;
        this.substation = file;
        this.consumptionReader = consumptionReader;
        this.numMsgs = 50;
    }

    public SmartMeterRunnable(int numMeter, File file, ConsumptionReader consumptionReader, int numMsgs) {
        this.numMeter = numMeter;
        this.substation = file;
        this.consumptionReader = consumptionReader;
        this.numMsgs = numMsgs;
    }


    /**
     * Main. Executes the 'normal' substation file.
     *
     * @param args -- not used
     */
    public static void main(String[] args) throws FileNotFoundException {
        int numMeter = Integer.parseInt(args[0]);
        File substationFile = new File(args[1]);
        if (args.length < 3) {
            ConsumptionReader consumptionReader = new ConsumptionFileReader(new BufferedReader(new FileReader("consumptions/meter" + numMeter + ".txt")));
            new SmartMeterRunnable(numMeter, substationFile, consumptionReader).run();
        } else {
            int numMsgs = Integer.parseInt(args[2]);
            new SmartMeterRunnable(numMeter, substationFile, new RandomConsumption(), numMsgs).run();
        }
    }

    @Override
    public void run() {
        MeterStateContextInt context;
        long now, then;
        try {
            context = new MeterStateContext(numMeter, CURVE_READER, new ConnectionMeter(substation, CURVE_READER), this.consumptionReader, "");
            context.establishKey();
            for (int i = 0; i < this.numMsgs; i++) {
                context.sendConsumption();
            }
        } catch (IOException | NullMessageException e) {
            e.printStackTrace();
        }
    }


}
