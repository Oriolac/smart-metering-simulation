package udl.cig.sms.connection.factory;

import udl.cig.sms.connection.Datagrams;
import udl.cig.sms.connection.datagram.SMSDatagram;
import udl.cig.sms.data.LoadCurve;

import java.io.IOException;
import java.io.InputStream;

/**
 * Initialize the different constructors of the datagrams.
 */
public class FactoryConstructor {

    /**
     * @param loadcurve to get the information of the ECC
     * @return the array of FactorySMSDatagram that contains all the Factories involved in the project.
     */
    public static FactorySMSDatagram[] constructFactories(LoadCurve loadcurve) {
        FactorySMSDatagram[] factories = new FactorySMSDatagram[Datagrams.values().length];
        factories[Datagrams.CIPHER_TEXT_DATAGRAM.ordinal()] = new FactoryCipherTextDatagram(loadcurve);
        factories[Datagrams.END_OF_DATAGRAM.ordinal()] = new FactoryEndOfDatagram();
        factories[Datagrams.GROUP_ELEMENT_DATAGRAM.ordinal()] = new FactoryGroupElementDatagram(loadcurve);
        factories[Datagrams.NEIGHBORHOOD_DATAGRAM.ordinal()] = new FactoryNeighborhoodDatagram(loadcurve);
        factories[Datagrams.BIG_INTEGER_DATAGRAM.ordinal()] = new FactoryBigIntegerDatagram();
        return factories;
    }

    /**
     * @param in needed to read the bytes of the datagram
     * @param factories array that construct all the SMSDatagram needed
     * @return SMSDatagram built from the bytes of the input
     * @throws IOException in case that the reads more than it has to.
     */
    public static SMSDatagram buildDatagramFrom(InputStream in, FactorySMSDatagram[] factories) throws IOException {
        byte[] bytes = new byte[1];
        if(in.read(bytes) < bytes.length)
            throw new IOException();
        FactorySMSDatagram factory = factories[bytes[0]];
        bytes = new byte[factory.getByteSize()];
        if(in.read(bytes) < bytes.length)
            throw new IOException();
        return factory.buildDatagram(bytes);
    }
}
