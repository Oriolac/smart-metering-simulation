package cat.udl.cig.sms.busom;

import cat.udl.cig.sms.busom.meter.BusomSetUp;
import cat.udl.cig.sms.busom.meter.MeterBusomContext;
import cat.udl.cig.sms.busom.meter.MeterBusomContextInt;
import cat.udl.cig.sms.busom.meter.SendChunk;
import cat.udl.cig.sms.connection.ConnectionMeterInt;
import cat.udl.cig.sms.connection.KeyRenewalException;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

/**
 * Controller of the protocol. Encapsulates the protocol to make it
 * easier for high architecture classes.
 */
public class MeterBusomService implements MeterBusomServiceInt {

    private final MeterBusomContextInt context;
    private final int numMeter;

    /**
     * Generates a meter busom controller
     *
     * @param certificate certificate of the meter
     * @param curveConfiguration   parameters of the ECC curve
     * @param connection  to the substation.
     */
    public MeterBusomService(int numMeter, String certificate, CurveConfiguration curveConfiguration, ConnectionMeterInt connection) throws IOException, NullMessageException {
        this.numMeter = numMeter;
        context = new MeterBusomContext(certificate, curveConfiguration, connection);
        context.generatePrivateKey();
    }

    /**
     * Generates a meter busom Controller. Used for testing
     *
     * @param curveConfiguration  parameters of the ECC curve
     * @param connection to the substation.
     * @throws IOException          if connection fails.
     * @throws NullMessageException Never throws this exception
     */
    public MeterBusomService(CurveConfiguration curveConfiguration, ConnectionMeterInt connection) throws IOException, NullMessageException, KeyRenewalException {
        this(1, "", curveConfiguration, connection);
    }

    /**
     * Starts the protocol
     *
     * @throws NullMessageException Never throwed. Declared by substractions of code.
     * @throws IOException          if connection fails.
     */
    @Override
    public void start() throws NullMessageException, IOException, KeyRenewalException {
        context.setUpNeighborHood();
    }

    /**
     * Sends List of messages to the substation
     *
     * @param messages messages to be sent. At maximum 13 bits
     * @throws IOException          if connection fails
     * @throws NullMessageException if some message is null.
     */
    @Override
    public void sendMessage(List<BigInteger> messages) throws IOException, NullMessageException, KeyRenewalException {
        long now, then;
        then = Instant.now().toEpochMilli();
        int i = 0;
        //BufferedWriter writer = new BufferedWriter(new FileWriter("analysis/msgm/192/met" + numMeter +  ".csv"));
        //writer.write("round,message");
        //writer.newLine();
        for (BigInteger message : messages) {
            //writer.write(i + "," + message.toString());
            //writer.newLine();
            context.sendChunk(message);
            context.sendPartialDecryption();
            now = Instant.now().toEpochMilli();
            //System.out.println("SM-BS: " + (now - then));
            then = now;
            i++;
        }
        //writer.close();
    }


}
