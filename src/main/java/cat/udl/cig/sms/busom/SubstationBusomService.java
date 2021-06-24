package cat.udl.cig.sms.busom;

import cat.udl.cig.sms.busom.substation.SubstationBusomContext;
import cat.udl.cig.sms.busom.substation.SubstationBusomContextInt;
import cat.udl.cig.sms.connection.ConnectionSubstationInt;
import cat.udl.cig.sms.crypt.CurveConfiguration;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Optional;

/**
 * Controller of Substation Busom. Extract the method to calculate a key.
 */
public class SubstationBusomService implements SubstationBusomServiceInt {

    private final int numberOfChunks;
    private final SubstationBusomContextInt substationBusomContextInt;

    /**
     * Generates a Substation Busom Controller.
     *
     * @param curveConfiguration  loads the ECC curve used for the protocol
     * @param connection to all the meters
     */
    public SubstationBusomService(CurveConfiguration curveConfiguration, ConnectionSubstationInt connection) {
        this.substationBusomContextInt = new SubstationBusomContext(curveConfiguration.getGroup(), connection);
        int bits = curveConfiguration.getField().getSize().bitLength();
        this.numberOfChunks = bits / 13 + ((bits % 13 == 0) ? 0 : 1);
    }

    /**
     * Receives and computes a key, adding all the messages (sis) with their power.
     *
     * @return Key to decrypt messages of the parent protocol
     * @throws IOException          If connection fails.
     * @throws NullMessageException Never sent.
     */
    @Override
    public BigInteger receiveSecretKey() throws IOException, NullMessageException {
        substationBusomContextInt.setUp();
        BigInteger message = BigInteger.ZERO;
        long then, now;
        then = Instant.now().toEpochMilli();
        for (int i = 0; i < this.numberOfChunks; ++i) {
            substationBusomContextInt.computeC();
            Optional<BigInteger> chunk = substationBusomContextInt.decrypt();
            if (chunk.isEmpty()) {
                throw new NullMessageException();
            }
            message = message.add(chunk.get().multiply(BigInteger.TWO.pow(13 * i)));
            now = Instant.now().toEpochMilli();
            System.out.println("SSt-BS: " + (now - then));
            then = now;
        }
        return message;
    }

}
