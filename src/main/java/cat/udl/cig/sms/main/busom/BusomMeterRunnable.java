package cat.udl.cig.sms.main.busom;

import cat.udl.cig.sms.busom.MeterBusomService;
import cat.udl.cig.sms.busom.NullMessageException;
import cat.udl.cig.sms.connection.ConnectionMeter;
import cat.udl.cig.sms.connection.KeyRenewalException;
import cat.udl.cig.sms.consumption.ConsumptionFileReader;
import cat.udl.cig.sms.consumption.ConsumptionReader;
import cat.udl.cig.sms.consumption.RandomConsumption;
import cat.udl.cig.sms.crypt.CurveConfiguration;
import cat.udl.cig.sms.main.SmartMeterRunnable;
import cat.udl.cig.sms.recsi.meter.MeterStateContext;
import cat.udl.cig.sms.recsi.meter.MeterStateContextInt;

import java.io.*;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BusomMeterRunnable implements Runnable{

    private static final CurveConfiguration CURVE_READER = new CurveConfiguration(new File("data/p192.toml"));
    private final File substation;
    private final ConsumptionReader consumptionReader;
    private final int numMeter;
    private final int numMsgs;

    /**
     * SMR with a parameter file. Used by a neighborhood simulation
     *
     * @param file neighborhood toml containing configuration
     */
    public BusomMeterRunnable(int numMeter, File file, int numMsgs) {
        this(numMeter, file, new RandomConsumption(), numMsgs);
    }


    public BusomMeterRunnable(int numMeter, File file, ConsumptionReader consumptionReader, int numMsgs) {
        this.numMeter = numMeter;
        this.substation = file;
        this.consumptionReader = consumptionReader;
        this.numMsgs = numMsgs;
    }

    public BusomMeterRunnable(int numMeter, File file, ConsumptionReader consumptionReader) {
        this(numMeter, file, consumptionReader, 92);
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
            new BusomMeterRunnable(numMeter, substationFile, consumptionReader).run();
        } else {
            int numMsgs = Integer.parseInt(args[2]);
            new BusomMeterRunnable(numMeter, substationFile, new RandomConsumption(), numMsgs).run();
        }
    }

    @Override
    public void run() {
        MeterBusomService service;
        long now, then;
        try {
            service = new MeterBusomService(numMeter, "", CURVE_READER, new ConnectionMeter(substation, CURVE_READER));
            service.start();
            List<BigInteger> messages = new ArrayList<>();
            for (int i = 0; i < numMsgs; i++) {
                messages.add(consumptionReader.read());
            }
            service.sendMessage(messages);
        } catch (IOException | NullMessageException | KeyRenewalException e) {
            e.printStackTrace();
        }
    }
}
